package me.sh4rewith.persistence;

import me.sh4rewith.domain.StorageCoordinates.StorageType;
import me.sh4rewith.persistence.filesystem.FileSystemStorageRepository;
import me.sh4rewith.persistence.mongo.MongoStorageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Repositories {

	@Autowired
	private UsersRepository usersRepository;
	@Autowired
	private SharedFilesRepository sharedFilesRepository;

	@Autowired(required = false)
	private MongoStorageRepository mongoStorageRepository;
	@Autowired
	private FileSystemStorageRepository fileSystemStorageRepository;

	public UsersRepository usersRepository() {
		return usersRepository;
	}

	public SharedFilesRepository sharedFilesRepository() {
		return sharedFilesRepository;
	}

	public StorageRepository filesRepository(StorageType storageType) {
		StorageRepository storageRepository = null;
		switch (storageType) {
		case FILESYSTEM:
			storageRepository = fileSystemStorageRepository;
			break;
		case MONGO:
			storageRepository = mongoStorageRepository;
		default:
			break;
		}
		if (storageRepository == null) {
			throw new IllegalArgumentException("Invalid storage type:"
					+ storageType + "no matching repository");
		}
		return storageRepository;
	}
}
