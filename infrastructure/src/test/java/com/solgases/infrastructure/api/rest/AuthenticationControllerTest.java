package com.solgases.infrastructure.api.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.solgases.application.dto.AuthenticatedUser;
import com.solgases.application.dto.AuthenticationCommand;
import com.solgases.application.exception.AuthenticationFailedException;
import com.solgases.application.port.in.AuthenticateUserUseCase;
import com.solgases.infrastructure.api.rest.error.GlobalExceptionHandler;
import com.solgases.infrastructure.security.AccessTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticateUserUseCase authenticateUserUseCase;
    @Mock
    private AccessTokenService accessTokenService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthenticationController(authenticateUserUseCase,
                        accessTokenService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void validCredentialsReturnTheIssuedToken() throws Exception {
        when(authenticateUserUseCase.execute(new AuthenticationCommand("jdoe", "pw-value")))
                .thenReturn(new AuthenticatedUser(5L));
        when(accessTokenService.issue(5L)).thenReturn(new AccessTokenService.IssuedAccessToken("issued-token", 900));

        mockMvc.perform(post("/api/auth/token").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"jdoe\",\"password\":\"pw-value\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("issued-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void failedAuthenticationReturnsAGeneric401ProblemDetail() throws Exception {
        when(authenticateUserUseCase.execute(any())).thenThrow(new AuthenticationFailedException());

        mockMvc.perform(post("/api/auth/token").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"jdoe\",\"password\":\"pw-value\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Invalid username or password"))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("pw-value"))));
    }

    @Test
    void blankOrOversizedFieldsAreRejectedBeforeAuthenticating() throws Exception {
        mockMvc.perform(post("/api/auth/token").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()").value(2));
        mockMvc.perform(post("/api/auth/token").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + "u".repeat(51) + "\",\"password\":\"pw-value\"}"))
                .andExpect(status().isBadRequest());
        verify(authenticateUserUseCase, never()).execute(any());
    }
}
