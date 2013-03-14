package me.sh4rewith.domain;

import javax.validation.constraints.NotNull;

public class SharedFile {

	private final RawFile rawFile;

	private final RawFileInfo rawFileInfo;

	private final SharedFileInfo info;

	private final SharedFileFootprint footprint;

	public static class Builder extends ValidatedImmutableBuilderBase<SharedFile> {
		@NotNull
		private RawFile rawFile;
		@NotNull
		private SharedFileInfo info;
		@NotNull
		private RawFileInfo rawFileInfo;

		public Builder() {
		}

		public Builder setRawFile(RawFile rawFile) {
			this.rawFile = rawFile;
			return this;
		}

		public Builder setSharedFileInfo(SharedFileInfo info) {
			this.info = info;
			return this;
		}

		public Builder setRawFileInfo(RawFileInfo rawFileInfo) {
			this.rawFileInfo = rawFileInfo;
			return this;
		}

		@Override
		protected SharedFile doBuild() {
			return new SharedFile(rawFile, rawFileInfo, info);
		}

	}

	private SharedFile(RawFile rawFile, RawFileInfo rawFileInfo, SharedFileInfo info) {
		this.footprint = new SharedFileFootprint.Builder()
				.setRawFileId(rawFile.getId())
				.setSharedFileInfoId(info.getId())
                .setCreationDate(info.getCreationDate())
                .setExpiration(info.getExpirationDate())
				.build();
		this.rawFile = rawFile;
		this.rawFileInfo = rawFileInfo;
		this.info = info;
	}

	public RawFile getRawFile() {
		return rawFile;
	}

	public SharedFileInfo getInfo() {
		return info;
	}

	public SharedFileFootprint getFootprint() {
		return footprint;
	}

	public RawFileInfo getRawFileInfo() {
		return rawFileInfo;
	}

}
