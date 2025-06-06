package com.pdrosoft.matchmaking.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PlayerDTO {
    private Integer id;

    private String username;
}
