package me.sh4rewith.persistence.keys;

import java.util.HashSet;

import org.elasticsearch.common.base.Joiner;

public enum SharedFileInfoKeys implements Key {
	OWNER,
	DESCRIPTION,
	CREATION_DATE,
	PRIVACY_TYPE,
	BUDDIES_LIST,
	EXPIRATION_DATE,
	SHARED_FILE_INFO_STORENAME("shared-file-info");

	private String keyName;

	SharedFileInfoKeys() {
		this.keyName = this.name().toLowerCase();
	}

	SharedFileInfoKeys(String keyName) {
		this.keyName = keyName;
	}

	public String keyName() {
		return keyName;
	}

	public static String joinedKeyNames(String separator) {
		HashSet<String> values = new HashSet<String>();
		for (SharedFileInfoKeys key : values()) {
			values.add(key.keyName());
		}
		return Joiner.on(separator).join(values);
	}

	public static String[] keyNamesArray() {
		HashSet<String> values = new HashSet<String>();
		for (SharedFileInfoKeys key : values()) {
			values.add(key.keyName());
		}
		return values.<String> toArray(new String[values.size()]);
	}

	@Override
	public String toString() {
		return keyName();
	}
}
