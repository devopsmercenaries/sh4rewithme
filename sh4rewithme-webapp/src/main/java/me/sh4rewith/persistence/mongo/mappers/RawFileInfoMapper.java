package me.sh4rewith.persistence.mongo.mappers;


import java.util.ArrayList;
import java.util.List;

import me.sh4rewith.domain.RawFileInfo;

import org.springframework.dao.DataAccessException;

import com.mongodb.DBObject;
import com.mongodb.MongoException;

public class RawFileInfoMapper extends AbstractMongoDocumentMapper {

	public static final String RAW_FILE_INFO_COLLECTION = "raw_file_info";

	public static final String SIZE = "size";
	public static final String CONTENT_TYPE = "content_type";
	public static final String ORIGINAL_FILE_NAME = "original_file_name";
	public static final String RAW_FILE_ID = "raw_file_id";

	List<RawFileInfo> rawFileInfoList = new ArrayList<RawFileInfo>();

	public static final String INFO_SUFFIX = "-info";

	@Override
	public void processDocument(DBObject dbObject) throws MongoException, DataAccessException {
		RawFileInfo rawFileInfo = new RawFileInfo.Builder()
				.setId((String) dbObject.get(MONGO_DOC_ID))
				.setRawFileId((String) dbObject.get(RAW_FILE_ID))
				.setContentType((String) dbObject.get(CONTENT_TYPE))
				.setSize((Long) dbObject.get(SIZE))
				.setOriginalFileName((String) dbObject.get(ORIGINAL_FILE_NAME))
				.build();
		rawFileInfoList.add(rawFileInfo);
	}

	public List<RawFileInfo> getRawFileInfoList() {
		return rawFileInfoList;
	}

	public RawFileInfo getRawFileInfo() {
		return rawFileInfoList.get(0);
	}
}
