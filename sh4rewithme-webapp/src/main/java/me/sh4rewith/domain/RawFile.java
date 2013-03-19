package me.sh4rewith.domain;

import javax.validation.constraints.NotNull;

public class RawFile {

	private final String id;

	private final StorageCoordinates coordinates;

	public static class Builder extends ValidatedImmutableBuilderBase<RawFile> {
		@NotNull
		private String id;
		@NotNull
		private StorageCoordinates coordinates;

		public Builder setStorageCoordinates(StorageCoordinates coordinates) {
			this.coordinates = coordinates;
			return this;
		}

		public Builder setId(String id) {
			this.id = id;
			return this;
		}

		@Override
		protected RawFile doBuild() {
			return new RawFile(id, coordinates);
		}
	}

	public RawFile(String id, StorageCoordinates coordinates) {
		this.id = id;
		this.coordinates = coordinates;
	}

	public StorageCoordinates getStorageCoordinates() {
		return coordinates;
	}

	public String getId() {
		return id;
	}

}
