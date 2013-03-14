package me.sh4rewith.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import me.sh4rewith.service.UsersService;
import me.sh4rewith.utils.SecureContextUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserInfoController {

	@Autowired
	UsersService service;

	@RequestMapping(
			value = "/user-info",
			method = RequestMethod.GET)
	public String getCurrentUserInfo(ModelMap model, HttpServletResponse response) {
		String view = "userInfo";
		model.put("userInfo", service.getUserInfoById(SecureContextUtil.getUsername()));
		return view;
	}

	@RequestMapping(
			value = "/buddies",
			method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public List<String> getAllUserIds(ModelMap model, HttpServletResponse response) {
		return service.getBuddiesOfUser(SecureContextUtil.getUsername());
	}
}
