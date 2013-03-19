package me.sh4rewith.service;

import java.util.List;

import me.sh4rewith.domain.DoSecured;
import me.sh4rewith.domain.SharedFile;
import me.sh4rewith.domain.SharedFileDescriptor;
import me.sh4rewith.domain.SharedFileInfo;
import me.sh4rewith.persistence.Repositories;
import me.sh4rewith.utils.exceptions.AccessDeniedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SharedFilesService {
	@Autowired
	Repositories repositories;

	public SharedFile getRawFileBytes(String sharedFileFootprintId,
			String buddyId) {
		SharedFile result;
		SharedFileInfo sharedFileInfo = repositories.sharedFilesRepository()
				.findSharedFileInfoByFootprintId(sharedFileFootprintId);
		if (sharedFileInfo.can(buddyId, DoSecured.DOWNLOAD)) {
			result = repositories.sharedFilesRepository()
					.getSharedFileByFootprintId(sharedFileFootprintId);
		} else {
			throw new AccessDeniedException();
		}
		return result;
	}

	public List<SharedFileDescriptor> getPublicStream(String buddyId) {
		return repositories.sharedFilesRepository().getPublicStream(buddyId);
	}

	public List<SharedFileDescriptor> getPersonalStream(String buddyId) {
		return repositories.sharedFilesRepository().getPersonalStream(buddyId);
	}

	public void deleteByFootprintId(String sharedFileFootprintId, String buddyId) {
		SharedFileInfo sharedFileInfo = repositories.sharedFilesRepository()
				.findSharedFileInfoByFootprintId(sharedFileFootprintId);
		if (sharedFileInfo.can(buddyId, DoSecured.DELETION)) {
			repositories.sharedFilesRepository().deleteByFootprintId(
					sharedFileFootprintId);
		} else {
			throw new AccessDeniedException();
		}
	}

	public void turnPublic(String sharedFileInfoId, String buddyId) {
		SharedFileInfo sharedFileInfo = repositories.sharedFilesRepository()
				.findSharedFileInfoById(sharedFileInfoId);
		if (sharedFileInfo.can(buddyId, DoSecured.PRIVACY_UPDATE)) {
			repositories.sharedFilesRepository().turnPublic(sharedFileInfo);
		} else {
			throw new AccessDeniedException();
		}

	}

	public void turnPrivate(String sharedFileInfoId, String buddyId) {
		SharedFileInfo sharedFileInfo = repositories.sharedFilesRepository()
				.findSharedFileInfoById(sharedFileInfoId);
		if (sharedFileInfo.can(buddyId, DoSecured.PRIVACY_UPDATE)) {
			repositories.sharedFilesRepository().turnPrivate(sharedFileInfo);
		} else {
			throw new AccessDeniedException();
		}
	}

	public void turnProtected(String sharedFileInfoId, String buddyId) {
		SharedFileInfo sharedFileInfo = repositories.sharedFilesRepository()
				.findSharedFileInfoById(sharedFileInfoId);
		if (sharedFileInfo.can(buddyId, DoSecured.PRIVACY_UPDATE)) {
			repositories.sharedFilesRepository().turnProtected(sharedFileInfo);
		} else {
			throw new AccessDeniedException();
		}
	}

	public void addBuddyToFile(String sharedFileInfoId, String otherBuddyId,
			String buddyId) {
		SharedFileInfo sharedFileInfo = repositories.sharedFilesRepository()
				.findSharedFileInfoById(sharedFileInfoId);
		if (sharedFileInfo.can(buddyId, DoSecured.PRIVACY_UPDATE)) {
			repositories.sharedFilesRepository()
					.appendBuddyToSharedFileInfoById(sharedFileInfoId,
							otherBuddyId);
		} else {
			throw new AccessDeniedException();
		}
	}

	public void store(SharedFile sharedFile) {
		repositories.sharedFilesRepository().store(sharedFile);
	}

}
