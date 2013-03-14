package me.sh4rewith.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;

public final class DigestUtils {
	private DigestUtils() {
	}

	public static String md5Hash(byte[] bytes) {
		String result = "" + UUID.randomUUID().toString();
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(bytes);
			result = new BigInteger(1, digest.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			// silent fail
		}
		return result;
	}

	public static String md5Hash(byte[]... byteArrays) {
		String result = "" + UUID.randomUUID().toString();
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			for (byte[] byteArray : byteArrays) {
				digest.update(byteArray);
			}
			result = new BigInteger(1, digest.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			// silent fail
		}
		return result;
	}

	public static String md5Hash(String... strings) {
		ArrayList<byte[]> byteArraysList = new ArrayList<byte[]>();
		for (String string : strings) {
			byteArraysList.add(string.getBytes());
		}
		return md5Hash(byteArraysList.<byte[]> toArray(new byte[][] {}));
	}

	public static String sha256Hex(String string) {
		return org.apache.commons.codec.digest.DigestUtils.sha256Hex(string);
	}
}
