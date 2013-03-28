package me.sh4rewith.persistence.elasticsearch;

import static me.sh4rewith.persistence.keys.FileStorageInfoKeys.FILE_STORAGE_STORENAME;
import me.sh4rewith.config.persistence.ElasticSearchEmbeddedConfig;
import me.sh4rewith.config.persistence.ElasticSearchEmbeddedConfig.ElasticSearchIndexConfig;
import me.sh4rewith.domain.StorageCoordinates;
import me.sh4rewith.persistence.StorageRepository;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Base64;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElasticSearchStorageRepository implements StorageRepository {
	@Autowired
	private Client esClient;
	@Autowired
	private ElasticSearchIndexConfig indexConfig;

	@Override
	public StorageCoordinates store(byte[] bytes) {
		String result = null;
		try {
			XContentBuilder source = XContentFactory
					.jsonBuilder()
					.startObject()
					.field(FILE_STORAGE_STORENAME.keyName(), Base64.encodeBytes(bytes))
					.endObject();
			IndexResponse indexResponse = esClient.prepareIndex(ElasticSearchEmbeddedConfig.GLOBAL_INDEX,
					indexConfig.indexNameFor(FILE_STORAGE_STORENAME)).setSource(source).execute().actionGet();
			result = indexResponse.id();
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while storing File", e);
		}
		return new ElasticSearchStorageCoordinates.Builder(result).build();
	}

	@Override
	public byte[] retrieve(StorageCoordinates coordinates) {
		byte[] file = null;
		try {
			GetResponse response = esClient
					.prepareGet(ElasticSearchEmbeddedConfig.GLOBAL_INDEX,
							FILE_STORAGE_STORENAME.keyName(),
							coordinates.coordinates())
					.setFields(indexConfig.indexNameFor(FILE_STORAGE_STORENAME)).execute()
					.actionGet();
			Object value = response.field(indexConfig.indexNameFor(FILE_STORAGE_STORENAME)).value();
			file = Base64.decode((String) value);
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while getting File by coordinates:" + coordinates.coordinates(), e);
		}
		return file;
	}

}
