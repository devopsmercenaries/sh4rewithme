package me.sh4rewith.config.persistence;

import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import fr.pilato.spring.elasticsearch.ElasticsearchNodeFactoryBean;

@Configuration
@Profile("embedded-elasticsearch")
@PropertySource("classpath:elasticsearch-embedded.properties")
public class ElasticSearchEmbeddedConfig {
	@Autowired
	private Environment environment;

	@Bean
	public Node elasticSearchNode() throws Exception {
		ElasticsearchNodeFactoryBean factoryBean = new ElasticsearchNodeFactoryBean();
		factoryBean.afterPropertiesSet();
		Node node = factoryBean.getObject();
		return node;
	}

	@Bean
	public Client elasticSearchClient() throws Exception {
		return elasticSearchNode().client();
	}

	@Bean
	public AdminClient elasticSearchAdminClient() throws Exception {
		return elasticSearchNode().client().admin();
	}

}