package com.solgases.infrastructure.api.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.solgases.application.dto.PermissionResult;
import com.solgases.application.dto.RoleCreateCommand;
import com.solgases.application.dto.RoleResult;
import com.solgases.application.dto.RoleUpdateCommand;
import com.solgases.application.exception.DuplicateRoleKeyException;
import com.solgases.application.exception.DuplicateRoleNameException;
import com.solgases.application.exception.PermissionNotFoundException;
import com.solgases.application.exception.RoleNotFoundException;
import com.solgases.application.port.in.CreateRoleUseCase;
import com.solgases.application.port.in.GetRoleByIdUseCase;
import com.solgases.application.port.in.ListRolesUseCase;
import com.solgases.application.port.in.UpdateRoleUseCase;
import com.solgases.infrastructure.api.rest.error.GlobalExceptionHandler;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    private static final MediaType PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON;
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final RoleResult ROLE = new RoleResult(3L, "R_KEY", "Role",
            List.of(new PermissionResult(1L, "P_KEY", "p.code", NOW, NOW)), NOW, NOW);

    @Mock
    private CreateRoleUseCase createRoleUseCase;
    @Mock
    private ListRolesUseCase listRolesUseCase;
    @Mock
    private GetRoleByIdUseCase getRoleByIdUseCase;
    @Mock
    private UpdateRoleUseCase updateRoleUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new RoleController(createRoleUseCase, listRolesUseCase,
                        getRoleByIdUseCase, updateRoleUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createReturns201WithLocationAndPermissions() throws Exception {
        when(createRoleUseCase.execute(new RoleCreateCommand("R_KEY", "Role", Set.of(1L)))).thenReturn(ROLE);

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"R_KEY\",\"name\":\"Role\",\"permissionIds\":[1]}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/roles/3"))
                .andExpect(jsonPath("$.key").value("R_KEY"))
                .andExpect(jsonPath("$.permissions[0].key").value("P_KEY"));
    }

    @Test
    void createWithInvalidBodyReturns400() throws Exception {
        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Role\",\"permissionIds\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.errors[0].field").value("key"));
    }

    @Test
    void createConflictsAndUnknownPermissionsAreTranslated() throws Exception {
        String body = "{\"key\":\"R_KEY\",\"name\":\"Role\",\"permissionIds\":[9]}";
        when(createRoleUseCase.execute(any(RoleCreateCommand.class)))
                .thenThrow(new DuplicateRoleKeyException("R_KEY"))
                .thenThrow(new DuplicateRoleNameException("Role"))
                .thenThrow(new PermissionNotFoundException(List.of(9L)));

        mockMvc.perform(post("/api/roles").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("A role with the key 'R_KEY' already exists"));
        mockMvc.perform(post("/api/roles").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("A role with the name 'Role' already exists"));
        mockMvc.perform(post("/api/roles").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(PROBLEM_JSON));
    }

    @Test
    void findAllAndFindById() throws Exception {
        when(listRolesUseCase.execute()).thenReturn(List.of(ROLE));
        when(getRoleByIdUseCase.execute(3L)).thenReturn(ROLE);
        when(getRoleByIdUseCase.execute(99L)).thenThrow(new RoleNotFoundException(99L));

        mockMvc.perform(get("/api/roles")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(get("/api/roles/3")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Role"));
        mockMvc.perform(get("/api/roles/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(PROBLEM_JSON));
    }

    @Test
    void updateCannotModifyTheKeyThroughTheRequestBody() throws Exception {
        when(updateRoleUseCase.execute(eq(3L), any(RoleUpdateCommand.class))).thenReturn(ROLE);

        mockMvc.perform(put("/api/roles/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"CHANGED\",\"name\":\"Role\",\"permissionIds\":[]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("R_KEY"));

        verify(updateRoleUseCase).execute(3L, new RoleUpdateCommand("Role", Set.of()));
    }

    @Test
    void updateReturns404And409() throws Exception {
        String body = "{\"name\":\"Role\",\"permissionIds\":[]}";
        when(updateRoleUseCase.execute(eq(99L), any(RoleUpdateCommand.class))).thenThrow(new RoleNotFoundException(99L));
        when(updateRoleUseCase.execute(eq(3L), any(RoleUpdateCommand.class)))
                .thenThrow(new DuplicateRoleNameException("Role"));

        mockMvc.perform(put("/api/roles/99").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/api/roles/3").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteAndActivationAreNotSupported() throws Exception {
        mockMvc.perform(delete("/api/roles/3")).andExpect(status().isMethodNotAllowed());
        mockMvc.perform(patch("/api/roles/3/activate")).andExpect(status().isNotFound());
    }
}
