package com.pdrosoft.matchmaking.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pdrosoft.matchmaking.dto.ErrorResultDTO;
import com.pdrosoft.matchmaking.dto.GameDTO;
import com.pdrosoft.matchmaking.dto.GameExtendedDTO;
import com.pdrosoft.matchmaking.dto.GameInputDTO;
import com.pdrosoft.matchmaking.dto.LoginResultDTO;
import com.pdrosoft.matchmaking.dto.UserAuthDTO;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@AutoConfigureMockMvc
public class GameControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper mapper = null;

	private ObjectMapper getObjectMapper() {
		if (mapper == null) {
			mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
			// support Java 8 date time apis
			mapper.registerModule(new JavaTimeModule());
		}

		return mapper;
	}

	private String getToken(String user, String password) throws Exception {
		var authData = UserAuthDTO.builder().username(user).password(password).build();

		var json = getObjectMapper().writeValueAsString(authData);
		var result = mockMvc.perform(post("/api/auth/login")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(json))//
				.andExpect(status().isOk()).andReturn();

		var authDTO = getObjectMapper().readValue(result.getResponse().getContentAsString(), LoginResultDTO.class);

		return authDTO.getToken();
	}

	@Test
	void testGameListSuccess() throws Exception {
		var token = getToken("testuser1", "password1");

		var result = mockMvc.perform(get("/api/game").header("Authorization", "Bearer %s".formatted(token)))//
				.andExpect(status().isOk()).andReturn();

		List<GameDTO> gameList = getObjectMapper().readValue(result.getResponse().getContentAsString(),
				new TypeReference<List<GameDTO>>() {
				});
		assertThat(gameList).hasSize(5);
		assertThat(gameList.get(0).getId()).isEqualTo(1);
		assertThat(gameList.get(1).getId()).isEqualTo(2);
		assertThat(gameList.get(2).getId()).isEqualTo(3);
		assertThat(gameList.get(3).getId()).isEqualTo(4);
		assertThat(gameList.get(4).getId()).isEqualTo(5);
	}

	@Test
	void testGameListWithNoToken() throws Exception {
		mockMvc.perform(get("/api/game")).andExpect(status().isForbidden());
	}

	@Test
	void testGameListWithInvalidToken() throws Exception {
		var token = "invalid.token";

		mockMvc.perform(get("/api/game").header("Authorization", "Bearer %s".formatted(token)))//
				.andExpect(status().isForbidden());
	}

	@Test
	void testCreateGameWithInvalidToken() throws Exception {
		var token = "invalid.token";

		mockMvc.perform(put("/api/game").header("Authorization", "Bearer %s".formatted(token)))//
				.andExpect(status().isForbidden());
	}

	@Test
	void testCreateJoinLeaveGuestFirstGameSuccess() throws Exception {
		var tokenHost = getToken("testuser1", "password1");
		var tokenGuest = getToken("testuser2", "password2");

		var gameInputDto = GameInputDTO.builder().joinCode("test-code").build();
		var json = getObjectMapper().writeValueAsString(gameInputDto);
		var result = mockMvc.perform(put("/api/game") //
				.header("Authorization", "Bearer %s".formatted(tokenHost)) //
				.contentType(MediaType.APPLICATION_JSON)//
				.content(json))//
				.andExpect(status().isOk()).andReturn();

		var game = getObjectMapper().readValue(result.getResponse().getContentAsString(), GameDTO.class);
		var newGameId = game.getId();
		assertThat(game.getCreationDate()).isBetween(Instant.now().minus(Duration.ofSeconds(2)),
				Instant.now().plus(Duration.ofSeconds(2)));
		assertThat(game.getName()).isEqualTo("testuser1's game");
		assertThat(game.getHost().getUsername()).isEqualTo("testuser1");
		assertThat(game.getGuest()).isNull();

		var resultJoin = mockMvc.perform(post("/api/game/{gameId}/join", Integer.toString(newGameId)) //
				.header("Authorization", "Bearer %s".formatted(tokenGuest)))//
				.andExpect(status().isOk()).andReturn();

		var gameJoined = getObjectMapper().readValue(resultJoin.getResponse().getContentAsString(),
				GameExtendedDTO.class);
		assertThat(gameJoined.getId()).isEqualTo(game.getId());
		assertThat(gameJoined.getCreationDate()).isNotNull();
		assertThat(gameJoined.getJoinCode()).isEqualTo(gameInputDto.getJoinCode());
		assertThat(gameJoined.getName()).isEqualTo(game.getName());
		assertThat(gameJoined.getHost().getUsername()).isEqualTo("testuser1");
		assertThat(gameJoined.getGuest().getUsername()).isEqualTo("testuser2");

		var resultLeaveGuest = mockMvc.perform(post("/api/game/{gameId}/leave", Integer.toString(newGameId)) //
				.header("Authorization", "Bearer %s".formatted(tokenGuest)))//
				.andExpect(status().isOk()).andReturn();

		var gameLeft = getObjectMapper().readValue(resultLeaveGuest.getResponse().getContentAsString(), GameDTO.class);
		assertThat(gameLeft.getId()).isEqualTo(newGameId);
		assertThat(gameLeft.getCreationDate()).isNotNull();
		assertThat(gameLeft.getName()).isEqualTo("testuser1's game");
		assertThat(gameLeft.getHost().getUsername()).isEqualTo("testuser1");
		assertThat(gameLeft.getGuest()).isNull();

		var resultLeaveHost = mockMvc.perform(post("/api/game/{gameId}/leave", Integer.toString(newGameId)) //
				.header("Authorization", "Bearer %s".formatted(tokenHost)))//
				.andExpect(status().isOk()).andReturn();

		assertThat(resultLeaveHost.getResponse().getContentAsString()).isNullOrEmpty();

	}

	@Test
	void testCreateJoinLeaveHostFirstGameSuccess() throws Exception {
		final var testCode = "test-code";

		var tokenHost = getToken("testuser1", "password1");
		var tokenGuest = getToken("testuser2", "password2");

		var gameInputDto = GameInputDTO.builder().joinCode(testCode).build();
		var json = getObjectMapper().writeValueAsString(gameInputDto);
		var result = mockMvc.perform(put("/api/game") //
				.header("Authorization", "Bearer %s".formatted(tokenHost)) //
				.contentType(MediaType.APPLICATION_JSON)//
				.content(json))//
				.andExpect(status().isOk()).andReturn();

		var game = getObjectMapper().readValue(result.getResponse().getContentAsString(), GameDTO.class);
		var newGameId = game.getId();
		assertThat(game.getCreationDate()).isBetween(Instant.now().minus(Duration.ofSeconds(2)),
				Instant.now().plus(Duration.ofSeconds(2)));
		assertThat(game.getName()).isEqualTo("testuser1's game");
		assertThat(game.getHost().getUsername()).isEqualTo("testuser1");
		assertThat(game.getGuest()).isNull();

		var resultJoin = mockMvc.perform(post("/api/game/{gameId}/join", Integer.toString(newGameId)) //
				.header("Authorization", "Bearer %s".formatted(tokenGuest)))//
				.andExpect(status().isOk()).andReturn();

		var gameJoined = getObjectMapper().readValue(resultJoin.getResponse().getContentAsString(),
				GameExtendedDTO.class);
		assertThat(gameJoined.getId()).isEqualTo(game.getId());
		assertThat(gameJoined.getCreationDate()).isNotNull();
		assertThat(gameJoined.getName()).isEqualTo(game.getName());
		assertThat(gameJoined.getJoinCode()).isEqualTo(gameInputDto.getJoinCode());
		assertThat(gameJoined.getHost().getUsername()).isEqualTo("testuser1");
		assertThat(gameJoined.getGuest().getUsername()).isEqualTo("testuser2");

		var resultLeaveHost = mockMvc.perform(post("/api/game/{gameId}/leave", Integer.toString(newGameId)) //
				.header("Authorization", "Bearer %s".formatted(tokenHost)))//
				.andExpect(status().isOk()).andReturn();

		assertThat(resultLeaveHost.getResponse().getContentAsString()).isNullOrEmpty();

		var resultLeaveGuest = mockMvc.perform(post("/api/game/{gameId}/leave", Integer.toString(newGameId)) //
				.header("Authorization", "Bearer %s".formatted(tokenGuest)))//
				.andExpect(status().isOk()).andReturn();

		assertThat(resultLeaveGuest.getResponse().getContentAsString()).isNullOrEmpty();
	}

	@Test
	void testCreateWithEmptyJoinCode() throws Exception {
		final var testCode = "";

		var tokenHost = getToken("testuser1", "password1");

		var gameInputDto = GameInputDTO.builder().joinCode(testCode).build();
		var json = getObjectMapper().writeValueAsString(gameInputDto);
		var result = mockMvc.perform(put("/api/game") //
				.header("Authorization", "Bearer %s".formatted(tokenHost)) //
				.contentType(MediaType.APPLICATION_JSON)//
				.content(json))//
				.andExpect(status().isBadRequest()).andReturn();

		var resultDto = getObjectMapper().readValue(result.getResponse().getContentAsString(), ErrorResultDTO.class);
		assertThat(resultDto.getMessage()).contains("joinCode").contains("must not be blank");
	}

	@Test
	void testLeaveInexistentGame() throws Exception {
		var token = getToken("testuser1", "password1");
		var inexistentGameId = 1200;

		var resultLeaveGuest = mockMvc.perform(post("/api/game/{gameId}/leave", Integer.toString(inexistentGameId)) //
				.header("Authorization", "Bearer %s".formatted(token)))//
				.andExpect(status().isOk()).andReturn();

		assertThat(resultLeaveGuest.getResponse().getContentAsString()).isNullOrEmpty();
	}

	@Test
	void testJoinInexistentGame() throws Exception {
		var token = getToken("testuser1", "password1");
		var inexistentGameId = 1200;

		mockMvc.perform(post("/api/game/{gameId}/join", Integer.toString(inexistentGameId)) //
				.header("Authorization", "Bearer %s".formatted(token)))//
				.andExpect(status().isNotFound());
	}

}
