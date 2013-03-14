package me.sh4rewith.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorUtil {

	public static void broadcastError(Class<?> clazz, Throwable throwable) {
		Logger logger = LoggerFactory.getLogger(clazz);
		logger.error(throwable.getMessage(), throwable);
	}

}
