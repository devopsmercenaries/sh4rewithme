package me.sh4rewith.persistence.mongo;

import static me.sh4rewith.persistence.mongo.mappers.AbstractMongoDocumentMapper.MONGO_DOC_ID;
import static me.sh4rewith.persistence.mongo.mappers.RawFileInfoMapper.CONTENT_TYPE;
import static me.sh4rewith.persistence.mongo.mappers.RawFileInfoMapper.INFO_SUFFIX;
import static me.sh4rewith.persistence.mongo.mappers.RawFileInfoMapper.ORIGINAL_FILE_NAME;
import static me.sh4rewith.persistence.mongo.mappers.RawFileInfoMapper.RAW_FILE_INFO_COLLECTION;
import static me.sh4rewith.persistence.mongo.mappers.RawFileInfoMapper.SIZE;
import static me.sh4rewith.persistence.mongo.mappers.RawFileMapper.BYTES;
import static me.sh4rewith.persistence.mongo.mappers.RawFileMapper.RAW_FILE_COLLECTION;
import static me.sh4rewith.persistence.mongo.mappers.SharedFileFootprintMapper.*;
import static me.sh4rewith.persistence.mongo.mappers.SharedFileInfoMapper.BUDDIES_LIST;
import static me.sh4rewith.persistence.mongo.mappers.SharedFileInfoMapper.DESCRIPTION;
import static me.sh4rewith.persistence.mongo.mappers.SharedFileInfoMapper.OWNER;
import static me.sh4rewith.persistence.mongo.mappers.SharedFileInfoMapper.PRIVACY_TYPE;
import static me.sh4rewith.persistence.mongo.mappers.SharedFileInfoMapper.SHARED_FILE_INFO_COLLECTION;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import me.sh4rewith.domain.Privacy;
import me.sh4rewith.domain.RawFile;
import me.sh4rewith.domain.RawFileInfo;
import me.sh4rewith.domain.SharedFile;
import me.sh4rewith.domain.SharedFileDescriptor;
import me.sh4rewith.domain.SharedFileFootprint;
import me.sh4rewith.domain.SharedFileInfo;
import me.sh4rewith.domain.Privacy.Type;
import me.sh4rewith.persistence.SharedFilesRepository;
import me.sh4rewith.persistence.mongo.mappers.RawFileInfoMapper;
import me.sh4rewith.persistence.mongo.mappers.RawFileMapper;
import me.sh4rewith.persistence.mongo.mappers.SharedFileFootprintMapper;
import me.sh4rewith.persistence.mongo.mappers.SharedFileInfoMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

@Service
public class MongoSharedFileRepository implements SharedFilesRepository {

	@Autowired
	MongoTemplate mongo;

	@Override
	public void store(SharedFile sharedFile) {
		saveRawFile(sharedFile.getRawFile());
		saveRawFileInfo(sharedFile.getRawFileInfo());
		saveSharedFileInfo(sharedFile.getInfo());
		saveFootprint(sharedFile.getFootprint());
	}

	@Override
	public List<SharedFile> findAll() {
		SharedFileFootprintMapper footprintMapper = new SharedFileFootprintMapper();
		mongo.executeQuery(new Query(Criteria.where(MONGO_DOC_ID).regex(".*")), SHARED_FILE_FOOTPRINT_COLLECTION, footprintMapper);
		List<SharedFileFootprint> footPrints = footprintMapper.getFootprintList();
		return Lists.transform(footPrints, new Function<SharedFileFootprint, SharedFile>() {
			@Override
			public SharedFile apply(@Nullable SharedFileFootprint footPrint) {
				if (footPrint == null)
					return null;
				SharedFile sharedFile = getSharedFileFromFootprint(footPrint);
				return sharedFile;
			}

		});
	}

