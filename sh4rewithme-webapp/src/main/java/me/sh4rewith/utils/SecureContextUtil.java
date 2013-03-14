package me.sh4rewith.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public final class SecureContextUtil {
	private SecureContextUtil() {
	}

	public static String getUsername() {
		String result = null;
		try {
			result = SecurityContextHolder.getContext().getAuthentication().getName();
		} catch (Throwable e) {
			// swallow errors
		}
		return result;
	}
}
