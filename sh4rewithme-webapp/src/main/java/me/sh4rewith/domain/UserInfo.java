package me.sh4rewith.domain;


import javax.validation.constraints.NotNull;

import me.sh4rewith.utils.DigestUtils;

public class UserInfo {

	private final String userHash;

	private final String id;

	private final String email;

	private final String firstname;

	private final String lastname;

	private final String credentials;
	private final RegistrationStatus registrationStatus;

	public static class Builder extends ValidatedImmutableBuilderBase<UserInfo> {
		@NotNull
		private String id;
		@NotNull
		private String email;
		@NotNull
		private String firstname;
		@NotNull
		private String lastname;
		@NotNull
		private String credentials;
		private RegistrationStatus registrationStatus = RegistrationStatus.NOT_YET_REGISTERED;
		private String userHash;

		public Builder(String id) {
			this.id = id;
		}

		public Builder setEmail(String email) {
			this.email = email;
			return this;
		}

		public Builder setFirstname(String firstname) {
			this.firstname = firstname;
			return this;
		}

		public Builder setLastname(String lastname) {
			this.lastname = lastname;
			return this;
		}

		public Builder setRegistrationStatus(RegistrationStatus registrationStatus) {
			this.registrationStatus = registrationStatus;
			return this;
		}

		public Builder setCredentials(String credentials) {
			this.credentials = credentials;
			return this;
		}

		public Builder setUserHash(String userHash) {
			this.userHash = userHash;
			return this;
		}

		@Override
		protected UserInfo doBuild() {
			if (userHash == null) {
				userHash = DigestUtils.md5Hash(id, email, firstname, lastname, credentials);
			}
			return new UserInfo(id, email, firstname, lastname, credentials, registrationStatus, userHash);
		}

	}

	private UserInfo(String id, String email, String firstname, String lastname, String credentials, RegistrationStatus registrationStatus, String userHash) {
		this.id = id;
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
		this.credentials = credentials;
		this.registrationStatus = registrationStatus;
		this.userHash = userHash;
	}

	public String getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public String getCredentials() {
		return credentials;
	}

	public RegistrationStatus getRegistrationStatus() {
		return registrationStatus;
	}

	public String getUserHash() {
		return this.userHash;
	}
}
