package me.sh4rewith.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import me.sh4rewith.domain.SharedFile;
import me.sh4rewith.service.SharedFilesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/download")
public class DownloadController {

	@Autowired
	SharedFilesService service;

	@RequestMapping("/by-footprint/{sharedFileFootprintId}/{originalFileName}")
	public void getContent(
			@PathVariable(value = "sharedFileFootprintId") String sharedFileFootprintId,
			HttpServletResponse response) throws IOException {
		final String userId = SecurityContextHolder.getContext()
				.getAuthentication().getName();
		SharedFile doc = service.getRawFileBytes(sharedFileFootprintId, userId);
		response.setContentLength(doc.getRawFileInfo().getSize().intValue());
		response.setContentType(doc.getRawFileInfo().getContentType());
		response.getOutputStream().write(
				service.getFileFromStorage(doc.getRawFile()
						.getStorageCoordinates()));
	}
}
