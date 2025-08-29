package com.flashcards.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flashcards.domain.exceptions.UnprocessableEntityException;
import com.flashcards.domain.model.User;
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
        var user = new User();
        user.setUsername("Puszmen12");
        user.setEmail("puszmen12@gmail.com");
        user.setPasswordHash("Srterydfgxc7657*hgf");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user))
            )
            .andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    void testCreateUser_NullUsername() throws Exception {
        var user = new User();
        user.setEmail("puszmen12@gmail.com");
        user.setPasswordHash("Srterydfgxc7657*hgf");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user))
            )
            .andExpect(MockMvcResultMatchers.status().is(500));
    }

    @Test
    void testCreateUser_NullEmail() throws Exception {
        var user = new User();
        user.setUsername("Puszmen12");
        user.setPasswordHash("Srterydfgxc7657*hgf");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user))
            )
            .andExpect(MockMvcResultMatchers.status().is(500));
    }

    @Test
    void testCreateUser_NullPassword() throws Exception {
        var user = new User();
        user.setUsername("Puszmen12");
        user.setEmail("puszmen12@gmail.com");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user))
            )
            .andExpect(MockMvcResultMatchers.status().is(500));
    }

    @Test
    void testCreateUser_UsernameExists() throws Exception {
        var user = new User();
        user.setUsername("Puszmen12");
        user.setEmail("puszmen12@gmail.com");
        user.setPasswordHash("Srterydfgxc7657*hgf");
        var conflictUser = new User();
        conflictUser.setUsername("Puszmen12");
        conflictUser.setEmail("puszmen1234@gmail.com");
        conflictUser.setPasswordHash("Srterydfgxc7657*hgf");
        String expectedMessage = messageSource.getMessage("user.already.exists", null, LocaleContextHolder.getLocale());
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
        );

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(conflictUser))
            )
            .andExpect(MockMvcResultMatchers.status().is(422))
            .andExpect(result ->
                Assertions.assertInstanceOf(UnprocessableEntityException.class, result.getResolvedException()))
            .andExpect(result ->
                Assertions.assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));
    }

    @Test
    void testCreateUser_EmailExists() throws Exception {
        var user = new User();
        user.setUsername("Puszmen12");
        user.setEmail("puszmen12@gmail.com");
        user.setPasswordHash("Srterydfgxc7657*hgf");
        var conflictUser = new User();
        conflictUser.setUsername("Puszmen13");
        conflictUser.setEmail("puszmen12@gmail.com");
        conflictUser.setPasswordHash("Srterydfgxc7657*hgf");
        String expectedMessage = messageSource.getMessage("email.already.taken", null, LocaleContextHolder.getLocale());

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
        );

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(conflictUser))
            )
            .andExpect(MockMvcResultMatchers.status().is(422))
            .andExpect(result ->
                Assertions.assertInstanceOf(UnprocessableEntityException.class, result.getResolvedException()))
            .andExpect(result ->
                Assertions.assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));
    }

    @Test
    void testCreateUser_WeakPassword() throws Exception {
        var user = new User();
        user.setUsername("Puszmen12");
        user.setEmail("puszmen12@gmail.com");
        user.setPasswordHash("qwerty");
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

    @Test
    void testCreateUser_ShortUsername() throws Exception {
        var user = new User();
        user.setUsername("o");
        user.setEmail("puszmen12@gmail.com");
        user.setPasswordHash("Srterydfgxc7657*hgf");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user))
            )
            .andExpect(MockMvcResultMatchers.status().is(500));
    }

    @Test
    void testCreateUser_LongUsername() throws Exception {
        var user = new User();
        String username = "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq";
        user.setUsername(username);
        user.setEmail("puszmen12@gmail.com");
        user.setPasswordHash("Srterydfgxc7657*hgf");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user))
            )
            .andExpect(MockMvcResultMatchers.status().is(500));
    }

    @Test
    void testCreateUser_InvalidEmailFormat() throws Exception {
        var user = new User();
        user.setUsername("Puszmen12");
        user.setEmail("puszmen12");
        user.setPasswordHash("Srterydfgxc7657*hgf");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user))
            )
            .andExpect(MockMvcResultMatchers.status().is(500));
    }
}
