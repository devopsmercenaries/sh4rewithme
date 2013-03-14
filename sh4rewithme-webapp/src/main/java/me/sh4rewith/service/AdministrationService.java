package me.sh4rewith.service;


import java.util.Date;

import me.sh4rewith.persistence.SharedFilesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class AdministrationService {

	@Autowired
	private SharedFilesRepository sharedFilesRepository;

	@Scheduled(fixedDelay = 60000)
	public void purgeFile() {
		/* Integer deletedDocumentNumber = */sharedFilesRepository.deleteOlderThan(new Date());
	}

}
