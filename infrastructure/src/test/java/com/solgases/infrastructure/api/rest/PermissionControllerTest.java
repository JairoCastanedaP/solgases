package com.solgases.infrastructure.api.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.solgases.application.dto.PermissionCreateCommand;
import com.solgases.application.dto.PermissionResult;
import com.solgases.application.dto.PermissionUpdateCommand;
import com.solgases.application.exception.DuplicatePermissionCodeException;
import com.solgases.application.exception.DuplicatePermissionKeyException;
import com.solgases.application.exception.PermissionNotFoundException;
import com.solgases.application.port.in.CreatePermissionUseCase;
import com.solgases.application.port.in.GetPermissionByIdUseCase;
import com.solgases.application.port.in.ListPermissionsUseCase;
import com.solgases.application.port.in.UpdatePermissionUseCase;
import com.solgases.infrastructure.api.rest.error.GlobalExceptionHandler;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class PermissionControllerTest {

    private static final MediaType PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON;
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final PermissionResult PERMISSION = new PermissionResult(1L, "P_KEY", "p.code", NOW, NOW);

    @Mock
    private CreatePermissionUseCase createPermissionUseCase;
    @Mock
    private ListPermissionsUseCase listPermissionsUseCase;
    @Mock
    private GetPermissionByIdUseCase getPermissionByIdUseCase;
    @Mock
    private UpdatePermissionUseCase updatePermissionUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PermissionController(createPermissionUseCase,
                        listPermissionsUseCase, getPermissionByIdUseCase, updatePermissionUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createReturns201WithLocation() throws Exception {
        when(createPermissionUseCase.execute(new PermissionCreateCommand("P_KEY", "p.code"))).thenReturn(PERMISSION);

        mockMvc.perform(post("/api/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"P_KEY\",\"code\":\"p.code\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/permissions/1"))
                .andExpect(jsonPath("$.key").value("P_KEY"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void createWithInvalidBodyReturns400() throws Exception {
        mockMvc.perform(post("/api/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"P_KEY\",\"code\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.errors[0].field").value("code"));
    }

    @Test
    void createConflictsReturn409() throws Exception {
        String body = "{\"key\":\"P_KEY\",\"code\":\"p.code\"}";
        when(createPermissionUseCase.execute(any(PermissionCreateCommand.class)))
                .thenThrow(new DuplicatePermissionKeyException("P_KEY"))
                .thenThrow(new DuplicatePermissionCodeException("p.code"));

        mockMvc.perform(post("/api/permissions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("A permission with the key 'P_KEY' already exists"));
        mockMvc.perform(post("/api/permissions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(PROBLEM_JSON));
    }

    @Test
    void findAllAndFindById() throws Exception {
        when(listPermissionsUseCase.execute()).thenReturn(List.of(PERMISSION));
        when(getPermissionByIdUseCase.execute(1L)).thenReturn(PERMISSION);
        when(getPermissionByIdUseCase.execute(99L)).thenThrow(new PermissionNotFoundException(99L));

        mockMvc.perform(get("/api/permissions")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(get("/api/permissions/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("p.code"));
        mockMvc.perform(get("/api/permissions/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(PROBLEM_JSON));
    }

    @Test
    void updateCannotModifyTheKeyThroughTheRequestBody() throws Exception {
        when(updatePermissionUseCase.execute(eq(1L), any(PermissionUpdateCommand.class))).thenReturn(PERMISSION);

        mockMvc.perform(put("/api/permissions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"CHANGED\",\"code\":\"p.code\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("P_KEY"));

        verify(updatePermissionUseCase).execute(1L, new PermissionUpdateCommand("p.code"));
    }

    @Test
    void updateReturns404And409() throws Exception {
        String body = "{\"code\":\"p.code\"}";
        when(updatePermissionUseCase.execute(eq(99L), any(PermissionUpdateCommand.class)))
                .thenThrow(new PermissionNotFoundException(99L));
        when(updatePermissionUseCase.execute(eq(1L), any(PermissionUpdateCommand.class)))
                .thenThrow(new DuplicatePermissionCodeException("p.code"));

        mockMvc.perform(put("/api/permissions/99").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/api/permissions/1").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteIsNotSupported() throws Exception {
        mockMvc.perform(delete("/api/permissions/1")).andExpect(status().isMethodNotAllowed());
    }
}
