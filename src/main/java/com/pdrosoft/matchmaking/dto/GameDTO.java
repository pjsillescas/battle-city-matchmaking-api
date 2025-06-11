package com.pdrosoft.matchmaking.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameDTO {
	private Integer id;

	private String name;

	private Instant creationDate;

	private PlayerDTO host;
	private PlayerDTO guest;

}
