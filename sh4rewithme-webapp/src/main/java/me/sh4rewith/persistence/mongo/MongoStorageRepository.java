package me.sh4rewith.persistence.mongo;

import me.sh4rewith.domain.StorageCoordinates;
import me.sh4rewith.persistence.StorageRepository;

import org.springframework.stereotype.Service;

@Service
public class MongoStorageRepository implements StorageRepository {

	@Override
	public StorageCoordinates store(byte[] file) {
		if (1 == 1)
			throw new IllegalStateException("Not yet implemented");
		return null;
	}

	@Override
	public byte[] retrieve(StorageCoordinates coordinates) {
		if (1 == 1)
			throw new IllegalStateException("Not yet implemented");
		return null;
	}

}
