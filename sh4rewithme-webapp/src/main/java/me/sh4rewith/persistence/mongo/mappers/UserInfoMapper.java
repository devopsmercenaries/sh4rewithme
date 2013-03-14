package me.sh4rewith.persistence.mongo.mappers;


import java.util.ArrayList;
import java.util.List;

import me.sh4rewith.domain.RegistrationStatus;
import me.sh4rewith.domain.UserInfo;

import org.springframework.dao.DataAccessException;

import com.mongodb.DBObject;
import com.mongodb.MongoException;

public class UserInfoMapper extends AbstractMongoDocumentMapper {
	public static final String LAST_NAME = "last_name";

	public static final String FIRST_NAME = "first_name";

	public static final String EMAIL = "email";

	public static final String CREDENTIALS = "credentials";

	public static final String REGISTRATION_STATUS = "registration_status";

	public static final String HASH = "hash";

	public static final String USER_INFO_COLLECTION = "user_info";

	List<UserInfo> userInfoList = new ArrayList<UserInfo>();

	@Override
	public void processDocument(DBObject dbObject) throws MongoException, DataAccessException {
		UserInfo userInfo = new UserInfo.Builder((String) dbObject.get(MONGO_DOC_ID))
				.setEmail((String) dbObject.get(EMAIL))
				.setFirstname((String) dbObject.get(FIRST_NAME))
				.setLastname((String) dbObject.get(LAST_NAME))
				.setCredentials((String) dbObject.get(CREDENTIALS))
				.setRegistrationStatus(
						RegistrationStatus.valueOf(
								(String) dbObject.get(REGISTRATION_STATUS)
								)
				)
				.setUserHash((String) dbObject.get(HASH))
				.build();
		userInfoList.add(userInfo);
	}

	public List<UserInfo> getUserInfoList() {
		return userInfoList;
	}

	public UserInfo getUserInfo() {
		return userInfoList.get(0);
	}
}
