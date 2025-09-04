package com.flashcards.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flashcards.application.dto.UserCreationDto;
import com.flashcards.domain.exceptions.UnprocessableEntityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MessageSource messageSource;

    @Test
    void testCreateUser_ValidUser() throws Exception {
        var user = new UserCreationDto("Puszmen12", "puszmen12@gmail.com", "Srterydfgxc7657*hgf");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user))
            )
            .andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    void testCreateUser_InvalidUserStatus500() throws Exception {
        var user = new UserCreationDto(null, "puszmen12@gmail.com", "Srterydfgxc7657*hgf");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user))
            )
            .andExpect(MockMvcResultMatchers.status().is(500));
    }

    @Test
    void testCreateUser_InvalidUserStatus422() throws Exception {
        var user = new UserCreationDto("Puszmen12", "puszmen12@gmail.com", "qwerty");
        String expectedMessage = messageSource.getMessage("weak.password", null, LocaleContextHolder.getLocale());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user))
            )
            .andExpect(MockMvcResultMatchers.status().is(422))
            .andExpect(result ->
                Assertions.assertInstanceOf(UnprocessableEntityException.class, result.getResolvedException()))
            .andExpect(result ->
                Assertions.assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));
    }
}
