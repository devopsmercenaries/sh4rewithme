package me.sh4rewith.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import me.sh4rewith.domain.RawFile;
import me.sh4rewith.domain.RawFileInfo;
import me.sh4rewith.domain.SharedFile;
import me.sh4rewith.domain.SharedFileInfo;
import me.sh4rewith.domain.StorageCoordinates;
import me.sh4rewith.domain.StorageCoordinates.StorageType;
import me.sh4rewith.service.SharedFilesService;
import me.sh4rewith.utils.DigestUtils;
import me.sh4rewith.web.forms.ToBeSharedFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/upload")
public class UploadController {

	Logger LOG = LoggerFactory.getLogger(UploadController.class);

	@Autowired
	SharedFilesService service;

	@RequestMapping(method = RequestMethod.POST)
	public String storeFile(ToBeSharedFile toBeSharedFile) throws IOException {
		MultipartFile file = toBeSharedFile.getFile();
		final String rawFileInfoId = DigestUtils.md5Hash(file.getContentType().getBytes(), file.getOriginalFilename().getBytes());
		final String rawFileId = DigestUtils.md5Hash(file
				.getBytes(), rawFileInfoId.getBytes());
		final StorageCoordinates coordinates = service.storeFile(
				StorageType.FILESYSTEM,
				file.getBytes());
		RawFileInfo rawFileInfo = new RawFileInfo.Builder()
				.setId(rawFileId + "-info")
				.setContentType(file.getContentType())
				.setSize(file.getSize())
				.setOriginalFileName(
						file.getOriginalFilename())
				.setRawFileId(rawFileId).build();
		RawFile rawFile = new RawFile.Builder().setId(rawFileId)
				.setStorageCoordinates(coordinates).build();
		String owner = SecurityContextHolder.getContext().getAuthentication()
		        .getName();
		SharedFileInfo sharedFileInfo = new SharedFileInfo.Builder()
				.setDescription(toBeSharedFile.getDescription())
				.setCreationDate(new Date())
				.setExpirationDate(
						new Date(new Date().getTime()
								+ (TimeUnit.MINUTES.toMillis(toBeSharedFile
										.getExpiration()))))
.setOwner(owner)
		        .build();
		SharedFile sharedFile = new SharedFile.Builder().setRawFile(rawFile)
				.setRawFileInfo(rawFileInfo).setSharedFileInfo(sharedFileInfo)
				.build();
		service.store(sharedFile);
		LOG.info("New File for " + owner + " : " + file.getOriginalFilename());
		return "redirect:shared-files";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String loadForm(ModelMap model) {
		ToBeSharedFile toBeSharedFile = new ToBeSharedFile();
		model.put("toBeSharedFile", toBeSharedFile);
		return "uploadForm";
	}
}
