package me.sh4rewith.domain;


import java.util.Date;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

import me.sh4rewith.utils.UUIDUtils;

public class SharedFileInfo {
	private String owner;

	private String description;

	private Date creationDate;

	private Date expirationDate;

	private String id;

	private Privacy privacy;

	public static class Builder extends ValidatedImmutableBuilderBase<SharedFileInfo> {
		@NotNull
		private String owner;
		@NotNull
		private String description;
		@NotNull
		private Date creationDate;
		@NotNull
		@Future
		private Date expirationDate;
		private String id;
		private Privacy privacy;

		public Builder() {
		}

		public Builder setOwner(String owner) {
			this.owner = owner;
			return this;
		}

		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}

		public Builder setCreationDate(Date creationDate) {
			this.creationDate = creationDate;
			return this;
		}

		public Builder setExpirationDate(Date expiration) {
			this.expirationDate = expiration;
			return this;
		}

		public Builder setId(String id) {
			this.id = id;
			return this;
		}

		public Builder setPrivacy(Privacy privacy) {
			this.privacy = privacy;
			return this;
		}

		@Override
		protected SharedFileInfo doBuild() {
			if (privacy == null) {
				privacy = new Privacy.Builder(Privacy.Type.PRIVATE)
						.build();
			}
			if (id == null) {
				this.id = UUIDUtils.generateUUID(owner + description + creationDate);
			}
			return new SharedFileInfo(id, owner, description, creationDate, expirationDate, privacy);
		}
	}

	private SharedFileInfo(String id, String owner, String description, Date creationDate, Date expirationDate, Privacy privacy) {
		this.owner = owner;
		this.description = description;
		this.creationDate = creationDate;
		this.expirationDate = expirationDate;
		this.id = id;
		this.privacy = privacy;
	}

	public SharedFileInfo(String owner, String description, Date creationDate, Date expirationDate) {
		this.owner = owner;
		this.description = description;
		this.creationDate = creationDate;
		this.expirationDate = expirationDate;
		this.id = UUIDUtils.generateUUID(owner + description + creationDate);
	}

	public String getOwner() {
		return owner;
	}

	public String getDescription() {
		return description;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getId() {
		return id;
	}

	public Privacy getPrivacy() {
		return privacy;
	}

	public Boolean can(String userId, DoSecured dosecured) {
		Boolean result = false;
		switch (dosecured) {
		case DOWNLOAD:
			result = this.privacy.getType() != Privacy.Type.PRIVATE || (userId.equals(this.owner) || this.privacy.getBuddies().contains(userId));
			break;
		case PRIVACY_UPDATE:
			result = userId.equals(this.owner);
			break;
		case DELETION:
			result = userId.equals(this.owner);
			break;
		default:
			break;
		}
		return result;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}
}
