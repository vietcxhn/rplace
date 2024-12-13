package com.projet.rplace.controller;

import com.projet.rplace.model.XUser;
import com.projet.rplace.security.JwtException;
import com.projet.rplace.security.JwtProvider;
import com.projet.rplace.services.JwtUserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    private RestTemplate restTemplate = new RestTemplate();

    @LocalServerPort
    private int port;

    @MockBean
    private JwtUserService userService;

    @MockBean
    private JwtProvider jwtProvider;

    @Test
    void testLoginSuccess() throws Exception {
        when(userService.login("user", "password")).thenReturn("mockToken");

        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("username", "user");
        request.add("password", "password");

        String url = "http://localhost:" + port + "/auth/login";

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("mockToken", response.getBody());
    }

    @Test
    void testLoginFailure() {
        when(userService.login("user", "wrongPassword")).thenThrow(new JwtException("Invalid credentials"));

        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("username", "user");
        request.add("password", "wrongPassword");

        String url = "http://localhost:" + port + "/auth/login";

        HttpClientErrorException.Unauthorized thrown = assertThrows(HttpClientErrorException.Unauthorized.class, () -> {
            restTemplate.postForEntity(url, request, String.class);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatusCode());
    }

    @Test
    void testSignupSuccess() {
        XUser newUser = new XUser(0L, "user", "password", Set.of("USER"));

        when(userService.signup(any(XUser.class))).thenReturn("mockToken");

        String url = "http://localhost:" + port + "/auth/signup";

        ResponseEntity<Map> response = restTemplate.postForEntity(url, newUser, Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("token"));
    }

    @Test
    void testSignupFailure() {
        XUser newUser = new XUser(0L, "user", "password", Set.of("USER"));

        when(userService.signup(any(XUser.class))).thenThrow(new JwtException("Username is already in use"));

        String url = "http://localhost:" + port + "/auth/signup";

        HttpClientErrorException.BadRequest thrown = assertThrows(HttpClientErrorException.BadRequest.class, () -> {
            restTemplate.postForEntity(url, newUser, String.class);
        });

        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
    }
}
