package me.sh4rewith.persistence.elasticsearch;

import static me.sh4rewith.persistence.keys.RawFileInfoKeys.CONTENT_TYPE;
import static me.sh4rewith.persistence.keys.RawFileInfoKeys.ORIGINAL_FILE_NAME;
import static me.sh4rewith.persistence.keys.RawFileInfoKeys.RAW_FILE_ID;
import static me.sh4rewith.persistence.keys.RawFileInfoKeys.RAW_FILE_INFO_STORENAME;
import static me.sh4rewith.persistence.keys.RawFileInfoKeys.SIZE;
import static me.sh4rewith.persistence.keys.RawFileKeys.RAW_FILE_STORENAME;
import static me.sh4rewith.persistence.keys.RawFileKeys.STORAGE_COORDINATES;
import static me.sh4rewith.persistence.keys.SharedFileFootprintKeys.SHARED_FILE_FOOTPRINT_STORENAME;
import static me.sh4rewith.persistence.keys.SharedFileFootprintKeys.SHARED_FILE_INFO_ID;
import static me.sh4rewith.persistence.keys.SharedFileInfoKeys.BUDDIES_LIST;
import static me.sh4rewith.persistence.keys.SharedFileInfoKeys.CREATION_DATE;
import static me.sh4rewith.persistence.keys.SharedFileInfoKeys.DESCRIPTION;
import static me.sh4rewith.persistence.keys.SharedFileInfoKeys.EXPIRATION_DATE;
import static me.sh4rewith.persistence.keys.SharedFileInfoKeys.OWNER;
import static me.sh4rewith.persistence.keys.SharedFileInfoKeys.PRIVACY_TYPE;
import static me.sh4rewith.persistence.keys.SharedFileInfoKeys.SHARED_FILE_INFO_STORENAME;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.sh4rewith.domain.Privacy;
import me.sh4rewith.domain.Privacy.Type;
import me.sh4rewith.domain.RawFile;
import me.sh4rewith.domain.RawFileInfo;
import me.sh4rewith.domain.SharedFile;
import me.sh4rewith.domain.SharedFileDescriptor;
import me.sh4rewith.domain.SharedFileFootprint;
import me.sh4rewith.domain.SharedFileInfo;
import me.sh4rewith.persistence.SharedFilesRepository;
import me.sh4rewith.persistence.keys.RawFileInfoKeys;
import me.sh4rewith.persistence.keys.RawFileKeys;
import me.sh4rewith.persistence.keys.SharedFileFootprintKeys;
import me.sh4rewith.persistence.keys.SharedFileInfoKeys;
import me.sh4rewith.utils.persistence.ElasticSearchResponseWrapper;
import me.sh4rewith.utils.persistence.ElasticSearchUtils;
import me.sh4rewith.utils.persistence.StorageUtils;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.OrFilterBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("elasticsearch")
public class ElasticSearchSharedFileRepository implements SharedFilesRepository {

	@Autowired
	private Client esClient;

	@Override
	public void store(SharedFile sharedFile) {
		saveRawFile(sharedFile.getRawFile());
		saveRawFileInfo(sharedFile.getRawFileInfo());
		saveSharedFileInfo(sharedFile.getInfo());
		saveFootprint(sharedFile.getFootprint());
	}

	private void saveFootprint(SharedFileFootprint footprint) {
		try {
			XContentBuilder source = XContentFactory
					.jsonBuilder()
					.startObject()
					.field(SHARED_FILE_INFO_ID.keyName(),
							footprint.getSharedFileInfoId())
					.field(RAW_FILE_ID.keyName(), footprint.getRawFileId())
					.field(CREATION_DATE.keyName(), footprint.getCreationDate())
					.field(EXPIRATION_DATE.keyName(),
							footprint.getCreationDate()).endObject();
			esClient.prepareIndex(ElasticSearchUtils.GLOBAL_INDEX,
					SHARED_FILE_FOOTPRINT_STORENAME.keyName(),
					footprint.getId()).setSource(source).execute().actionGet();
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while storing Shared File footprint", e);
		}
	}

