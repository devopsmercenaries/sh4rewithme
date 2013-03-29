package me.sh4rewith.config.persistence.bootstrap;

import java.util.concurrent.ExecutionException;

import me.sh4rewith.config.persistence.ElasticSearchConfigBase.ElasticSearchIndexConfig;
import me.sh4rewith.persistence.UsersRepository;

import org.elasticsearch.client.AdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("demo")
public class ElasticSearchInitBootstrap extends AbstractInitBootstrap {

	@Autowired
	@Qualifier("elasticsearch")
	private UsersRepository usersRepository;

	@Override
	protected UsersRepository getUsersRepository() {
		return usersRepository;
	}

	@Autowired
	private AdminClient esAdminClient;

	@Autowired
	private ElasticSearchIndexConfig indexConfig;

	@Bean
	public ElasticSearchInitBootstrap bootstrap() throws InterruptedException,
			ExecutionException {
		createAndStoreDemoUsers();
		return this;
	}
}
