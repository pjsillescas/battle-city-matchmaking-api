package com.pdrosoft.matchmaking.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GameDTO {
	private Integer id;

	private String name;

	private Instant creationDate;

	private PlayerDTO host;
	private PlayerDTO guest;

}
