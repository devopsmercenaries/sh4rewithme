package me.sh4rewith.persistence.mongo;

import static me.sh4rewith.persistence.mongo.mappers.AbstractMongoDocumentMapper.MONGO_DOC_ID;
import static me.sh4rewith.persistence.mongo.mappers.UserInfoMapper.CREDENTIALS;
import static me.sh4rewith.persistence.mongo.mappers.UserInfoMapper.EMAIL;
import static me.sh4rewith.persistence.mongo.mappers.UserInfoMapper.FIRST_NAME;
import static me.sh4rewith.persistence.mongo.mappers.UserInfoMapper.HASH;
import static me.sh4rewith.persistence.mongo.mappers.UserInfoMapper.LAST_NAME;
import static me.sh4rewith.persistence.mongo.mappers.UserInfoMapper.REGISTRATION_STATUS;
import static me.sh4rewith.persistence.mongo.mappers.UserInfoMapper.USER_INFO_STORENAME;

import java.util.List;

import me.sh4rewith.domain.RegistrationStatus;
import me.sh4rewith.domain.UserInfo;
import me.sh4rewith.persistence.UsersRepository;
import me.sh4rewith.persistence.mongo.mappers.UserInfoMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@Profile("init-mongo,wipe-mongo,local-mongo,prod-mongo,remote-mongo")
public class MongoUsersRepository implements UsersRepository {

	@Autowired
	MongoTemplate mongo;

	@Override
	public void store(UserInfo userInfo) {
		final Query userQuery = new Query(Criteria.where(MONGO_DOC_ID).is(userInfo.getId()));
		mongo.upsert(userQuery, new Update().set(MONGO_DOC_ID, userInfo.getId()), USER_INFO_STORENAME);
		mongo.upsert(userQuery, new Update().set(EMAIL, userInfo.getEmail()), USER_INFO_STORENAME);
		mongo.upsert(userQuery, new Update().set(FIRST_NAME, userInfo.getFirstname()), USER_INFO_STORENAME);
		mongo.upsert(userQuery, new Update().set(LAST_NAME, userInfo.getLastname()), USER_INFO_STORENAME);
		mongo.upsert(userQuery, new Update().set(CREDENTIALS, userInfo.getCredentials()), USER_INFO_STORENAME);
		mongo.upsert(userQuery, new Update().set(REGISTRATION_STATUS, userInfo.getRegistrationStatus()), USER_INFO_STORENAME);
		mongo.upsert(userQuery, new Update().set(HASH, userInfo.getUserHash()), USER_INFO_STORENAME);
	}

	@Override
	public UserInfo getUserInfoById(String userId) {
		UserInfoMapper userInfoMapper = new UserInfoMapper();
		mongo.executeQuery(new Query(Criteria.where(MONGO_DOC_ID).is(userId)), USER_INFO_STORENAME, userInfoMapper);
		return userInfoMapper.getUserInfo();
	}

	@Override
	public List<UserInfo> findAllUserInfoBut(String userId) {
		UserInfoMapper userInfoMapper = new UserInfoMapper();
		mongo.executeQuery(new Query(Criteria.where(MONGO_DOC_ID).ne(userId)), USER_INFO_STORENAME, userInfoMapper);
		return userInfoMapper.getUserInfoList();
	}

	@Override
	public Long countAll() {
		return mongo.count(new Query(Criteria.where(MONGO_DOC_ID).regex(".*")), USER_INFO_STORENAME);
	}

	@Override
	public UserInfo getUserInfoByHash(String userHash) {
		UserInfoMapper userInfoMapper = new UserInfoMapper();
		mongo.executeQuery(new Query(Criteria.where(HASH).is(userHash)), USER_INFO_STORENAME, userInfoMapper);
		return userInfoMapper.getUserInfo();
	}

	@Override
	public void changeRegistrationStatusByHash(String userInfoHash, RegistrationStatus registrationStatus) {
		final Query userQuery = new Query(Criteria.where(HASH).is(userInfoHash));
		mongo.upsert(userQuery, new Update().set(REGISTRATION_STATUS, registrationStatus), USER_INFO_STORENAME);
	}
}
