package me.sh4rewith.persistence.keys;

import java.util.HashSet;

import org.elasticsearch.common.base.Joiner;

public enum RawFileKeys implements Key {
	BYTES,
	RAW_FILE_STORENAME("raw-file");

	private String keyName;

	RawFileKeys() {
		this.keyName = this.name().toLowerCase();
	}

	RawFileKeys(String keyName) {
		this.keyName = keyName;
	}

	public String keyName() {
		return keyName;
	}

	public static String joinedKeyNames(String separator) {
		HashSet<String> values = new HashSet<String>();
		for (RawFileKeys key : values()) {
			values.add(key.keyName());
		}
		return Joiner.on(separator).join(values);
	}

	public static String[] keyNamesArray() {
		HashSet<String> values = new HashSet<String>();
		for (RawFileKeys key : values()) {
			values.add(key.keyName());
		}
		return values.<String> toArray(new String[values.size()]);
	}

	@Override
	public String toString() {
		return keyName();
	}
}
