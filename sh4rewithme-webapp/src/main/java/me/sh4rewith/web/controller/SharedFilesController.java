package me.sh4rewith.web.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.sh4rewith.domain.SharedFileDescriptor;
import me.sh4rewith.service.SharedFilesService;
import me.sh4rewith.utils.SecureContextUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/shared-files")
public class SharedFilesController {

	@Autowired
	SharedFilesService service;

	@RequestMapping(
			method = RequestMethod.GET)
	public String listSharedFiles(ModelMap model, HttpServletRequest request) {
		final String username = SecureContextUtil.getUsername();
		List<SharedFileDescriptor> sharedFiles = service.getPublicStream(username);
		List<SharedFileDescriptor> privatelySharedFiles = service.getPersonalStream(username);
		model.put("sharedFiles", sharedFiles);
		model.put("privatelySharedFiles", privatelySharedFiles);
		model.put("currentUser", username);
		return "sharedFiles";
	}

	@RequestMapping(
			value = "/by-footprint/{sharedFileFootprintId}",
			method = RequestMethod.DELETE)
	public void deleteSharedFile(@PathVariable("sharedFileFootprintId") String sharedFileFootprintId, HttpServletResponse response) throws IOException {
		service.deleteByFootprintId(sharedFileFootprintId, SecureContextUtil.getUsername());
	}

	@RequestMapping(
			value = "/privacy/public/{sharedFileInfoId}",
			method = RequestMethod.PUT)
	public void turnSharedFilePublic(@PathVariable("sharedFileInfoId") String sharedFileInfoId, HttpServletResponse response) throws IOException {
		service.turnPublic(sharedFileInfoId, SecureContextUtil.getUsername());
	}

	@RequestMapping(
			value = "/privacy/private/{sharedFileInfoId}",
			method = RequestMethod.PUT)
	public void turnSharedFilePrivate(@PathVariable("sharedFileInfoId") String sharedFileInfoId, HttpServletResponse response) throws IOException {
		service.turnPrivate(sharedFileInfoId, SecureContextUtil.getUsername());
	}

	@RequestMapping(
			value = "/privacy/protected/{sharedFileInfoId}",
			method = RequestMethod.PUT)
	public void turnSharedFileProtected(@PathVariable("sharedFileInfoId") String sharedFileInfoId, HttpServletResponse response) throws IOException {
		service.turnProtected(sharedFileInfoId, SecureContextUtil.getUsername());
	}

	@RequestMapping(
			value = "/by-info-id/{sharedFileInfoId}/buddies/{buddyId}",
			method = RequestMethod.PUT)
	public void addBuddyToFile(@PathVariable("sharedFileInfoId") String sharedFileInfoId, @PathVariable("buddyId") String buddyId, HttpServletResponse response) throws IOException {
		service.addBuddyToFile(sharedFileInfoId, buddyId, SecureContextUtil.getUsername());
	}

}
