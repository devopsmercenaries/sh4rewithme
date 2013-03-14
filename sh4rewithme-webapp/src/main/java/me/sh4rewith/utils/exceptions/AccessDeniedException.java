package me.sh4rewith.utils.exceptions;

public class AccessDeniedException extends RuntimeException {
	private static final long serialVersionUID = 7140865761280379277L;

	public AccessDeniedException() {
		super();
	}

	public AccessDeniedException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccessDeniedException(String message) {
		super(message);
	}

	public AccessDeniedException(Throwable cause) {
		super(cause);
	}

}