package me.sh4rewith.persistence;

import me.sh4rewith.domain.StorageCoordinates;

public interface StorageRepository {

	public StorageCoordinates store(byte[] file);

	public byte[] retrieve(StorageCoordinates coordinates);
}
