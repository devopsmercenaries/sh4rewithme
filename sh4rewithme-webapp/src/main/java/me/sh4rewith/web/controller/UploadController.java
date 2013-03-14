package me.sh4rewith.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import me.sh4rewith.domain.RawFile;
import me.sh4rewith.domain.RawFileInfo;
import me.sh4rewith.domain.SharedFile;
import me.sh4rewith.domain.SharedFileInfo;
import me.sh4rewith.service.SharedFilesService;
import me.sh4rewith.utils.DigestUtils;
import me.sh4rewith.web.forms.ToBeSharedFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/upload")
public class UploadController {

	@Autowired
	SharedFilesService service;

	@RequestMapping(method = RequestMethod.POST)
	public String storeFile(ToBeSharedFile toBeSharedFile) throws IOException {
		final String rawFileInfoId = DigestUtils.md5Hash(toBeSharedFile.getFile().getContentType().getBytes(), toBeSharedFile.getFile().getOriginalFilename().getBytes());
		final String rawFileId = DigestUtils.md5Hash(toBeSharedFile.getFile().getBytes(), rawFileInfoId.getBytes());
		RawFileInfo rawFileInfo = new RawFileInfo.Builder()
				.setId(rawFileInfoId)
				.setContentType(toBeSharedFile.getFile().getContentType())
				.setSize(toBeSharedFile.getFile().getSize())
				.setOriginalFileName(toBeSharedFile.getFile().getOriginalFilename())
				.setRawFileId(rawFileId)
				.build();
		RawFile rawFile = new RawFile.Builder()
				.setId(rawFileId)
				.setBytes(toBeSharedFile.getFile().getBytes())
				.build();
		SharedFileInfo sharedFileInfo = new SharedFileInfo.Builder()
				.setDescription(toBeSharedFile.getDescription())
				.setCreationDate(new Date())
				.setExpirationDate(new Date(new Date().getTime() + (TimeUnit.MINUTES.toMillis(toBeSharedFile.getExpiration()))))
				.setOwner(SecurityContextHolder.getContext().getAuthentication().getName())
				.build();
		SharedFile sharedFile = new SharedFile.Builder()
				.setRawFile(rawFile)
				.setRawFileInfo(rawFileInfo)
				.setSharedFileInfo(sharedFileInfo)
				.build();
		service.store(sharedFile);
		return "redirect:shared-files";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String loadForm(ModelMap model) {
		ToBeSharedFile toBeSharedFile = new ToBeSharedFile();
		model.put("toBeSharedFile", toBeSharedFile);
		return "uploadForm";
	}
}
