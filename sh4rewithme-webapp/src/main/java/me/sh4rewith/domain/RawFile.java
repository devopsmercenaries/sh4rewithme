package me.sh4rewith.domain;

import javax.validation.constraints.NotNull;

public class RawFile {

	private final String id;

	private final byte[] bytes;

	public static class Builder extends ValidatedImmutableBuilderBase<RawFile> {
		@NotNull
		private String id;
		@NotNull
		private byte[] bytes;

		public Builder setBytes(byte[] bytes) {
			this.bytes = bytes;
			return this;
		}

		public Builder setId(String id) {
			this.id = id;
			return this;
		}

		@Override
		protected RawFile doBuild() {
			return new RawFile(id, bytes);
		}
	}

	public RawFile(String id, byte[] bytes) {
		this.id = id;
		this.bytes = bytes;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public String getId() {
		return id;
	}

}
