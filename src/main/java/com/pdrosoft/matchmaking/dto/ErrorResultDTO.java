package com.pdrosoft.matchmaking.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResultDTO {
	private Instant timestamp;
	private String message;
}
