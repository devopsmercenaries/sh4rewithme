package me.sh4rewith.config.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("prod")
@PropertySource("classpath:elasticsearch-prod.properties")
public class ElasticSearchProdConfig extends ElasticSearchConfigBase {
	public String getElasticSearchConfigFile() {
		return "elasticsearch-prod.yml";
	}
}