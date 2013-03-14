package me.sh4rewith.persistence;


import java.util.List;

import me.sh4rewith.domain.RegistrationStatus;
import me.sh4rewith.domain.UserInfo;

public interface UsersRepository {

	void store(UserInfo userInfo);

	UserInfo getUserInfoById(String userId);

	List<UserInfo> findAllUserInfoBut(String userId);

	Long countAll();

	UserInfo getUserInfoByHash(String userHash);

	void changeRegistrationStatusByHash(String userInfoHash, RegistrationStatus registered);

}
