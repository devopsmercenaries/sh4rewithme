package me.sh4rewith.config.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("demo")
@PropertySource("classpath:elasticsearch-demo.properties")
public class ElasticSearchDemoConfig extends ElasticSearchConfigBase {
	public String getElasticSearchConfigFile() {
		return "elasticsearch-demo.yml";
	}
}