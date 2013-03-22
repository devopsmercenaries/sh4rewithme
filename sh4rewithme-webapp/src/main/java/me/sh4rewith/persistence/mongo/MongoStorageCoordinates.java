package me.sh4rewith.persistence.mongo;

import javax.validation.constraints.NotNull;

import me.sh4rewith.domain.StorageCoordinates;
import me.sh4rewith.domain.ValidatedImmutableBuilderBase;

public class MongoStorageCoordinates implements StorageCoordinates {

	private final String coordinates;

	private MongoStorageCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public static class Builder extends
			ValidatedImmutableBuilderBase<MongoStorageCoordinates> {
		@NotNull
		private String coordinates;

		public Builder(String coordinates) {
			this.coordinates = coordinates;
		}

		@Override
		protected MongoStorageCoordinates doBuild() {
			return new MongoStorageCoordinates(coordinates);
		}
	}

	@Override
	public StorageType storageType() {
		return StorageType.MONGO;
	}

	@Override
	public String coordinates() {
		return this.coordinates;
	}

}
