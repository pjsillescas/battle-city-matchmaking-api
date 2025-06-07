package com.pdrosoft.matchmaking.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pdrosoft.matchmaking.dto.PlayerDTO;
import com.pdrosoft.matchmaking.security.JwtUtil;
import com.pdrosoft.matchmaking.service.MatchmakingService;

import lombok.NonNull;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@NonNull
	private final AuthenticationManager authManager;

	@NonNull
	private final JwtUtil jwtUtil;

	@NonNull
	private final MatchmakingService matchmakingService;

	public AuthController(@Autowired AuthenticationManager authManager, @Autowired JwtUtil jwtUtil,
			@Autowired MatchmakingService matchmakingService) {
		this.authManager = authManager;
		this.jwtUtil = jwtUtil;
		this.matchmakingService = matchmakingService;
	}

	@PostMapping("/login")
	public Map<String, String> login(@RequestBody Map<String, String> request) {
		Authentication auth = authManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.get("username"), request.get("password")));

		String token = jwtUtil.generateToken(auth.getName());
		return Map.of("token", token);
	}

	@PutMapping("/signup")
	public PlayerDTO signup(@RequestBody Map<String, String> request) {

		return matchmakingService.addPlayer(request.get("username"), request.get("password"));
	}

}
