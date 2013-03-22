package me.sh4rewith.config.persistence.bootstrap;

import me.sh4rewith.domain.RegistrationStatus;
import me.sh4rewith.domain.UserInfo;
import me.sh4rewith.persistence.UsersRepository;
import me.sh4rewith.utils.DigestUtils;

public abstract class AbstractInitBootstrap {

	protected abstract UsersRepository getUsersRepository();

	protected void createAndStoreDemoUsers() {
		UsersRepository usersRepository = getUsersRepository();
		UserInfo zeus = new UserInfo.Builder("zeus").setFirstname("Jean-Paul")
				.setLastname("Zeus")
				.setCredentials(DigestUtils.sha256Hex("zeus"))
				.setEmail("zeus@sh4rewith.me")
				.setRegistrationStatus(RegistrationStatus.REGISTERED).build();
		UserInfo hera = new UserInfo.Builder("hera").setFirstname("Martine")
				.setLastname("Héra")
				.setCredentials(DigestUtils.sha256Hex("hera"))
				.setEmail("hera@sh4rewith.me")
				.setRegistrationStatus(RegistrationStatus.REGISTERED).build();
		UserInfo poseidon = new UserInfo.Builder("poseidon")
				.setFirstname("Marcel").setLastname("Poseïdon")
				.setCredentials(DigestUtils.sha256Hex("poseidon"))
				.setEmail("poseidon@sh4rewith.me")
				.setRegistrationStatus(RegistrationStatus.REGISTERED).build();
		UserInfo athena = new UserInfo.Builder("athena").setFirstname("Slip")
				.setLastname("Athena")
				.setCredentials(DigestUtils.sha256Hex("Athena"))
				.setEmail("athena@sh4rewith.me")
				.setRegistrationStatus(RegistrationStatus.REGISTERED).build();

		usersRepository.store(zeus);
		usersRepository.store(hera);
		usersRepository.store(poseidon);
		usersRepository.store(athena);

	}
}
