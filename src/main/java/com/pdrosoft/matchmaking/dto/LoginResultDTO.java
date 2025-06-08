package com.pdrosoft.matchmaking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResultDTO {
	private String token;
}
