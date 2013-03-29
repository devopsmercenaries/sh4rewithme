package me.sh4rewith.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import me.sh4rewith.domain.SharedFile;
import me.sh4rewith.service.SharedFilesService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/download")
public class DownloadController {

	Logger LOG = LoggerFactory.getLogger(DownloadController.class);

	@Autowired
	SharedFilesService service;

	@RequestMapping("/by-footprint/{sharedFileFootprintId}/{originalFileName}")
	public void getContent(
			@PathVariable(value = "sharedFileFootprintId") String sharedFileFootprintId,
			HttpServletResponse response) throws IOException {
		final String userId = SecurityContextHolder.getContext()
				.getAuthentication().getName();
		LOG.info("Download file for " + userId + " for " + sharedFileFootprintId);

		SharedFile doc = service.getRawFileBytes(sharedFileFootprintId, userId);
		response.setContentLength(doc.getRawFileInfo().getSize().intValue());
		response.setContentType(doc.getRawFileInfo().getContentType());
		LOG.info("Prepared the files");

		response.getOutputStream().write(
				service.getFileFromStorage(doc.getRawFile()
						.getStorageCoordinates()));
	}
}
