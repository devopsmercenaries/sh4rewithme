package me.sh4rewith.service;

public enum ConfirmationMechanism {
	MAIL, DIRECT;
	private String confirmationId;

	public static String getPropertyName() {
		return "confirmation.mechanism";
	}

	public String getConfirmationId() {
		return confirmationId;
	}

	public void setConfirmationId(String confirmationId) {
		this.confirmationId = confirmationId;
	}
}