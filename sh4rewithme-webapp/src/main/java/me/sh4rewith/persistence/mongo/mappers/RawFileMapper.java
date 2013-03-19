package me.sh4rewith.persistence.mongo.mappers;

import java.util.ArrayList;
import java.util.List;

import me.sh4rewith.domain.RawFile;
import me.sh4rewith.persistence.keys.RawFileKeys;
import me.sh4rewith.persistence.mongo.MongoStorageCoordinates;

import org.springframework.dao.DataAccessException;

import com.mongodb.DBObject;
import com.mongodb.MongoException;

public class RawFileMapper extends AbstractMongoDocumentMapper {
	public static final String RAW_FILE_STORENAME =
			RawFileKeys.RAW_FILE_STORENAME.keyName();
	public static final String STORAGE_COORDINATES =
			RawFileKeys.STORAGE_COORDINATES.keyName();

	List<RawFile> contentList = new ArrayList<RawFile>();

	@Override
	public void processDocument(DBObject dbObject) throws MongoException,
			DataAccessException {
		String id = (String) dbObject.get(MONGO_DOC_ID);
		RawFile content = new RawFile.Builder()
				.setId(id)
				.setStorageCoordinates(
						new MongoStorageCoordinates.Builder((String) dbObject
								.get(STORAGE_COORDINATES)).build())
				.build();
		contentList.add(content);
	}

	public List<RawFile> getContentList() {
		return contentList;
	}

	public RawFile getRawFile() {
		return contentList.get(0);
	}
}
