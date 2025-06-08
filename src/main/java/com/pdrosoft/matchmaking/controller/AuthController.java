package com.pdrosoft.matchmaking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pdrosoft.matchmaking.dto.LoginResultDTO;
import com.pdrosoft.matchmaking.dto.PlayerDTO;
import com.pdrosoft.matchmaking.dto.UserAuthDTO;
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
	public LoginResultDTO login(@RequestBody UserAuthDTO request) {
		Authentication auth = authManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		String token = jwtUtil.generateToken(auth.getName());
		return LoginResultDTO.builder().token(token).build();
	}

	@PutMapping("/signup")
	public PlayerDTO signup(@RequestBody UserAuthDTO request) {

		return matchmakingService.addPlayer(request.getUsername(), request.getPassword());
	}

}
