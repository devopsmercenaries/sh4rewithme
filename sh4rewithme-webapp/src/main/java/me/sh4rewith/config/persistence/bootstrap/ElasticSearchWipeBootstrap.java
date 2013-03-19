package me.sh4rewith.config.persistence.bootstrap;

import me.sh4rewith.persistence.keys.UserInfoKeys;
import me.sh4rewith.persistence.mongo.mappers.RawFileInfoMapper;
import me.sh4rewith.persistence.mongo.mappers.RawFileMapper;
import me.sh4rewith.persistence.mongo.mappers.SharedFileFootprintMapper;
import me.sh4rewith.persistence.mongo.mappers.SharedFileInfoMapper;

import org.elasticsearch.client.AdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("wipe-elasticsearch")
public class ElasticSearchWipeBootstrap {
	@Autowired
	private AdminClient esAdminClient;

	@Bean
	public ElasticSearchWipeBootstrap bootstrap() {
		esAdminClient
				.indices()
				.prepareDelete(
						UserInfoKeys.USER_INFO_STORENAME.keyName(),
						SharedFileFootprintMapper.SHARED_FILE_FOOTPRINT_STORENAME,
						SharedFileInfoMapper.SHARED_FILE_INFO_STORENAME,
						RawFileInfoMapper.RAW_FILE_INFO_STORENAME,
						RawFileMapper.RAW_FILE_STORENAME
				)
				.execute().actionGet();
		return this;
	}
}