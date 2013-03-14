package me.sh4rewith.web.forms;

import org.springframework.web.multipart.MultipartFile;

public class ToBeSharedFile {
	private MultipartFile file;
	private String description;
	private Integer expiration;

	public ToBeSharedFile() {
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public Integer getExpiration() {
        return expiration;
    }

    public void setExpiration(Integer expiration) {
        this.expiration = expiration;
    }
}
