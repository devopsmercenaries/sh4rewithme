package me.sh4rewith.config.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.Mongo;
import com.mongodb.MongoURI;

@Configuration
public abstract class MongoConfigBase extends AbstractMongoConfiguration {
	@Autowired
	private Environment environment;

	public String getDatabaseName() {
		return "devops2devoxx";
	}

	@Override
	public Mongo mongo() throws Exception {
		return new Mongo(new MongoURI(
				environment.getProperty("mongodb.fulluri")
				));
	}
}
