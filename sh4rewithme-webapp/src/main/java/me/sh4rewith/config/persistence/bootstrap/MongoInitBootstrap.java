package me.sh4rewith.config.persistence.bootstrap;

import me.sh4rewith.domain.RegistrationStatus;
import me.sh4rewith.domain.UserInfo;
import me.sh4rewith.persistence.UsersRepository;
import me.sh4rewith.persistence.mongo.mappers.RawFileInfoMapper;
import me.sh4rewith.persistence.mongo.mappers.RawFileMapper;
import me.sh4rewith.persistence.mongo.mappers.SharedFileFootprintMapper;
import me.sh4rewith.persistence.mongo.mappers.SharedFileInfoMapper;
import me.sh4rewith.persistence.mongo.mappers.UserInfoMapper;
import me.sh4rewith.utils.DigestUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@Profile("init-mongo")
public class MongoInitBootstrap {
	@Autowired
	private MongoTemplate mongo;
	@Autowired
	private UsersRepository usersRepository;

	@Bean
	public MongoInitBootstrap bootstrap() {
		if (!mongo.collectionExists(UserInfoMapper.USER_INFO_COLLECTION)
				&& !mongo.collectionExists(SharedFileFootprintMapper.SHARED_FILE_FOOTPRINT_COLLECTION)
				&& !mongo.collectionExists(SharedFileInfoMapper.SHARED_FILE_INFO_COLLECTION)
				&& !mongo.collectionExists(RawFileInfoMapper.RAW_FILE_INFO_COLLECTION)
				&& !mongo.collectionExists(RawFileMapper.RAW_FILE_COLLECTION)) {
			UserInfo aheritier = new UserInfo.Builder("aheritier")
					.setFirstname("Arnaud").setLastname("Héritier")
					.setCredentials(DigestUtils.sha256Hex("aheritier"))
					.setEmail("aheritier@gmail.com")
					.setRegistrationStatus(RegistrationStatus.REGISTERED)
					.build();
			UserInfo dbaeli = new UserInfo.Builder("dbaeli")
					.setFirstname("Dimitri").setLastname("Baeli")
					.setCredentials(DigestUtils.sha256Hex("dbaeli"))
					.setEmail("dbaeli@gmail.com")
					.setRegistrationStatus(RegistrationStatus.REGISTERED)
					.build();
			UserInfo gcuisinier = new UserInfo.Builder("gcuisinier")
					.setFirstname("Gildas").setLastname("Cuisinier")
					.setCredentials(DigestUtils.sha256Hex("gcuisinier"))
					.setEmail("gcuisinier@gmail.com")
					.setRegistrationStatus(RegistrationStatus.REGISTERED)
					.build();
			UserInfo hgomez = new UserInfo.Builder("hgomez")
					.setFirstname("Henri").setLastname("Gomez")
					.setCredentials(DigestUtils.sha256Hex("hgomez"))
					.setEmail("henri.gomez@gmail.com")
					.setRegistrationStatus(RegistrationStatus.REGISTERED)
					.build();
			UserInfo pagregoire = new UserInfo.Builder("pagregoire")
					.setFirstname("Pierre-Antoine").setLastname("Grégoire")
					.setCredentials(DigestUtils.sha256Hex("pagregoire"))
					.setEmail("pierre.antoine.gregoire@gmail.com")
					.setRegistrationStatus(RegistrationStatus.REGISTERED)
					.build();
			UserInfo jgregoire = new UserInfo.Builder("jgregoire")
					.setFirstname("Jean").setLastname("Grégoire")
					.setCredentials(DigestUtils.sha256Hex("jgregoire"))
					.setEmail("jgregoire@jgregoire.com")
					.setRegistrationStatus(RegistrationStatus.NOT_YET_REGISTERED)
					.build();
			usersRepository.store(aheritier);
			usersRepository.store(dbaeli);
			usersRepository.store(gcuisinier);
			usersRepository.store(hgomez);
			usersRepository.store(pagregoire);
			usersRepository.store(jgregoire);
		}
		return this;
	}
}
