package me.sh4rewith.config.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("qual")
@PropertySource("classpath:elasticsearch-qual.properties")
public class ElasticSearchQualConfig extends ElasticSearchConfigBase {
	public String getElasticSearchConfigFile() {
		return "elasticsearch-qual.yml";
	}
}