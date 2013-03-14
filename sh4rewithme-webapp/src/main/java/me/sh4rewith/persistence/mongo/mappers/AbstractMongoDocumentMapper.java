package me.sh4rewith.persistence.mongo.mappers;

import org.springframework.data.mongodb.core.DocumentCallbackHandler;

public abstract class AbstractMongoDocumentMapper implements DocumentCallbackHandler {
	public static final String MONGO_DOC_ID = "_id";
}
