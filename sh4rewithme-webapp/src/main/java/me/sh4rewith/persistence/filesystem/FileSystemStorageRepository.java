package me.sh4rewith.persistence.filesystem;

import java.io.File;
import java.io.IOException;

import me.sh4rewith.domain.StorageCoordinates;
import me.sh4rewith.persistence.StorageException;
import me.sh4rewith.persistence.StorageRepository;
import me.sh4rewith.utils.UUIDUtils;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

@Service
public class FileSystemStorageRepository implements StorageRepository {

	@Override
	public StorageCoordinates store(byte[] bytes) {
		File tempStorage = new File(FileUtils.getTempDirectory(), "sh4rewithme");
		File tempFile = new File(tempStorage, UUIDUtils.generateUUID(System
				.currentTimeMillis() + ""));
		StorageCoordinates storageCoordinates = null;
		try {
			FileUtils.writeByteArrayToFile(tempFile, bytes);
			storageCoordinates = new FileSystemStorageCoordinates.Builder(
					tempFile.toURI()
							.toURL().toExternalForm()).build();
		} catch (IOException e) {
			tempFile.delete();
			throw new StorageException("Problem while storing file.", e);
		}
		return storageCoordinates;
	}

	@Override
	public byte[] retrieve(StorageCoordinates coordinates) {
		byte[] file = null;
		try {
			FileUtils.readFileToByteArray(new File(coordinates.coordinates()));
		} catch (IOException e) {
			throw new StorageException("Problem while retrieving file.", e);
		}
		return file;
	}

}
