package me.sh4rewith.config.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("remote-mongo")
@PropertySource("classpath:mongodb-remote.properties")
public class MongoRemoteConfig extends MongoConfigBase {
}
