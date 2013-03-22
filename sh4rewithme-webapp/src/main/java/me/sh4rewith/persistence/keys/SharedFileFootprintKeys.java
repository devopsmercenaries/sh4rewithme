package me.sh4rewith.persistence.keys;

import java.util.HashSet;

import org.elasticsearch.common.base.Joiner;

public enum SharedFileFootprintKeys implements Key {
	SHARED_FILE_INFO_ID,
	CREATION_DATE,
	RAW_FILE_ID,
	EXPIRATION_DATE,
	SHARED_FILE_FOOTPRINT_STORENAME("shared-file-footprint");

	private String keyName;

	SharedFileFootprintKeys() {
		this.keyName = this.name().toLowerCase();
	}

	SharedFileFootprintKeys(String keyName) {
		this.keyName = keyName;
	}

	public String keyName() {
		return keyName;
	}

	public static String joinedKeyNames(String separator) {
		HashSet<String> values = new HashSet<String>();
		for (SharedFileFootprintKeys key : values()) {
			values.add(key.keyName());
		}
		return Joiner.on(separator).join(values);
	}

	public static String[] keyNamesArray() {
		HashSet<String> values = new HashSet<String>();
		for (SharedFileFootprintKeys key : values()) {
			values.add(key.keyName());
		}
		return values.<String> toArray(new String[values.size()]);
	}

	@Override
	public String toString() {
		return keyName();
	}
}
