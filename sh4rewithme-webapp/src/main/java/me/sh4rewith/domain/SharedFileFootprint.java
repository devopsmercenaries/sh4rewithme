package me.sh4rewith.domain;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class SharedFileFootprint {
	private final String id;
	private final String rawFileId;
	private final String sharedFileInfoId;
    private final Date creationDate;
    private final Date expirationDate;

    public static class Builder extends ValidatedImmutableBuilderBase<SharedFileFootprint> {
		@NotNull
		private String rawFileId;
		@NotNull
		private String sharedFileInfoId;
        @NotNull
        private Date creationDate;
        @NotNull
        private Date expirationDate;


        public Builder() {
		}

		public Builder setRawFileId(String rawFileId) {
			this.rawFileId = rawFileId;
			return this;
		}

		public Builder setSharedFileInfoId(String sharedFileInfoId) {
			this.sharedFileInfoId = sharedFileInfoId;
			return this;
		}

        public Builder setCreationDate(Date creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder setExpiration(Date creationDate) {
            this.expirationDate = creationDate;
            return this;
        }

		@Override
		protected SharedFileFootprint doBuild() {
			return new SharedFileFootprint(rawFileId, sharedFileInfoId, creationDate, expirationDate);
		}
	}

	private SharedFileFootprint(String rawFileId, String sharedFileInfoId, Date creationDate, Date expirationDate) {
		this.id = rawFileId + "-" + sharedFileInfoId;
		this.rawFileId = rawFileId;
		this.sharedFileInfoId = sharedFileInfoId;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
	}

	public String getRawFileId() {
		return rawFileId;
	}

	public String getSharedFileInfoId() {
		return sharedFileInfoId;
	}

	public String getId() {
		return id;
	}

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }
}
