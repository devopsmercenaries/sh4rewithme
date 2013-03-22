package me.sh4rewith.config.persistence.bootstrap;

import me.sh4rewith.persistence.UsersRepository;
import me.sh4rewith.persistence.keys.UserInfoKeys;
import me.sh4rewith.persistence.mongo.mappers.RawFileInfoMapper;
import me.sh4rewith.persistence.mongo.mappers.RawFileMapper;
import me.sh4rewith.persistence.mongo.mappers.SharedFileFootprintMapper;
import me.sh4rewith.persistence.mongo.mappers.SharedFileInfoMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@Profile("wipe-mongo")
public class MongoWipeBootstrap {
	@Autowired
	private MongoTemplate mongo;
	@Autowired
	@Qualifier("mongo")
	private UsersRepository usersRepository;

	@Bean
	public MongoWipeBootstrap bootstrap() {
		mongo.dropCollection(RawFileInfoMapper.RAW_FILE_INFO_STORENAME);
		mongo.dropCollection(RawFileMapper.RAW_FILE_STORENAME);
		mongo.dropCollection(SharedFileInfoMapper.SHARED_FILE_INFO_STORENAME);
		mongo.dropCollection(SharedFileFootprintMapper.SHARED_FILE_FOOTPRINT_STORENAME);
		mongo.dropCollection(UserInfoKeys.USER_INFO_STORENAME.keyName());
		return this;
	}
}
