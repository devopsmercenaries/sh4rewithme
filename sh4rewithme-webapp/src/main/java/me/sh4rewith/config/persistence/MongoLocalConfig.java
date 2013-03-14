package me.sh4rewith.config.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("local-mongo")
@PropertySource("classpath:mongodb-local.properties")
public class MongoLocalConfig extends MongoConfigBase {
}