	@Override
	public List<SharedFileDescriptor> findAllDescriptors() {
		SharedFileFootprintMapper footprintMapper = new SharedFileFootprintMapper();
		Query query = new Query(Criteria.where(MONGO_DOC_ID).regex(".*"));
		query.sort().on(CREATION_DATE, Order.DESCENDING);
		mongo.executeQuery(query, SHARED_FILE_FOOTPRINT_COLLECTION, footprintMapper);
		List<SharedFileFootprint> footPrints = footprintMapper.getFootprintList();
		return Lists.transform(footPrints, new Function<SharedFileFootprint, SharedFileDescriptor>() {
			@Override
			public SharedFileDescriptor apply(@Nullable SharedFileFootprint footPrint) {
				if (footPrint == null)
					return null;
				SharedFileDescriptor sharedFileDescriptor = getSharedFileDescriptorFromFootprint(footPrint);
				return sharedFileDescriptor;
			}

		});
	}

	@Override
	public SharedFile getSharedFileByFootprintId(String footprintId) {
		SharedFileFootprintMapper footprintMapper = new SharedFileFootprintMapper();
		mongo.executeQuery(new Query(Criteria.where(MONGO_DOC_ID).is(footprintId)), SHARED_FILE_FOOTPRINT_COLLECTION, footprintMapper);
		SharedFile doc = getSharedFileFromFootprint(footprintMapper.getFootprint());
		return doc;
	}

	@Override
	public void deleteByFootprintId(String footprint) {
		SharedFileFootprintMapper footprintMapper = new SharedFileFootprintMapper();
		final Query footprintQuery = new Query(Criteria.where(MONGO_DOC_ID).is(footprint));
		mongo.executeQuery(footprintQuery, SHARED_FILE_FOOTPRINT_COLLECTION, footprintMapper);
		SharedFileFootprint sharedFileFootprint = footprintMapper.getFootprint();
		final Query rawFileQuery = new Query(Criteria.where(MONGO_DOC_ID).is(sharedFileFootprint.getRawFileId()));
		final Query rawFileInfoQuery = new Query(Criteria.where(MONGO_DOC_ID).is(sharedFileFootprint.getRawFileId() + "-info"));
		final Query sharedFileInfoQuery = new Query(Criteria.where(MONGO_DOC_ID).is(sharedFileFootprint.getSharedFileInfoId()));
		mongo.remove(rawFileQuery, RAW_FILE_COLLECTION);
		mongo.remove(rawFileInfoQuery, RAW_FILE_INFO_COLLECTION);
		mongo.remove(sharedFileInfoQuery, SHARED_FILE_INFO_COLLECTION);
		mongo.remove(footprintQuery, SHARED_FILE_FOOTPRINT_COLLECTION);
	}

	@Override
	public List<SharedFileDescriptor> getPublicStream(String buddyId) {
		SharedFileInfoMapper sharedFileInfoMapper = new SharedFileInfoMapper();
		Query query = new Query(
				Criteria
						.where(MONGO_DOC_ID)
						.regex(".*")
						.and(PRIVACY_TYPE).is(Privacy.Type.PUBLIC.name())
				);
		mongo.executeQuery(query, SHARED_FILE_INFO_COLLECTION, sharedFileInfoMapper);
		List<SharedFileInfo> infoList = sharedFileInfoMapper.getSharedFileInfoList();
		return Lists.transform(infoList, new Function<SharedFileInfo, SharedFileDescriptor>() {
			@Override
			public SharedFileDescriptor apply(@Nullable SharedFileInfo sharedFileInfo) {
				if (sharedFileInfo == null)
					return null;
				SharedFileFootprintMapper footprintMapper = new SharedFileFootprintMapper();
				final Query footprintQuery = new Query(Criteria.where(SHARED_FILE_INFO_ID).is(sharedFileInfo.getId()));
				footprintQuery.sort().on(CREATION_DATE, Order.DESCENDING);
				mongo.executeQuery(footprintQuery, SHARED_FILE_FOOTPRINT_COLLECTION, footprintMapper);
				SharedFileDescriptor sharedFileDescriptor = getSharedFileDescriptorFromFootprint(footprintMapper.getFootprint());
				return sharedFileDescriptor;
			}
		});
	}

