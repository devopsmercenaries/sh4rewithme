package me.sh4rewith.service;

import java.util.List;

import me.sh4rewith.domain.DoSecured;
import me.sh4rewith.domain.SharedFile;
import me.sh4rewith.domain.SharedFileDescriptor;
import me.sh4rewith.domain.SharedFileInfo;
import me.sh4rewith.persistence.SharedFilesRepository;
import me.sh4rewith.utils.exceptions.AccessDeniedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SharedFilesService {
	@Autowired
	SharedFilesRepository repository;

	public SharedFile getRawFileBytes(String sharedFileFootprintId, String buddyId) {
		SharedFile result;
		SharedFileInfo sharedFileInfo = repository.findSharedFileInfoByFootprintId(sharedFileFootprintId);
		if (sharedFileInfo.can(buddyId, DoSecured.DOWNLOAD)) {
			result = repository.getSharedFileByFootprintId(sharedFileFootprintId);
		} else {
			throw new AccessDeniedException();
		}
		return result;
	}

	public List<SharedFileDescriptor> getPublicStream(String buddyId) {
		return repository.getPublicStream(buddyId);
	}

	public List<SharedFileDescriptor> getPersonalStream(String buddyId) {
		return repository.getPersonalStream(buddyId);
	}

	public void deleteByFootprintId(String sharedFileFootprintId, String buddyId) {
		SharedFileInfo sharedFileInfo = repository.findSharedFileInfoByFootprintId(sharedFileFootprintId);
		if (sharedFileInfo.can(buddyId, DoSecured.DELETION)) {
			repository.deleteByFootprintId(sharedFileFootprintId);
		} else {
			throw new AccessDeniedException();
		}
	}

	public void turnPublic(String sharedFileInfoId, String buddyId) {
		SharedFileInfo sharedFileInfo = repository.findSharedFileInfoById(sharedFileInfoId);
		if (sharedFileInfo.can(buddyId, DoSecured.PRIVACY_UPDATE)) {
			repository.turnPublic(sharedFileInfo);
		} else {
			throw new AccessDeniedException();
		}

	}

	public void turnPrivate(String sharedFileInfoId, String buddyId) {
		SharedFileInfo sharedFileInfo = repository.findSharedFileInfoById(sharedFileInfoId);
		if (sharedFileInfo.can(buddyId, DoSecured.PRIVACY_UPDATE)) {
			repository.turnPrivate(sharedFileInfo);
		} else {
			throw new AccessDeniedException();
		}
	}

	public void turnProtected(String sharedFileInfoId, String buddyId) {
		SharedFileInfo sharedFileInfo = repository.findSharedFileInfoById(sharedFileInfoId);
		if (sharedFileInfo.can(buddyId, DoSecured.PRIVACY_UPDATE)) {
			repository.turnProtected(sharedFileInfo);
		} else {
			throw new AccessDeniedException();
		}
	}

	public void addBuddyToFile(String sharedFileInfoId, String otherBuddyId, String buddyId) {
		SharedFileInfo sharedFileInfo = repository.findSharedFileInfoById(sharedFileInfoId);
		if (sharedFileInfo.can(buddyId, DoSecured.PRIVACY_UPDATE)) {
			repository.appendBuddyToSharedFileInfoById(sharedFileInfoId, otherBuddyId);
		} else {
			throw new AccessDeniedException();
		}
	}

	public void store(SharedFile sharedFile) {
		repository.store(sharedFile);
	}

}
