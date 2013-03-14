package me.sh4rewith.persistence;


import java.util.Date;
import java.util.List;

import me.sh4rewith.domain.SharedFile;
import me.sh4rewith.domain.SharedFileDescriptor;
import me.sh4rewith.domain.SharedFileInfo;

public interface SharedFilesRepository {

	public void store(SharedFile sharedFile);

	public List<SharedFile> findAll();

	public SharedFile getSharedFileByFootprintId(String footprintId);

	List<SharedFileDescriptor> findAllDescriptors();

	public void deleteByFootprintId(String sharedFileFootprint);

	public List<SharedFileDescriptor> getPublicStream(String buddyId);

	public List<SharedFileDescriptor> getPersonalStream(String buddyId);

	public SharedFileInfo findSharedFileInfoById(String sharedFileInfoId);

	public void turnPublic(SharedFileInfo sharedFileInfo);

	public void turnPrivate(SharedFileInfo sharedFileInfo);

	public void turnProtected(SharedFileInfo sharedFileInfo);

	public SharedFileInfo findSharedFileInfoByFootprintId(String sharedFileFootprintId);

	public Long countAll();

    public Integer deleteOlderThan(Date expirationDate);

	public void appendBuddyToSharedFileInfoById(String sharedFileInfoId, String buddy);

}
