package me.sh4rewith.persistence.keys;

import java.util.HashSet;

import org.elasticsearch.common.base.Joiner;

public enum FileStorageInfoKeys implements Key {
	BYTES,
	FILE_STORAGE_STORENAME("file-storage");

	private String keyName;

	FileStorageInfoKeys() {
		this.keyName = this.name().toLowerCase();
	}

	FileStorageInfoKeys(String keyName) {
		this.keyName = keyName;
	}

	public String keyName() {
		return keyName;
	}

	public static String joinedKeyNames(String separator) {
		HashSet<String> values = new HashSet<String>();
		for (FileStorageInfoKeys key : values()) {
			values.add(key.keyName());
		}
		return Joiner.on(separator).join(values);
	}

	public static String[] keyNamesArray() {
		HashSet<String> values = new HashSet<String>();
		for (FileStorageInfoKeys key : values()) {
			values.add(key.keyName());
		}
		return values.<String> toArray(new String[values.size()]);
	}

	@Override
	public String toString() {
		return keyName();
	}
}
