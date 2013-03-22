package me.sh4rewith.config.persistence.bootstrap;

import me.sh4rewith.persistence.UsersRepository;
import me.sh4rewith.persistence.mongo.mappers.RawFileInfoMapper;
import me.sh4rewith.persistence.mongo.mappers.RawFileMapper;
import me.sh4rewith.persistence.mongo.mappers.SharedFileFootprintMapper;
import me.sh4rewith.persistence.mongo.mappers.SharedFileInfoMapper;
import me.sh4rewith.persistence.mongo.mappers.UserInfoMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@Profile("init-mongo")
public class MongoInitBootstrap extends AbstractInitBootstrap {

	@Autowired
	@Qualifier("mongo")
	private UsersRepository usersRepository;

	@Override
	protected UsersRepository getUsersRepository() {
		return usersRepository;
	}

	@Autowired
	private MongoTemplate mongo;

	@Bean
	public MongoInitBootstrap bootstrap() {
		if (!mongo.collectionExists(UserInfoMapper.USER_INFO_STORENAME)
				&& !mongo
						.collectionExists(SharedFileFootprintMapper.SHARED_FILE_FOOTPRINT_STORENAME)
				&& !mongo
						.collectionExists(SharedFileInfoMapper.SHARED_FILE_INFO_STORENAME)
				&& !mongo
						.collectionExists(RawFileInfoMapper.RAW_FILE_INFO_STORENAME)
				&& !mongo.collectionExists(RawFileMapper.RAW_FILE_STORENAME)) {
			createAndStoreDemoUsers();
		}
		return this;
	}

}
