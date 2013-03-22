package me.sh4rewith.persistence.keys;

import java.util.HashSet;

import org.elasticsearch.common.base.Joiner;

public enum UserInfoKeys implements Key {
	LAST_NAME,
	FIRST_NAME,
	EMAIL,
	CREDENTIALS,
	REGISTRATION_STATUS,
	HASH,
	USER_INFO_STORENAME("user-info");

	private String keyName;

	UserInfoKeys() {
		this.keyName = this.name().toLowerCase();
	}

	UserInfoKeys(String keyName) {
		this.keyName = keyName;
	}

	public String keyName() {
		return keyName;
	}

	public static String joinedKeyNames(String separator) {
		HashSet<String> values = new HashSet<String>();
		for (UserInfoKeys key : values()) {
			values.add(key.keyName());
		}
		return Joiner.on(separator).join(values);
	}

	public static String[] keyNamesArray() {
		HashSet<String> values = new HashSet<String>();
		for (UserInfoKeys key : values()) {
			values.add(key.keyName());
		}
		return values.<String> toArray(new String[values.size()]);
	}

	@Override
	public String toString() {
		return keyName();
	}
}