	private void saveSharedFileInfo(SharedFileInfo info) {
		try {
			XContentBuilder source = XContentFactory
					.jsonBuilder()
					.startObject()
					.field(OWNER.keyName(), info.getOwner())
					.field(DESCRIPTION.keyName(), info.getDescription())
					.field(CREATION_DATE.keyName(), info.getCreationDate())
					.field(EXPIRATION_DATE.keyName(), info.getCreationDate())
					.field(PRIVACY_TYPE.keyName(),
							info.getPrivacy().getType().name())
					.array(BUDDIES_LIST.keyName(),
							info.getPrivacy().getBuddies().toArray())
					.endObject();
			esClient.prepareIndex(ElasticSearchUtils.GLOBAL_INDEX,
					SHARED_FILE_INFO_STORENAME.keyName(), info.getId())
					.setSource(source).execute().actionGet();
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while storing Shared File information",
					e);
		}
	}

	private void saveRawFileInfo(RawFileInfo rawFileInfo) {
		try {
			XContentBuilder source = XContentFactory
					.jsonBuilder()
					.startObject()
					.field(SIZE.keyName(), rawFileInfo.getSize())
					.field(CONTENT_TYPE.keyName(), rawFileInfo.getContentType())
					.field(ORIGINAL_FILE_NAME.keyName(),
							rawFileInfo.getOriginalFileName())
					.field(RAW_FILE_ID.keyName(), rawFileInfo.getRawFileId())
					.endObject();
			esClient.prepareIndex(ElasticSearchUtils.GLOBAL_INDEX,
					RAW_FILE_INFO_STORENAME.keyName(), rawFileInfo.getId())
					.setSource(source).execute().actionGet();
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while storing Raw File information", e);
		}
	}

	private void saveRawFile(RawFile rawFile) {
		try {
			XContentBuilder source = XContentFactory
					.jsonBuilder()
					.startObject()
					.field(STORAGE_COORDINATES.keyName(),
							StorageUtils.serializeCoordinates(rawFile
									.getStorageCoordinates())).endObject();
			esClient.prepareIndex(ElasticSearchUtils.GLOBAL_INDEX,
					RAW_FILE_STORENAME.keyName(), rawFile.getId())
					.setSource(source).execute().actionGet();
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while storing Raw File", e);
		}
	}

	@Override
	public List<SharedFile> findAll() {
		List<SharedFile> result = new ArrayList<SharedFile>();
		try {
			SearchResponse response = esClient
					.prepareSearch(ElasticSearchUtils.GLOBAL_INDEX)
					.setTypes(SHARED_FILE_FOOTPRINT_STORENAME.keyName())
					.addFields(SharedFileFootprintKeys.keyNamesArray())
					.execute().actionGet();
			SearchHit[] searchHits = response.getHits().getHits();
			for (SearchHit searchHit : searchHits) {
				SharedFileFootprint footprint = mapSharedFileFootprint(new ElasticSearchResponseWrapper(
						searchHit));
				result.add(getSharedFileFromFootprint(footprint));
			}
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while storing User information", e);
		}
		return result;
	}

	@Override
	public SharedFile getSharedFileByFootprintId(String footprintId) {
		SharedFile result = null;
		try {
			SharedFileFootprint footprint = getSharedFileFootprintById(footprintId);
			result = getSharedFileFromFootprint(footprint);
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while storing User information", e);
		}
		return result;
	}

	private SharedFile getSharedFileFromFootprint(SharedFileFootprint footprint) {
		SharedFile sharedFile = new SharedFile.Builder()
				.setRawFile(getRawFileById(footprint.getRawFileId()))
				.setRawFileInfo(
						getRawFileInfoByRawFileId(footprint.getRawFileId()))
				.setSharedFileInfo(
						getSharedFileInfoById(footprint.getSharedFileInfoId()))
				.build();
		return sharedFile;
	}

	private SharedFileInfo getSharedFileInfoById(String sharedFileInfoId) {
		SharedFileInfo result = null;
		try {
			GetResponse response = esClient
					.prepareGet(ElasticSearchUtils.GLOBAL_INDEX,
							SHARED_FILE_INFO_STORENAME.keyName(),
							sharedFileInfoId)
					.setFields(SharedFileInfoKeys.keyNamesArray()).execute()
					.actionGet();
			result = mapSharedFileInfo(new ElasticSearchResponseWrapper(
					response));
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while getting Shared File Info by Id", e);
		}
		return result;
	}

	private RawFileInfo getRawFileInfoByRawFileId(String rawFileId) {
		RawFileInfo result = null;
		try {
			SearchResponse response = esClient
					.prepareSearch(ElasticSearchUtils.GLOBAL_INDEX)
					.setTypes(RAW_FILE_INFO_STORENAME.keyName())
					.addFields(RawFileInfoKeys.keyNamesArray())
					.setQuery(
							QueryBuilders.fieldQuery(RAW_FILE_ID.keyName(),
									rawFileId)).execute().actionGet();
			SearchHit searchHit = response.getHits().getHits()[0];
			result = mapRawFileInfo(new ElasticSearchResponseWrapper(searchHit));
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while storing User information", e);
		}
		return result;
	}

	private RawFile getRawFileById(String rawFileId) {
		RawFile result = null;
		try {
			GetResponse response = esClient
					.prepareGet(ElasticSearchUtils.GLOBAL_INDEX,
							RAW_FILE_STORENAME.keyName(), rawFileId)
					.setFields(RawFileKeys.keyNamesArray()).execute()
					.actionGet();
			result = mapRawFile(new ElasticSearchResponseWrapper(response));
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while storing User information", e);
		}
		return result;
	}

	@Override
	public List<SharedFileDescriptor> findAllDescriptors() {
		List<SharedFileDescriptor> result = new ArrayList<SharedFileDescriptor>();
		try {
			SearchResponse response = esClient
					.prepareSearch(ElasticSearchUtils.GLOBAL_INDEX)
					.setTypes(SHARED_FILE_FOOTPRINT_STORENAME.keyName())
					.addFields(SharedFileFootprintKeys.keyNamesArray())
					.execute().actionGet();
			SearchHit[] searchHits = response.getHits().getHits();
			for (SearchHit searchHit : searchHits) {
				SharedFileFootprint footprint = mapSharedFileFootprint(new ElasticSearchResponseWrapper(
						searchHit));
				result.add(getSharedFileDescriptorFromFootprint(footprint));
			}
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while storing User information", e);
		}
		return result;
	}

	private SharedFileDescriptor getSharedFileDescriptorFromFootprint(
			SharedFileFootprint footprint) {
		SharedFileDescriptor descriptor = new SharedFileDescriptor.Builder()
				.setRawFileInfo(
						getRawFileInfoByRawFileId(footprint.getRawFileId()))
				.setSharedFileInfo(
						getSharedFileInfoById(footprint.getSharedFileInfoId()))
				.build();
		return descriptor;
	}

	@Override
	public void deleteByFootprintId(String footprintId) {
		SharedFileFootprint footprint = getSharedFileFootprintById(footprintId);
		try {
			esClient.prepareDelete(ElasticSearchUtils.GLOBAL_INDEX,
					RAW_FILE_STORENAME.keyName(), footprint.getRawFileId())
					.execute().actionGet();
			esClient.prepareDelete(ElasticSearchUtils.GLOBAL_INDEX,
					RAW_FILE_INFO_STORENAME.keyName(),
					footprint.getRawFileId() + "-info").execute().actionGet();
			esClient.prepareDelete(ElasticSearchUtils.GLOBAL_INDEX,
					SHARED_FILE_INFO_STORENAME.keyName(),
					footprint.getSharedFileInfoId()).execute().actionGet();
			esClient.prepareDelete(ElasticSearchUtils.GLOBAL_INDEX,
					SHARED_FILE_FOOTPRINT_STORENAME.keyName(), footprintId)
					.execute().actionGet();
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while deleting by footprint Id", e);
		}
	}

	private SharedFileFootprint getSharedFileFootprintById(String footprintId) {
		GetResponse response = esClient
				.prepareGet(ElasticSearchUtils.GLOBAL_INDEX,
						SHARED_FILE_FOOTPRINT_STORENAME.keyName(), footprintId)
				.setFields(SharedFileFootprintKeys.keyNamesArray()).execute()
				.actionGet();
		SharedFileFootprint footprint = mapSharedFileFootprint(new ElasticSearchResponseWrapper(
				response));
		return footprint;
	}

	@Override
	public List<SharedFileDescriptor> getPublicStream(String buddyId) {
		List<SharedFileDescriptor> result = new ArrayList<SharedFileDescriptor>();
		try {
			SearchResponse response = esClient
					.prepareSearch(ElasticSearchUtils.GLOBAL_INDEX)
					.setTypes(SHARED_FILE_INFO_STORENAME.keyName())
					.setQuery(
							QueryBuilders.fieldQuery(PRIVACY_TYPE.keyName(),
									Privacy.Type.PUBLIC.name())).execute()
					.actionGet();
			SearchHit[] searchHits = response.getHits().getHits();
			for (SearchHit searchHit : searchHits) {
				SharedFileFootprint footprint = getSharedFileFootprintBySharedFileInfoId(searchHit
						.id());
				result.add(getSharedFileDescriptorFromFootprint(footprint));
			}
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while storing User information", e);
		}
		return result;
	}

	@Override
	public List<SharedFileDescriptor> getPersonalStream(String buddyId) {
		List<SharedFileDescriptor> result = new ArrayList<SharedFileDescriptor>();
		try {
			OrFilterBuilder filterOwnedOrBuddied = FilterBuilders.orFilter(
					FilterBuilders.termFilter(OWNER.keyName(), buddyId),
					FilterBuilders.termFilter(BUDDIES_LIST.keyName(), buddyId));
			BoolQueryBuilder notPublicQuery = QueryBuilders.boolQuery()
					.mustNot(
							QueryBuilders.fieldQuery(PRIVACY_TYPE.keyName(),
									Privacy.Type.PUBLIC.name()));
			FilteredQueryBuilder query = QueryBuilders.filteredQuery(
					notPublicQuery, filterOwnedOrBuddied);

			SearchResponse response = esClient
					.prepareSearch(ElasticSearchUtils.GLOBAL_INDEX)
					.setTypes(SHARED_FILE_INFO_STORENAME.keyName())
					.setQuery(query).execute().actionGet();
			SearchHit[] searchHits = response.getHits().getHits();
			for (SearchHit searchHit : searchHits) {
				SharedFileFootprint footprint = getSharedFileFootprintBySharedFileInfoId(searchHit
						.id());
				result.add(getSharedFileDescriptorFromFootprint(footprint));
			}
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while getting Personal Stream", e);
		}
		return result;
	}

	private SharedFileFootprint getSharedFileFootprintBySharedFileInfoId(
			String id) {
		MatchQueryBuilder matchQuery = QueryBuilders.matchPhraseQuery(
				SHARED_FILE_INFO_ID.keyName(), id);
		SearchResponse response = esClient
				.prepareSearch(ElasticSearchUtils.GLOBAL_INDEX)
				.setTypes(SHARED_FILE_FOOTPRINT_STORENAME.keyName())
				.setQuery(matchQuery)
				.addFields(SharedFileFootprintKeys.keyNamesArray()).execute()
				.actionGet();
		SearchHit searchHit = response.getHits().getHits()[0];
		SharedFileFootprint footprint = mapSharedFileFootprint(new ElasticSearchResponseWrapper(
				searchHit));
		return footprint;

	}

	@Override
	public SharedFileInfo findSharedFileInfoById(String sharedFileInfoId) {
		return getSharedFileInfoById(sharedFileInfoId);
	}

	@Override
	public void turnPublic(SharedFileInfo sharedFileInfo) {
		updatePrivacy(sharedFileInfo, Privacy.Type.PUBLIC);
	}

	@Override
	public void turnPrivate(SharedFileInfo sharedFileInfo) {
		updatePrivacy(sharedFileInfo, Privacy.Type.PRIVATE);
	}

	@Override
	public void turnProtected(SharedFileInfo sharedFileInfo) {
		updatePrivacy(sharedFileInfo, Privacy.Type.PROTECTED);

	}

	private void updatePrivacy(SharedFileInfo sharedFileInfo, Type privacyType) {
		try {
			esClient.prepareUpdate().setIndex(ElasticSearchUtils.GLOBAL_INDEX)
					.setType(SHARED_FILE_INFO_STORENAME.keyName())
					.setId(sharedFileInfo.getId())
					.setScript("ctx._source."+PRIVACY_TYPE.keyName()+" = \""+privacyType.name()+"\"").execute()
					.actionGet();
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while updating Privacy", e);
		}
	}

	@Override
	public SharedFileInfo findSharedFileInfoByFootprintId(
			String sharedFileFootprintId) {
		SharedFileInfo result = null;
		SharedFileFootprint sharedFileFootprintById = getSharedFileFootprintById(sharedFileFootprintId);
		try {
			result = getSharedFileInfoById(sharedFileFootprintById
					.getSharedFileInfoId());
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while finding SharedFileInfo by Footprint Id",
					e);
		}
		return result;
	}

	@Override
	public Long countAll() {
		CountResponse response = esClient
				.prepareCount(ElasticSearchUtils.GLOBAL_INDEX)
				.setTypes(SHARED_FILE_FOOTPRINT_STORENAME.keyName()).execute()
				.actionGet();
		return response.count();
	}

	@Override
	public Integer deleteOlderThan(Date expirationDate) {
		List<SharedFileFootprint> footprints = getSharedFileFootprintOlderThan(expirationDate);
		for (SharedFileFootprint footprint : footprints) {
			deleteByFootprintId(footprint.getId());
		}
		return footprints.size();
	}

	private List<SharedFileFootprint> getSharedFileFootprintOlderThan(
			Date expirationDate) {
		List<SharedFileFootprint> result = new ArrayList<SharedFileFootprint>();
		SearchResponse response = esClient
				.prepareSearch(ElasticSearchUtils.GLOBAL_INDEX)
				.setTypes(SHARED_FILE_FOOTPRINT_STORENAME.keyName())
				.addFields(SharedFileFootprintKeys.keyNamesArray())
				.setQuery(
						QueryBuilders.rangeQuery(EXPIRATION_DATE.keyName()).gt(
								expirationDate)).execute().actionGet();
		SearchHit[] searchHits = response.getHits().getHits();
		for (SearchHit searchHit : searchHits) {
			SharedFileFootprint footprint = mapSharedFileFootprint(new ElasticSearchResponseWrapper(
					searchHit));
			result.add(footprint);
		}
		return result;
	}

	@Override
	public void appendBuddyToSharedFileInfoById(String sharedFileInfoId,
			String buddy) {
		SharedFileInfo sharedFileInfo = getSharedFileInfoById(sharedFileInfoId);
		Set<String> newBuddies = new HashSet<String>(sharedFileInfo
				.getPrivacy().getBuddies());
		newBuddies.add(buddy);
		try {
			XContentBuilder source = XContentFactory.jsonBuilder()
					.startObject()
					.array(BUDDIES_LIST.keyName(), newBuddies.toArray())
					.endObject();
			esClient.prepareUpdate().setIndex(ElasticSearchUtils.GLOBAL_INDEX)
					.setType(SHARED_FILE_INFO_STORENAME.keyName())
					.setId(sharedFileInfo.getId()).setUpsert(source).execute()
					.actionGet();
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while updating Privacy", e);
		}
	}

	private static RawFile mapRawFile(ElasticSearchResponseWrapper wrapper) {
		return new RawFile.Builder()
				.setId(wrapper.getId())
				.setStorageCoordinates(
						StorageUtils.deserializeCoordinates(wrapper
								.getStringField(STORAGE_COORDINATES))).build();
	}

	private static SharedFileFootprint mapSharedFileFootprint(
			ElasticSearchResponseWrapper wrapper) {
		return new SharedFileFootprint.Builder()
				.setCreationDate(wrapper.getDateField(CREATION_DATE))
				.setExpiration(wrapper.getDateField(EXPIRATION_DATE))
				.setRawFileId(wrapper.getStringField(RAW_FILE_ID))
				.setSharedFileInfoId(
						wrapper.getStringField(SHARED_FILE_INFO_ID)).build();
	}

	private static RawFileInfo mapRawFileInfo(
			ElasticSearchResponseWrapper wrapper) {
		return new RawFileInfo.Builder()
				.setId(wrapper.getId())
				.setSize(wrapper.getLongField(SIZE))
				.setContentType(wrapper.getStringField(CONTENT_TYPE))
				.setOriginalFileName(wrapper.getStringField(ORIGINAL_FILE_NAME))
				.setRawFileId(wrapper.getStringField(RAW_FILE_ID)).build();
	}

	private static SharedFileInfo mapSharedFileInfo(
			ElasticSearchResponseWrapper responseWrapper) {
		Privacy.Type privacyType = Privacy.Type.valueOf(responseWrapper
				.getStringField(PRIVACY_TYPE));
		Privacy.Builder privacyBuilder = new Privacy.Builder(privacyType);
		if (privacyType == Privacy.Type.PRIVATE) {
			List<String> buddies = responseWrapper
					.getListOfStringsField(BUDDIES_LIST);
			for (String buddyId : buddies) {
				privacyBuilder.shareWithBuddy(buddyId);
			}
		}
		SharedFileInfo sharedFileInfo = new SharedFileInfo.Builder()
				.setId(responseWrapper.getId())
				.setOwner(responseWrapper.getStringField(OWNER))
				.setDescription(responseWrapper.getStringField(DESCRIPTION))
				.setCreationDate(responseWrapper.getDateField(CREATION_DATE))
				.setExpirationDate(
						responseWrapper.getDateField(EXPIRATION_DATE))
				.setPrivacy(privacyBuilder.build()).build();
		return sharedFileInfo;
	}

}
