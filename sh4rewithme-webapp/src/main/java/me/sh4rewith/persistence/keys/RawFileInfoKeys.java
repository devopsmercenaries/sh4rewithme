package me.sh4rewith.persistence.keys;

import java.util.HashSet;

import org.elasticsearch.common.base.Joiner;

public enum RawFileInfoKeys implements Key {
	SIZE,
	CONTENT_TYPE,
	ORIGINAL_FILE_NAME,
	RAW_FILE_ID,
	RAW_FILE_INFO_STORENAME("raw-file-info");

	private String keyName;

	RawFileInfoKeys() {
		this.keyName = this.name().toLowerCase();
	}

	RawFileInfoKeys(String keyName) {
		this.keyName = keyName;
	}

	public String keyName() {
		return keyName;
	}

	public static String joinedKeyNames(String separator) {
		HashSet<String> values = new HashSet<String>();
		for (RawFileInfoKeys key : values()) {
			values.add(key.keyName());
		}
		return Joiner.on(separator).join(values);
	}

	public static String[] keyNamesArray() {
		HashSet<String> values = new HashSet<String>();
		for (RawFileInfoKeys key : values()) {
			values.add(key.keyName());
		}
		return values.<String> toArray(new String[values.size()]);
	}

	@Override
	public String toString() {
		return keyName();
	}
}
