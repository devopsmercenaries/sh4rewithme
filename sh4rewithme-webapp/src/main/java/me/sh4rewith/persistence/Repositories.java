package me.sh4rewith.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Repositories {

	@Autowired
	private UsersRepository usersRepository;
	@Autowired
	private SharedFilesRepository sharedFilesRepository;

	public UsersRepository usersRepository() {
		return usersRepository;
	}

	public SharedFilesRepository sharedFilesRepository() {
		return sharedFilesRepository;
	}
}
