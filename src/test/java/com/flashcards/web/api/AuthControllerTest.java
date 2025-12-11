package com.flashcards.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flashcards.application.dto.LoginUserDto;
import com.flashcards.application.dto.UserCreationDto;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user))
            )
            .andExpect(status().is(200));
    }

    @Test
    void testCreateUser_InvalidUserStatus500() throws Exception {
        var user = new UserCreationDto(null, "puszmen12@gmail.com", "Srterydfgxc7657*hgf");

        mockMvc.perform(
                post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user))
            )
            .andExpect(status().is(400));
    }

    @Test
    void testCreateUser_InvalidUserStatus422() throws Exception {
        var user = new UserCreationDto("Puszmen12", "puszmen12@gmail.com", "qwerty");
        String expectedMessage = messageSource.getMessage("weak.password", null, LocaleContextHolder.getLocale());

        mockMvc.perform(
                post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user))
            )
            .andExpect(status().is(400));
    }

    @Test
    void testLoginUser_ValidUser() throws Exception {
        var user = new UserCreationDto("Puszmen12", "puszmen12@gmail.com", "Srterydfgxc7657*hgf");
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
        );
        var loginRequest = new LoginUserDto("Puszmen12", "Srterydfgxc7657*hgf");
        mockMvc.perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest))
            )
            .andExpect(status().is(200));
    }

    @Test
    void testLoginUser_BadCredentials() throws Exception {
        var newUser = new UserCreationDto("Puszmen13", "puszmen13@gmail.com", "Srterydfgxc7657*hgf");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
            .andExpect(status().isOk()); // lub .isOk()

        // Act: błędny identyfikator (literówka)
        var badLogin = new LoginUserDto("Pumen13", "Srterydfgxc7657*hgf");

        MvcResult mvcResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badLogin)))
            .andExpect(request().asyncStarted())
            .andReturn();

        // Assert: 401 + JSON z error
        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isForbidden());
    }
}
