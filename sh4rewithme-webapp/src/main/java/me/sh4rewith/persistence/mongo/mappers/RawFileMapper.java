package me.sh4rewith.persistence.mongo.mappers;


import java.util.ArrayList;
import java.util.List;

import me.sh4rewith.domain.RawFile;

import org.springframework.dao.DataAccessException;

import com.mongodb.DBObject;
import com.mongodb.MongoException;

public class RawFileMapper extends AbstractMongoDocumentMapper {
	public static final String RAW_FILE_COLLECTION = "raw_file";

	public static final String BYTES = "bytes";

	List<RawFile> contentList = new ArrayList<RawFile>();

	@Override
	public void processDocument(DBObject dbObject) throws MongoException, DataAccessException {
		String id = (String) dbObject.get(MONGO_DOC_ID);
		RawFile content = new RawFile.Builder()
				.setId(id)
				.setBytes((byte[]) dbObject.get(BYTES))
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
