package com.pdrosoft.matchmaking.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pdrosoft.matchmaking.dto.LoginResultDTO;
import com.pdrosoft.matchmaking.dto.PlayerDTO;
import com.pdrosoft.matchmaking.dto.UserAuthDTO;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureMockMvc
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private ObjectWriter getObjectWriter() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
		//mapper.configure(SerializationFeature., false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		return ow;
	}

	private ObjectReader getObjectReader() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
		//mapper.configure(SerializationFeature., false);
		ObjectReader or = mapper.reader();
		return or;
	}

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
		/*
		var playerDTO = (PlayerDTO) result.getAsyncResult();
		assertThat(playerDTO.getId()).isEqualTo(1);
		assertThat(playerDTO.getUsername()).isEqualTo("testuser1");
		*/
	}
}
