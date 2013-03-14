package me.sh4rewith.utils;

import java.util.UUID;

public final class UUIDUtils {
	private UUIDUtils() {
	}

	public static String generateUUID(String seed) {
		return UUID.nameUUIDFromBytes(seed.getBytes()).toString();
	}
}