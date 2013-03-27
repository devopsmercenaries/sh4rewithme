package me.sh4rewith.utils.persistence;

import me.sh4rewith.domain.StorageCoordinates;
import me.sh4rewith.domain.StorageCoordinates.StorageType;
import me.sh4rewith.persistence.elasticsearch.ElasticSearchStorageCoordinates;
import me.sh4rewith.persistence.filesystem.FileSystemStorageCoordinates;
import me.sh4rewith.persistence.mongo.MongoStorageCoordinates;

public class StorageUtils {
	private static final String COORDINATES_SEPARATOR = "###";

	public static StorageCoordinates deserializeCoordinates(String valueAsString) {
		String[] tokens = valueAsString.split(COORDINATES_SEPARATOR);
		StorageType storageType = StorageType.valueOf(tokens[0]);
		String coordinatesStr = tokens[1];
		StorageCoordinates coordinates = null;
		switch (storageType) {
		case FILESYSTEM:
			coordinates = new FileSystemStorageCoordinates
					.Builder(coordinatesStr).build();
			break;
		case MONGO:
			coordinates = new MongoStorageCoordinates
					.Builder(coordinatesStr)
							.build();
			break;
		case ELASTICSEARCH:
			coordinates = new ElasticSearchStorageCoordinates
					.Builder(coordinatesStr)
							.build();
			break;

		default:
			break;
		}
		return coordinates;
	}

	public static String serializeCoordinates(
			StorageCoordinates storageCoordinates) {
		StringBuilder builder = new StringBuilder();
		builder.append(storageCoordinates.storageType().name());
		builder.append(COORDINATES_SEPARATOR);
		builder.append(storageCoordinates.coordinates());
		return builder.toString();
	}

}
