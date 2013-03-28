package me.sh4rewith.config.persistence;

import static me.sh4rewith.persistence.keys.FileStorageInfoKeys.FILE_STORAGE_STORENAME;
import static me.sh4rewith.persistence.keys.RawFileInfoKeys.RAW_FILE_INFO_STORENAME;
import static me.sh4rewith.persistence.keys.RawFileKeys.RAW_FILE_STORENAME;
import static me.sh4rewith.persistence.keys.SharedFileFootprintKeys.SHARED_FILE_FOOTPRINT_STORENAME;
import static me.sh4rewith.persistence.keys.SharedFileInfoKeys.SHARED_FILE_INFO_STORENAME;
import static me.sh4rewith.persistence.keys.UserInfoKeys.USER_INFO_STORENAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import me.sh4rewith.persistence.keys.Key;

import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import fr.pilato.spring.elasticsearch.ElasticsearchClientFactoryBean;
import fr.pilato.spring.elasticsearch.ElasticsearchNodeFactoryBean;

@Configuration
@Profile("embedded-elasticsearch")
@PropertySource("classpath:elasticsearch-embedded.properties")
public class ElasticSearchEmbeddedConfig {
	public static final String GLOBAL_INDEX = "sh4rewithme";

	public static class ElasticSearchIndexConfig {
		final private Map<Key, String> indexMap;
		final private String defaultIndex;

		private ElasticSearchIndexConfig(String defaultIndex, Map<Key, String> indexMap) {
			this.defaultIndex = defaultIndex;
			this.indexMap = indexMap;
		}

		public static class Builder {
			private Map<Key, String> indexMap = new HashMap<Key, String>();
			private String defaultIndex;

			public Builder(String defaultIndex) {
				this.defaultIndex = defaultIndex;
			}

			public ElasticSearchIndexConfig build() {
				return new ElasticSearchIndexConfig(defaultIndex, Collections.unmodifiableMap(indexMap));
			}

			public Builder addIndexNameFor(Key key, Environment environment) {
				indexMap.put(key, environment.getProperty(key.keyName() + ".index", GLOBAL_INDEX));
				return this;
			}
		}

		public String indexNameFor(Key key) {
			return indexMap.get(key);
		}

		public String defaultIndex() {
			return defaultIndex;
		}

		public String[] allIndexes() {
			ArrayList<String> indexes = new ArrayList<String>(indexMap.values());
			indexes.add(defaultIndex);
			return indexes.toArray(new String[0]);
		}

	}

	@Autowired
	private Environment environment;

	@Bean
	public Node elasticSearchNode() throws Exception {
		ElasticsearchNodeFactoryBean factoryBean = new ElasticsearchNodeFactoryBean();
		factoryBean.setSettingsFile("elasticsearch-demo.yml");
		factoryBean.afterPropertiesSet();
		Node node = factoryBean.getObject();
		return node;
	}

	@Bean
	public Client elasticSearchClient() throws Exception {
		ElasticsearchClientFactoryBean elasticsearchClientFactoryBean = new ElasticsearchClientFactoryBean();
		elasticsearchClientFactoryBean.setNode(elasticSearchNode());
		// Ajouter des mappings pour les types à supprimer.
		elasticsearchClientFactoryBean.setForceMapping(environment.getRequiredProperty("force.mapping", Boolean.class));
		elasticsearchClientFactoryBean.afterPropertiesSet();
		return elasticsearchClientFactoryBean.getObject();
	}

	@Bean
	public AdminClient elasticSearchAdminClient() throws Exception {
		return elasticSearchClient().admin();
	}

	@Bean
	public ElasticSearchIndexConfig indexMap() {
		return new ElasticSearchIndexConfig.Builder(environment.getRequiredProperty("default.index"))
				.addIndexNameFor(SHARED_FILE_FOOTPRINT_STORENAME, environment)
				.addIndexNameFor(SHARED_FILE_INFO_STORENAME, environment)
				.addIndexNameFor(RAW_FILE_INFO_STORENAME, environment)
				.addIndexNameFor(RAW_FILE_STORENAME, environment)
				.addIndexNameFor(USER_INFO_STORENAME, environment)
				.addIndexNameFor(FILE_STORAGE_STORENAME, environment).build();
	}

}