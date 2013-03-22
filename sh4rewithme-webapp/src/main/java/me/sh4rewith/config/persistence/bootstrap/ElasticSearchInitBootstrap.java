package me.sh4rewith.config.persistence.bootstrap;

import java.util.concurrent.ExecutionException;

import me.sh4rewith.persistence.UsersRepository;
import me.sh4rewith.persistence.keys.UserInfoKeys;
import me.sh4rewith.persistence.mongo.mappers.RawFileInfoMapper;
import me.sh4rewith.persistence.mongo.mappers.RawFileMapper;
import me.sh4rewith.persistence.mongo.mappers.SharedFileFootprintMapper;
import me.sh4rewith.persistence.mongo.mappers.SharedFileInfoMapper;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("init-elasticsearch")
public class ElasticSearchInitBootstrap extends AbstractInitBootstrap {

	@Autowired
	@Qualifier("elasticsearch")
	private UsersRepository usersRepository;

	@Override
	protected UsersRepository getUsersRepository() {
		return usersRepository;
	}

	@Autowired
	private Client esClient;
	@Autowired
	private AdminClient esAdminClient;

	@Bean
	public ElasticSearchInitBootstrap bootstrap() throws InterruptedException,
			ExecutionException {
		IndicesExistsResponse userInfoIndexExistsResponse = esAdminClient
				.indices()
				.prepareExists(UserInfoKeys.USER_INFO_STORENAME.keyName())
				.execute().get();
		IndicesExistsResponse sharedFileFootprintIndexExistsResponse = esAdminClient
				.indices()
				.prepareExists(
						SharedFileFootprintMapper.SHARED_FILE_FOOTPRINT_STORENAME)
				.execute().actionGet();
		IndicesExistsResponse sharedFileInfoExistsResponse = esAdminClient
				.indices()
				.prepareExists(SharedFileInfoMapper.SHARED_FILE_INFO_STORENAME)
				.execute().actionGet();
		IndicesExistsResponse rawFileInfoIndexExistsResponse = esAdminClient
				.indices()
				.prepareExists(RawFileInfoMapper.RAW_FILE_INFO_STORENAME)
				.execute().actionGet();
		IndicesExistsResponse rawFileIndexExistsResponse = esAdminClient
				.indices().prepareExists(RawFileMapper.RAW_FILE_STORENAME)
				.execute().actionGet();
		if (!userInfoIndexExistsResponse.exists()
				&& !sharedFileFootprintIndexExistsResponse.exists()
				&& !sharedFileInfoExistsResponse.exists()
				&& !rawFileInfoIndexExistsResponse.exists()
				&& !rawFileIndexExistsResponse.exists()) {
			createAndStoreDemoUsers();
		}
		return this;
	}
}
