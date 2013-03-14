package me.sh4rewith.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

	/** Login form. */
	@RequestMapping("/login")
	public String login() {
		return "login";
	}

	/** Login form with error. */
	@RequestMapping("/login-failure")
	public String loginError(ModelMap model) {
		model.addAttribute("loginError", true);
		return "login";
	}
}