	@Override
	public List<SharedFileDescriptor> getPersonalStream(String buddyId) {
		SharedFileInfoMapper sharedFileInfoMapper = new SharedFileInfoMapper();
		Query query = new Query(new Criteria().andOperator(
				Criteria
						.where(MONGO_DOC_ID)
						.regex(".*")
						.and(PRIVACY_TYPE).ne(Privacy.Type.PUBLIC.name()),
				new Criteria().orOperator(
						Criteria.where(OWNER).is(buddyId),
						Criteria.where(BUDDIES_LIST).is(buddyId)))
				);
		mongo.executeQuery(query, SHARED_FILE_INFO_COLLECTION, sharedFileInfoMapper);
		List<SharedFileInfo> infoList = sharedFileInfoMapper.getSharedFileInfoList();
		return Lists.transform(infoList, new Function<SharedFileInfo, SharedFileDescriptor>() {
			@Override
			public SharedFileDescriptor apply(@Nullable SharedFileInfo sharedFileInfo) {
				if (sharedFileInfo == null)
					return null;
				SharedFileFootprintMapper footprintMapper = new SharedFileFootprintMapper();
				final Query footprintQuery = new Query(Criteria.where(SHARED_FILE_INFO_ID).is(sharedFileInfo.getId()));
				footprintQuery.sort().on(CREATION_DATE, Order.DESCENDING);
				mongo.executeQuery(footprintQuery, SHARED_FILE_FOOTPRINT_COLLECTION, footprintMapper);
				SharedFileDescriptor sharedFileDescriptor = getSharedFileDescriptorFromFootprint(footprintMapper.getFootprint());
				return sharedFileDescriptor;
			}
		});
	}

	@Override
	public SharedFileInfo findSharedFileInfoById(String sharedFileInfoId) {
		SharedFileInfoMapper mapper = new SharedFileInfoMapper();
		mongo.executeQuery(new Query(Criteria.where(MONGO_DOC_ID).is(sharedFileInfoId)), SHARED_FILE_INFO_COLLECTION, mapper);
		return mapper.getSharedFileInfo();
	}

	@Override
	public void turnPublic(SharedFileInfo sharedFileInfo) {
		turn(Privacy.Type.PUBLIC, sharedFileInfo);
	}

	@Override
	public void turnPrivate(SharedFileInfo sharedFileInfo) {
		turn(Privacy.Type.PRIVATE, sharedFileInfo);
	}

	@Override
	public void turnProtected(SharedFileInfo sharedFileInfo) {
		turn(Privacy.Type.PROTECTED, sharedFileInfo);
	}

	@Override
	public SharedFileInfo findSharedFileInfoByFootprintId(String sharedFileFootprintId) {
		SharedFileFootprintMapper footprintMapper = new SharedFileFootprintMapper();
		final Query footprintQuery = new Query(Criteria.where(MONGO_DOC_ID).is(sharedFileFootprintId));
		mongo.executeQuery(footprintQuery, SHARED_FILE_FOOTPRINT_COLLECTION, footprintMapper);
		SharedFileFootprint sharedFileFootprint = footprintMapper.getFootprint();
		return getSharedFileDescriptorFromFootprint(sharedFileFootprint).getInfo();
	}

	@Override
	public Long countAll() {
		return mongo.count(new Query(Criteria.where(MONGO_DOC_ID).regex(".*")), SHARED_FILE_FOOTPRINT_COLLECTION);
	}

	private void saveFootprint(SharedFileFootprint footprint) {
		final Query footPrintQuery = new Query(Criteria.where(MONGO_DOC_ID).is(footprint.getId()));
		mongo.upsert(footPrintQuery, new Update().set(MONGO_DOC_ID, footprint.getId()), SHARED_FILE_FOOTPRINT_COLLECTION);
		mongo.upsert(footPrintQuery, new Update().set(SharedFileFootprintMapper.RAW_FILE_ID, footprint.getRawFileId()), SHARED_FILE_FOOTPRINT_COLLECTION);
		mongo.upsert(footPrintQuery, new Update().set(SHARED_FILE_INFO_ID, footprint.getSharedFileInfoId()), SHARED_FILE_FOOTPRINT_COLLECTION);
		mongo.upsert(footPrintQuery, new Update().set(SharedFileFootprintMapper.CREATION_DATE, footprint.getCreationDate()), SHARED_FILE_FOOTPRINT_COLLECTION);
		mongo.upsert(footPrintQuery, new Update().set(SharedFileFootprintMapper.EXPIRATION_DATE, footprint.getExpirationDate()), SHARED_FILE_FOOTPRINT_COLLECTION);
	}

