package me.sh4rewith.config.persistence.bootstrap;

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
		esAdminClient.indices().prepareDelete("sh4rewithme").execute()
				.actionGet();
		return this;
	}
}