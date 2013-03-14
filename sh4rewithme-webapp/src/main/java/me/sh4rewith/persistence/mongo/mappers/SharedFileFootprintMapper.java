package me.sh4rewith.persistence.mongo.mappers;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.sh4rewith.domain.SharedFileFootprint;

import org.springframework.dao.DataAccessException;

import com.mongodb.DBObject;
import com.mongodb.MongoException;

public class SharedFileFootprintMapper extends AbstractMongoDocumentMapper {
	public static final String SHARED_FILE_FOOTPRINT_COLLECTION = "shared_file_footprint";

	public static final String SHARED_FILE_INFO_ID = "shared_file_info_id";

	public static final String CREATION_DATE = "creation_date";

	public static final String RAW_FILE_ID = "raw_file_id";

    public static final String EXPIRATION_DATE = "expiration_date";

	List<SharedFileFootprint> footprints = new ArrayList<SharedFileFootprint>();

    @Override
	public void processDocument(DBObject dbObject) throws MongoException, DataAccessException {
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
