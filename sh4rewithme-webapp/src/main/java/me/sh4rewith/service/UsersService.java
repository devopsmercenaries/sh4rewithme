package me.sh4rewith.service;

import java.util.List;
import java.util.NoSuchElementException;

import me.sh4rewith.domain.RegistrationStatus;
import me.sh4rewith.domain.UserInfo;
import me.sh4rewith.persistence.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

@Service
public class UsersService {

	@Autowired
	Repositories repositories;
	@Autowired(required = false)
	MailSender mailSender;
	@Autowired
	Environment env;

	public UserInfo getUserInfoById(String userId) {
		if (userId == null) {
			throw new NoSuchElementException();
		}
		return repositories.usersRepository().getUserInfoById(userId);
	}

	public List<String> getBuddiesOfUser(String userId) {
		List<String> result = null;
		if (userId != null) {
			List<UserInfo> userInfo = repositories.usersRepository()
					.findAllUserInfoBut(userId);
			result = Lists.transform(userInfo,
					new Function<UserInfo, String>() {
						@Override
						public String apply(UserInfo userInfo) {
							return userInfo.getId();
						}
					});
		} else {
			throw new NoSuchElementException();
		}
		return result;
	}

	public void registerAndSendConfirmationEmail(UserInfo userInfo) {
		repositories.usersRepository().store(userInfo);
		if (mailSender != null) {
			SimpleMailMessage simpleMessage = new SimpleMailMessage();
			simpleMessage.setFrom("admin@sh4rewith.me");
			simpleMessage.setTo(userInfo.getEmail());
			simpleMessage.setReplyTo("admin@sh4rewith.me");
			simpleMessage
					.setSubject("Complete your registration to Sh4rewith.me.");
			simpleMessage.setText("Welcome" + userInfo.getFirstname() + " "
					+ userInfo.getLastname() + " <a href=\""
					+ env.getRequiredProperty("registration.confirm.url")
					+ userInfo.getUserHash()
					+ "\">Click here to confirm your registration</a>");
			mailSender.send(simpleMessage);
		}
	}

	public UserInfo changeRegistrationStatusByHash(String userInfoHash,
			RegistrationStatus registrationStatus) {
		UserInfo userInfo = repositories.usersRepository().getUserInfoByHash(
				userInfoHash);
		if (userInfo != null
				&& userInfo.getRegistrationStatus() == RegistrationStatus.NOT_YET_REGISTERED) {
			repositories.usersRepository().changeRegistrationStatusByHash(
					userInfoHash, registrationStatus);
			userInfo = repositories.usersRepository().getUserInfoByHash(
					userInfoHash);
		} else {
			throw new NoSuchElementException();
		}
		return userInfo;
	}

}
