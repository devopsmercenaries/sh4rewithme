package me.sh4rewith.persistence.elasticsearch;

import me.sh4rewith.domain.StorageCoordinates;
import me.sh4rewith.persistence.StorageRepository;
import me.sh4rewith.utils.persistence.ElasticSearchUtils;

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

	@Override
	public StorageCoordinates store(byte[] bytes) {
		String result = null;
		try {
			XContentBuilder source = XContentFactory
					.jsonBuilder()
					.startObject()
					.field("bytes", Base64.encodeBytes(bytes))
					.endObject();
			IndexResponse indexResponse = esClient.prepareIndex(ElasticSearchUtils.GLOBAL_INDEX,
					"file_storage").setSource(source).execute().actionGet();
			result = indexResponse.id();
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while storing Shared File footprint", e);
		}
		return new ElasticSearchStorageCoordinates.Builder(result).build();
	}

	@Override
	public byte[] retrieve(StorageCoordinates coordinates) {
		byte[] file = null;
		try {
			GetResponse response = esClient
					.prepareGet(ElasticSearchUtils.GLOBAL_INDEX,
							"file_storage",
							coordinates.coordinates())
					.setFields("bytes").execute()
					.actionGet();
			Object value = response.field("bytes").value();
			file = Base64.decode((String)value);
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while getting Shared File Info by Id", e);
		}
		return file;
	}

}
