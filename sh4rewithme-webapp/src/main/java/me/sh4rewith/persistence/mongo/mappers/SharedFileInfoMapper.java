package me.sh4rewith.persistence.mongo.mappers;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.sh4rewith.domain.Privacy;
import me.sh4rewith.domain.SharedFileInfo;

import org.springframework.dao.DataAccessException;

import com.mongodb.DBObject;
import com.mongodb.MongoException;

public class SharedFileInfoMapper extends AbstractMongoDocumentMapper {
	public static final String SHARED_FILE_INFO_COLLECTION = "shared_file_info";
	
	public static final String OWNER = "owner";
	public static final String DESCRIPTION = "description";
	public static final String CREATION_DATE = "creation_date";
	public static final String PRIVACY_TYPE = "privacy_type";
	public static final String BUDDIES_LIST = "buddies_list";
    public static final String EXPIRATION_DATE = "expiration_date";


    List<SharedFileInfo> metadataList = new ArrayList<SharedFileInfo>();

	@Override
	public void processDocument(DBObject dbObject) throws MongoException, DataAccessException {
		Privacy.Type privacyType = Privacy.Type.valueOf((String) dbObject.get(PRIVACY_TYPE));
		Privacy.Builder privacyBuilder = new Privacy.Builder(privacyType);
		if (privacyType == Privacy.Type.PRIVATE) {
			@SuppressWarnings("unchecked")
			List<String> buddies = (List<String>) dbObject.get("buddies_list");
			for (String buddyId : buddies) {
				privacyBuilder.shareWithBuddy(buddyId);
			}
		}
		SharedFileInfo metadata = new SharedFileInfo.Builder()
				.setId((String) dbObject.get(MONGO_DOC_ID))
				.setCreationDate((Date) dbObject.get(CREATION_DATE))
				.setDescription((String) dbObject.get(DESCRIPTION))
				.setOwner((String) dbObject.get(OWNER))
				.setPrivacy(privacyBuilder.build())
                .setExpirationDate((Date) dbObject.get(EXPIRATION_DATE))
                .build();
		metadataList.add(metadata);
	}

	public List<SharedFileInfo> getSharedFileInfoList() {
		return metadataList;
	}

	public SharedFileInfo getSharedFileInfo() {
		return metadataList.get(0);
	}

}
