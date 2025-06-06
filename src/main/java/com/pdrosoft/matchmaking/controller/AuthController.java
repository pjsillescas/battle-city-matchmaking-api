package com.pdrosoft.matchmaking.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pdrosoft.matchmaking.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authManager;

	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping("/login")
	public Map<String, String> login(@RequestBody Map<String, String> request) {
		Authentication auth = authManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.get("username"), request.get("password")));

		String token = jwtUtil.generateToken(auth.getName());
		return Map.of("token", token);
	}
}
