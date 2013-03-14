package me.sh4rewith.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import me.sh4rewith.domain.RegistrationStatus;
import me.sh4rewith.domain.UserInfo;
import me.sh4rewith.service.UsersService;
import me.sh4rewith.utils.DigestUtils;
import me.sh4rewith.web.forms.UserRegistration;

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

	@Autowired
	UsersService service;

	@RequestMapping(
			value = "/user-registration",
			method = RequestMethod.POST)
	public String index(@Valid UserRegistration userRegistration, BindingResult bindingResult, ModelMap modelMap) throws IOException {
		String result = "redirect:user-registration-success";
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
			service.registerAndSendConfirmationEmail(userInfo);
		} else {
			// HACK because Thymeleaf Spring 3 support doesn't (yet) support global errors.
			// Therefore I copy them as field errors with id 'global_error'.
			List<List<String>> errorsList = new ArrayList<List<String>>();
			for (ObjectError globalError : bindingResult.getGlobalErrors()) {
				errorsList.add(Arrays.asList(globalError.getCodes()));
			}
			modelMap.addAttribute("global_errors", errorsList);
			userRegistration.setPassword("");
			userRegistration.setConfirmedPassword("");
			result = "userRegistrationForm";
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
	public String getRegistrationSuccess() {
		return "userRegistrationSuccess";
	}

	@RequestMapping(
			value = "/user-registration-confirm/{userInfoHash}",
			method = RequestMethod.GET)
	public String confirm(@PathVariable("userInfoHash") String userInfoHash, ModelMap modelMap) {
		UserInfo userInfo = service.changeRegistrationStatusByHash(userInfoHash, RegistrationStatus.REGISTERED);
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