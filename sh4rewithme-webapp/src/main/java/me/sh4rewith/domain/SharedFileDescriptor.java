package me.sh4rewith.domain;

import javax.validation.constraints.NotNull;

public class SharedFileDescriptor {

	private final RawFileInfo rawFileInfo;

	private final SharedFileInfo info;

	private final SharedFileFootprint footprint;

	public static class Builder extends ValidatedImmutableBuilderBase<SharedFileDescriptor> {
		@NotNull
		private RawFileInfo rawFileInfo;
		@NotNull
		private SharedFileInfo info;

		public Builder() {
		}

		public Builder setRawFileInfo(RawFileInfo rawFileInfo) {
			this.rawFileInfo = rawFileInfo;
			return this;
		}

		public Builder setSharedFileInfo(SharedFileInfo info) {
			this.info = info;
			return this;
		}

		@Override
		protected SharedFileDescriptor doBuild() {
			return new SharedFileDescriptor(rawFileInfo, info);
		}
	}

	private SharedFileDescriptor(RawFileInfo rawFileInfo, SharedFileInfo info) {
		this.footprint = new SharedFileFootprint.Builder()
				.setRawFileId(rawFileInfo.getRawFileId())
				.setSharedFileInfoId(info.getId())
                .setCreationDate(info.getCreationDate())
                .setExpiration(info.getExpirationDate())
				.build();
		this.rawFileInfo = rawFileInfo;
		this.info = info;
	}

	public RawFileInfo getRawFileInfo() {
		return rawFileInfo;
	}

	public SharedFileInfo getInfo() {
		return info;
	}

	public SharedFileFootprint getFootprint() {
		return footprint;
	}

}
