package me.sh4rewith.domain;

public interface StorageCoordinates {

	public static enum StorageType {
		FILESYSTEM,
		MONGO
	}

	StorageType storageType();

	String coordinates();
}