	private void saveSharedFileInfo(SharedFileInfo sharedFileInfo) {
		final Query metadataQuery = new Query(Criteria.where(MONGO_DOC_ID).is(sharedFileInfo.getId()));
		mongo.upsert(metadataQuery, new Update().set(MONGO_DOC_ID, sharedFileInfo.getId()), SHARED_FILE_INFO_COLLECTION);
		mongo.upsert(metadataQuery, new Update().set(CREATION_DATE, sharedFileInfo.getCreationDate()), SHARED_FILE_INFO_COLLECTION);
		mongo.upsert(metadataQuery, new Update().set(EXPIRATION_DATE, sharedFileInfo.getExpirationDate()), SHARED_FILE_INFO_COLLECTION);
		mongo.upsert(metadataQuery, new Update().set(DESCRIPTION, sharedFileInfo.getDescription()), SHARED_FILE_INFO_COLLECTION);
		mongo.upsert(metadataQuery, new Update().set(OWNER, sharedFileInfo.getOwner()), SHARED_FILE_INFO_COLLECTION);
		mongo.upsert(metadataQuery, new Update().set(PRIVACY_TYPE, sharedFileInfo.getPrivacy().getType()), SHARED_FILE_INFO_COLLECTION);
		if (sharedFileInfo.getPrivacy().getType() == Privacy.Type.PRIVATE) {
			mongo.upsert(metadataQuery, new Update().set(BUDDIES_LIST, Arrays.asList(sharedFileInfo.getPrivacy().getBuddies().toArray())), SHARED_FILE_INFO_COLLECTION);
		}
	}

	private void saveRawFile(RawFile rawFile) {
		final Query contentQuery = new Query(Criteria.where(MONGO_DOC_ID).is(rawFile.getId()));
		mongo.upsert(contentQuery, new Update().set(MONGO_DOC_ID, rawFile.getId()), RAW_FILE_COLLECTION);
		mongo.upsert(contentQuery, new Update().set(BYTES, rawFile.getBytes()), RAW_FILE_COLLECTION);
	}

	private void saveRawFileInfo(RawFileInfo rawFileInfo) {
		final Query contentQuery = new Query(Criteria.where(MONGO_DOC_ID).is(rawFileInfo.getRawFileId() + INFO_SUFFIX));
		mongo.upsert(contentQuery, new Update().set(CONTENT_TYPE, rawFileInfo.getContentType()), RAW_FILE_INFO_COLLECTION);
		mongo.upsert(contentQuery, new Update().set(ORIGINAL_FILE_NAME, rawFileInfo.getOriginalFileName()), RAW_FILE_INFO_COLLECTION);
		mongo.upsert(contentQuery, new Update().set(SIZE, rawFileInfo.getSize()), RAW_FILE_INFO_COLLECTION);
		mongo.upsert(contentQuery, new Update().set(RAW_FILE_ID, rawFileInfo.getRawFileId()), RAW_FILE_INFO_COLLECTION);
	}

	private SharedFile getSharedFileFromFootprint(SharedFileFootprint footPrint) {
		RawFileInfoMapper rawFileInfoMapper = new RawFileInfoMapper();
		mongo.executeQuery(new Query(Criteria.where(MONGO_DOC_ID).is(footPrint.getRawFileId() + INFO_SUFFIX)), RAW_FILE_INFO_COLLECTION, rawFileInfoMapper);
		RawFileMapper rawFileMapper = new RawFileMapper();
		mongo.executeQuery(new Query(Criteria.where(MONGO_DOC_ID).is(footPrint.getRawFileId())), RAW_FILE_COLLECTION, rawFileMapper);
		SharedFileInfoMapper sharedFileInfoMapper = new SharedFileInfoMapper();
		mongo.executeQuery(new Query(Criteria.where(MONGO_DOC_ID).is(footPrint.getSharedFileInfoId())), SHARED_FILE_INFO_COLLECTION, sharedFileInfoMapper);
		SharedFile rawFile = new SharedFile.Builder()
				.setRawFile(rawFileMapper.getRawFile())
				.setRawFileInfo(rawFileInfoMapper.getRawFileInfo())
				.setSharedFileInfo(sharedFileInfoMapper.getSharedFileInfo())
				.build();
		return rawFile;
	}

