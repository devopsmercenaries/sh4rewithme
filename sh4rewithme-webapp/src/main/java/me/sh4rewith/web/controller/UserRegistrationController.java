package me.sh4rewith.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import me.sh4rewith.domain.RegistrationStatus;
import me.sh4rewith.domain.UserInfo;
import me.sh4rewith.service.ConfirmationMechanism;
import me.sh4rewith.service.UsersService;
import me.sh4rewith.utils.DigestUtils;
import me.sh4rewith.web.forms.UserRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserRegistrationController {

	Logger LOG = LoggerFactory.getLogger(UserRegistrationController.class);

	@Autowired
	UsersService service;

	@RequestMapping(
			value = "/user-registration",
			method = RequestMethod.POST)
	public String index(@Valid UserRegistration userRegistration,
			BindingResult bindingResult, ModelMap modelMap) throws IOException {
		String result = "userRegistrationSuccess";
		if (!bindingResult.hasErrors()) {
			UserInfo userInfo = new UserInfo.Builder(userRegistration.getId())
					.setEmail(userRegistration.getEmail())
					.setFirstname(userRegistration.getFirstname())
					.setLastname(userRegistration.getLastname())
					.setCredentials(userRegistration.getPassword())
					.setUserHash(DigestUtils.md5Hash(
							userRegistration.getId(),
							userRegistration.getEmail(),
							userRegistration.getFirstname(),
							userRegistration.getLastname(),
							userRegistration.getPassword())
					)
					.build();
			ConfirmationMechanism confirmationMechanism = service
					.registerAndNotifyConfirmation(userInfo);
			switch (confirmationMechanism) {
			case MAIL:
				modelMap.addAttribute("mailConfirmation", true);
				break;
			case DIRECT:
				modelMap.addAttribute("directConfirmation", true);
				modelMap.addAttribute("confirmationId",
						confirmationMechanism.getConfirmationId());
				break;
			default:
				break;
			}
			LOG.info("New User : " + userRegistration.getEmail());
		} else {
			// HACK because Thymeleaf Spring 3 support doesn't (yet) support
			// global errors.
			// Therefore I copy them as field errors with id 'global_error'.
			List<List<String>> errorsList = new ArrayList<List<String>>();
			for (ObjectError globalError : bindingResult.getGlobalErrors()) {
				errorsList.add(Arrays.asList(globalError.getCodes()));
			}
			modelMap.addAttribute("global_errors", errorsList);
			userRegistration.setPassword("");
			userRegistration.setConfirmedPassword("");
			result = "userRegistrationForm";
			LOG.warn("Error creating User : " + userRegistration.getEmail()
			        + ", Messages : " + bindingResult.toString());
		}
		return result;
	}

	@RequestMapping(
			value = "/user-registration",
			method = RequestMethod.GET)
	public String getRegistrationForm(ModelMap model) {
		UserRegistration userRegistration = new UserRegistration();
		model.put("userRegistration", userRegistration);
		return "userRegistrationForm";
	}

	@RequestMapping(
			value = "/user-registration-success",
			method = RequestMethod.GET)
	public String getRegistrationSuccess(ModelMap model) {
		return "userRegistrationSuccess";
	}

	@RequestMapping(
			value = "/user-registration-confirm/{userInfoHash}",
			method = RequestMethod.GET)
	public String confirm(@PathVariable("userInfoHash") String userInfoHash,
			ModelMap modelMap) {
		UserInfo userInfo = service.changeRegistrationStatusByHash(
				userInfoHash, RegistrationStatus.REGISTERED);
		modelMap.put("userId", userInfo.getId());
		return "redirect:/login";
	}

	@RequestMapping(
			value = "/user-registration-openid",
			method = RequestMethod.GET)
	public String getRegistrationFormFilledWithOpenIdDetails(ModelMap model) {
		UserRegistration userRegistration = new UserRegistration();
		model.put("userRegistration", userRegistration);
		return "userRegistrationForm";
	}
}