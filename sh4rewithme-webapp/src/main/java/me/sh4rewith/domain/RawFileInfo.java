package me.sh4rewith.domain;


import javax.validation.constraints.NotNull;

import me.sh4rewith.utils.DigestUtils;

public class RawFileInfo {
	private final String id;

	private final String contentType;

	private final Long size;

	private final String originalFileName;

	private final String rawFileId;

	public static class Builder extends ValidatedImmutableBuilderBase<RawFileInfo> {
		private String id;
		@NotNull
		private String rawFileId;
		@NotNull
		private String contentType;
		@NotNull
		private Long size;
		@NotNull
		private String originalFileName;

		public Builder setContentType(String contentType) {
			this.contentType = contentType;
			return this;
		}

		public Builder setSize(Long size) {
			this.size = size;
			return this;
		}

		public Builder setOriginalFileName(String originalFileName) {
			this.originalFileName = originalFileName;
			return this;
		}

		public Builder setId(String id) {
			this.id = id;
			return this;
		}

		public Builder setRawFileId(String rawFileId) {
			this.rawFileId = rawFileId;
			return this;
		}

		@Override
		protected RawFileInfo doBuild() {
			if (id == null) {
				id = DigestUtils.md5Hash(contentType.getBytes(), originalFileName.getBytes());
			}
			RawFileInfo rawFileInfo = new RawFileInfo(id, rawFileId, contentType, size, originalFileName);
			return rawFileInfo;
		}
	}

	public RawFileInfo(String id, String rawFileId, String contentType, Long size, String fileNameExtension) {
		this.id = id;
		this.rawFileId = rawFileId;
		this.contentType = contentType;
		this.size = size;
		this.originalFileName = fileNameExtension;
	}

	public String getContentType() {
		return contentType;
	}

	public Long getSize() {
		return size;
	}

	public String getOriginalFileExtension() {
		String fileExtension = "";
		int extensionIndex = this.originalFileName.lastIndexOf(".");
		if (extensionIndex != -1) {
			fileExtension = this.originalFileName.substring(extensionIndex + 1);
		}
		return fileExtension;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public String getId() {
		return id;
	}

	public String getRawFileId() {
		return rawFileId;
	}

}