	private SharedFileDescriptor getSharedFileDescriptorFromFootprint(SharedFileFootprint footPrint) {
		RawFileInfoMapper rawFileInfoMapper = new RawFileInfoMapper();
		mongo.executeQuery(new Query(Criteria.where(MONGO_DOC_ID).is(footPrint.getRawFileId() + INFO_SUFFIX)), RAW_FILE_INFO_COLLECTION, rawFileInfoMapper);
		SharedFileInfoMapper sharedFileInfoMapper = new SharedFileInfoMapper();
		mongo.executeQuery(new Query(Criteria.where(MONGO_DOC_ID).is(footPrint.getSharedFileInfoId())), SHARED_FILE_INFO_COLLECTION, sharedFileInfoMapper);
		SharedFileDescriptor doc = new SharedFileDescriptor.Builder()
				.setRawFileInfo(rawFileInfoMapper.getRawFileInfo())
				.setSharedFileInfo(sharedFileInfoMapper.getSharedFileInfo())
				.build();
		return doc;
	}

	private void turn(Type privacyType, SharedFileInfo sharedFileInfo) {
		final Query metadataQuery = new Query(Criteria.where(MONGO_DOC_ID).is(sharedFileInfo.getId()));
		mongo.upsert(metadataQuery, new Update().set(PRIVACY_TYPE, privacyType), SHARED_FILE_INFO_COLLECTION);
		if (privacyType == Privacy.Type.PRIVATE) {
			mongo.upsert(metadataQuery, new Update().set(BUDDIES_LIST, Arrays.asList(sharedFileInfo.getPrivacy().getBuddies().toArray())), SHARED_FILE_INFO_COLLECTION);
		}
	}

	@Override
	public Integer deleteOlderThan(Date expirationDate) {
		SharedFileFootprintMapper footprintMapper = new SharedFileFootprintMapper();
		final Query footprintQuery = new Query(Criteria.where(EXPIRATION_DATE).lt(expirationDate));
		mongo.executeQuery(footprintQuery, SHARED_FILE_FOOTPRINT_COLLECTION, footprintMapper);

		// SharedFileFootprint sharedFileFootprint = footprintMapper.getFootprint();
		List<SharedFileFootprint> shareFilesToBeDeleted = footprintMapper.getFootprintList();
		for (SharedFileFootprint cur : shareFilesToBeDeleted) {
			final Query rawFileQuery = new Query(Criteria.where(MONGO_DOC_ID).is(cur.getRawFileId()));
			final Query rawFileInfoQuery = new Query(Criteria.where(MONGO_DOC_ID).is(cur.getRawFileId() + "-info"));
			final Query sharedFileInfoQuery = new Query(Criteria.where(MONGO_DOC_ID).is(cur.getSharedFileInfoId()));
			mongo.remove(rawFileQuery, RAW_FILE_COLLECTION);
			mongo.remove(rawFileInfoQuery, RAW_FILE_INFO_COLLECTION);
			mongo.remove(sharedFileInfoQuery, SHARED_FILE_INFO_COLLECTION);
			mongo.remove(footprintQuery, SHARED_FILE_FOOTPRINT_COLLECTION);
		}

		return shareFilesToBeDeleted.size();
	}

	@Override
	public void appendBuddyToSharedFileInfoById(String sharedFileInfoId, String buddy) {
		final Query metadataQuery = new Query(Criteria.where(MONGO_DOC_ID).is(sharedFileInfoId));
		mongo.upsert(metadataQuery, new Update().addToSet(BUDDIES_LIST, buddy), SHARED_FILE_INFO_COLLECTION);
	}
}
