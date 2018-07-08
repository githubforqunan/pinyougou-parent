package com.pinyougou.manager.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

	@RequestMapping("/login")
	public Map<String, String> login(){
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Map<String, String> map = new HashMap<>();
		map.put("loginName", name);
		return map;
	}
}
