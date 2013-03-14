package me.sh4rewith.config.persistence;

import me.sh4rewith.config.persistence.bootstrap.MongoInitBootstrap;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("prod-mongo")
@PropertySource("classpath:mongodb-prod.properties")
@Import(MongoInitBootstrap.class)
public class MongoProdConfig extends MongoConfigBase {
}