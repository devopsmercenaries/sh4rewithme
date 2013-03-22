package me.sh4rewith.persistence.mongo.mappers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.sh4rewith.domain.SharedFileFootprint;
import me.sh4rewith.persistence.keys.SharedFileFootprintKeys;

import org.springframework.dao.DataAccessException;

import com.mongodb.DBObject;
import com.mongodb.MongoException;

public class SharedFileFootprintMapper extends AbstractMongoDocumentMapper {
	public static final String SHARED_FILE_FOOTPRINT_STORENAME =
			SharedFileFootprintKeys.SHARED_FILE_FOOTPRINT_STORENAME.keyName();
	public static final String SHARED_FILE_INFO_ID =
			SharedFileFootprintKeys.SHARED_FILE_INFO_ID.keyName();
	public static final String CREATION_DATE =
			SharedFileFootprintKeys.CREATION_DATE.keyName();
	public static final String RAW_FILE_ID =
			SharedFileFootprintKeys.RAW_FILE_ID.keyName();
	public static final String EXPIRATION_DATE =
			SharedFileFootprintKeys.EXPIRATION_DATE.keyName();

	List<SharedFileFootprint> footprints = new ArrayList<SharedFileFootprint>();

	@Override
	public void processDocument(DBObject dbObject) throws MongoException,
			DataAccessException {
		SharedFileFootprint footprint = new SharedFileFootprint.Builder()
				.setRawFileId((String) dbObject.get(RAW_FILE_ID))
				.setSharedFileInfoId((String) dbObject.get(SHARED_FILE_INFO_ID))
				.setCreationDate((Date) dbObject.get(CREATION_DATE))
				.setExpiration((Date) dbObject.get(EXPIRATION_DATE))
				.build();
		footprints.add(footprint);
	}

	public List<SharedFileFootprint> getFootprintList() {
		return footprints;
	}

	public SharedFileFootprint getFootprint() {
		return footprints.get(0);
	}
}
