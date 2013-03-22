package me.sh4rewith.persistence.filesystem;

import java.net.MalformedURLException;
import java.net.URL;

import javax.validation.constraints.NotNull;

import me.sh4rewith.domain.StorageCoordinates;
import me.sh4rewith.domain.ValidatedImmutableBuilderBase;

public class FileSystemStorageCoordinates implements StorageCoordinates {

	private String coordinates;

	private FileSystemStorageCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public static class Builder extends
			ValidatedImmutableBuilderBase<FileSystemStorageCoordinates> {
		@NotNull
		private String coordinates;

		public Builder(String coordinates) {
			URL url;
			try {
				url = new URL(coordinates);
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException("File URL is not valid", e);
			}

			this.coordinates = url.toExternalForm();
		}

		@Override
		protected FileSystemStorageCoordinates doBuild() {
			return new FileSystemStorageCoordinates(coordinates);
		}
	}

	@Override
	public StorageType storageType() {
		return StorageType.FILESYSTEM;
	}

	@Override
	public String coordinates() {
		return coordinates;
	}

}
