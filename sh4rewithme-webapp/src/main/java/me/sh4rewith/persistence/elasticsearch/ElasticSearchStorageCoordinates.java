package me.sh4rewith.persistence.elasticsearch;

import javax.validation.constraints.NotNull;

import me.sh4rewith.domain.StorageCoordinates;
import me.sh4rewith.domain.ValidatedImmutableBuilderBase;

public class ElasticSearchStorageCoordinates implements StorageCoordinates {

	private String coordinates;

	private ElasticSearchStorageCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public static class Builder extends
			ValidatedImmutableBuilderBase<ElasticSearchStorageCoordinates> {
		@NotNull
		private String coordinates;

		public Builder(String coordinates) {
			this.coordinates = coordinates;
		}

		@Override
		protected ElasticSearchStorageCoordinates doBuild() {
			return new ElasticSearchStorageCoordinates(coordinates);
		}
	}

	@Override
	public StorageType storageType() {
		return StorageType.ELASTICSEARCH;
	}

	@Override
	public String coordinates() {
		return coordinates;
	}

}
