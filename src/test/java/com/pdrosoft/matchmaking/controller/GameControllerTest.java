package com.pdrosoft.matchmaking.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pdrosoft.matchmaking.dto.GameDTO;
import com.pdrosoft.matchmaking.dto.LoginResultDTO;
import com.pdrosoft.matchmaking.dto.UserAuthDTO;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
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

		List<GameDTO> gameList = getObjectMapper().readValue(result.getResponse().getContentAsString(), new TypeReference<List<GameDTO>>() {});
		assertThat(gameList).hasSize(5);
		assertThat(gameList.get(0).getId()).isEqualTo(1);
		assertThat(gameList.get(1).getId()).isEqualTo(2);
		assertThat(gameList.get(2).getId()).isEqualTo(3);
		assertThat(gameList.get(3).getId()).isEqualTo(4);
		assertThat(gameList.get(4).getId()).isEqualTo(5);
	}

	@Test
	void testGameListWithNoToken() throws Exception {
		var token = "";
		
		mockMvc.perform(get("/api/game"))
				.andExpect(status().isForbidden());
	}

	@Test
	void testGameListWithNoToken() throws Exception {
		var token = "";
		
		mockMvc.perform(get("/api/game"))//.header("Authorization", "Bearer %s".formatted(token)))//
				.andExpect(status().isForbidden());
	}

	
	/*
	@Test
	void testLoginSuccess() throws Exception {
		var authData = UserAuthDTO.builder().username("testuser1").password("password1").build();

		var json = getObjectWriter().writeValueAsString(authData);
		var result = mockMvc.perform(post("/api/auth/login")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(json))//
				.andExpect(status().isOk()).andReturn();

		var authDTO = getObjectReader().readValue(result.getResponse().getContentAsString(), LoginResultDTO.class);
		assertThat(authDTO).isNotNull().extracting(LoginResultDTO::getToken).isNotNull();
	}

	@ParameterizedTest
	@CsvSource(value = { "testuser4,password1", "testuser1,password4" })
	void testLoginPlayerNotFound(String user, String password) throws Exception {
		var authData = UserAuthDTO.builder().username(user).password(password).build();

		var json = getObjectWriter().writeValueAsString(authData);
		var result = mockMvc.perform(post("/api/auth/login")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(json))//
				.andExpect(status().isForbidden()).andReturn();

		var errorDTO = getObjectReader().readValue(result.getResponse().getContentAsString(), ErrorResultDTO.class);
		assertThat(errorDTO).isNotNull().extracting(ErrorResultDTO::getMessage).isEqualTo("Bad credentials");
	}

	@ParameterizedTest
	@CsvSource(value = { "user10,,password cannot be empty", ",pass,username cannot be empty" })
	void testLoginWithMissingData(String username, String password, String message) throws Exception {
		var authData = UserAuthDTO.builder().username(username).password(password).build();
		var json = getObjectWriter().writeValueAsString(authData);
		var result = mockMvc.perform(post("/api/auth/login")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(json))//
				.andExpect(status().isBadRequest()).andReturn();

		var errorDTO = getObjectReader().readValue(result.getResponse().getContentAsString(), ErrorResultDTO.class);
		assertThat(errorDTO).isNotNull();
		assertThat(errorDTO.getMessage()).isEqualTo(message);

	}

	@Test
	void testSignupSuccess() throws Exception {
		var authData = UserAuthDTO.builder().username("user5").password("pass5").build();

		var json = getObjectWriter().writeValueAsString(authData);
		
		var resultLogin1 = mockMvc.perform(post("/api/auth/login")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(json))//
				.andExpect(status().isForbidden()).andReturn();
		var errorDTO = getObjectReader().readValue(resultLogin1.getResponse().getContentAsString(), ErrorResultDTO.class);
		assertThat(errorDTO).isNotNull();
		assertThat(errorDTO.getMessage()).isEqualTo("Bad credentials");

		
		var result = mockMvc.perform(put("/api/auth/signup")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(json))//
				.andExpect(status().isOk()).andReturn();

		var playerDTO = getObjectReader().readValue(result.getResponse().getContentAsString(), PlayerDTO.class);
		assertThat(playerDTO).isNotNull();
		assertThat(playerDTO.getId()).isEqualTo(4);
		assertThat(playerDTO.getUsername()).isEqualTo("user5");

		var resultLogin = mockMvc.perform(post("/api/auth/login")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(json))//
				.andExpect(status().isOk()).andReturn();
		var authDTO = getObjectReader().readValue(resultLogin.getResponse().getContentAsString(), LoginResultDTO.class);
		assertThat(authDTO).isNotNull().extracting(LoginResultDTO::getToken).isNotNull();
		
	}

	@Test
	void testSignupExistingUser() throws Exception {
		var authData = UserAuthDTO.builder().username("testuser1").password("pass6").build();

		var json = getObjectWriter().writeValueAsString(authData);
		var result = mockMvc.perform(put("/api/auth/signup")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(json))//
				.andExpect(status().isForbidden()).andReturn();

		var errorDTO = getObjectReader().readValue(result.getResponse().getContentAsString(), ErrorResultDTO.class);
		assertThat(errorDTO).isNotNull();
		assertThat(errorDTO.getMessage()).isEqualTo("player already exists 'testuser1'");

	}

	@ParameterizedTest
	@CsvSource(value = { "user10,,password cannot be empty", ",pass,username cannot be empty" })
	void testSignupWithMissingData(String username, String password, String message) throws Exception {
		var authData = UserAuthDTO.builder().username(username).password(password).build();
		var json = getObjectWriter().writeValueAsString(authData);
		var result = mockMvc.perform(put("/api/auth/signup")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(json))//
				.andExpect(status().isBadRequest()).andReturn();

		var errorDTO = getObjectReader().readValue(result.getResponse().getContentAsString(), ErrorResultDTO.class);
		assertThat(errorDTO).isNotNull();
		assertThat(errorDTO.getMessage()).isEqualTo(message);

	}
	*/

}
