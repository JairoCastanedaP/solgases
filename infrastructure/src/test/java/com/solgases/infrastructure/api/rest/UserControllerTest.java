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
import com.solgases.application.dto.RoleResult;
import com.solgases.application.dto.UserCommand;
import com.solgases.application.dto.UserResult;
import com.solgases.application.exception.DuplicateUsernameException;
import com.solgases.application.exception.RoleNotFoundException;
import com.solgases.application.exception.UserNotFoundException;
import com.solgases.application.port.in.ActivateUserUseCase;
import com.solgases.application.port.in.CreateUserUseCase;
import com.solgases.application.port.in.DeactivateUserUseCase;
import com.solgases.application.port.in.GetUserByIdUseCase;
import com.solgases.application.port.in.ListUsersUseCase;
import com.solgases.application.port.in.UpdateUserUseCase;
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
class UserControllerTest {

    private static final MediaType PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON;
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final RoleResult ROLE = new RoleResult(1L, "R_KEY", "Role",
            List.of(new PermissionResult(10L, "P_KEY", "p.code", NOW, NOW)), NOW, NOW);

    @Mock
    private CreateUserUseCase createUserUseCase;
    @Mock
    private ListUsersUseCase listUsersUseCase;
    @Mock
    private GetUserByIdUseCase getUserByIdUseCase;
    @Mock
    private UpdateUserUseCase updateUserUseCase;
    @Mock
    private ActivateUserUseCase activateUserUseCase;
    @Mock
    private DeactivateUserUseCase deactivateUserUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(createUserUseCase, listUsersUseCase,
                        getUserByIdUseCase, updateUserUseCase, activateUserUseCase, deactivateUserUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private static UserResult user(boolean active) {
        return new UserResult(5L, "jdoe", "John Doe", active, List.of(ROLE), NOW, NOW);
    }

    @Test
    void createReturns201WithLocationAndNestedRolesAndPermissions() throws Exception {
        when(createUserUseCase.execute(new UserCommand("jdoe", "John Doe", Set.of(1L)))).thenReturn(user(true));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"jdoe\",\"displayName\":\"John Doe\",\"roleIds\":[1]}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/users/5"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.roles[0].key").value("R_KEY"))
                .andExpect(jsonPath("$.roles[0].permissions[0].code").value("p.code"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void createIgnoresIdActiveAndPasswordSentByTheClient() throws Exception {
        when(createUserUseCase.execute(any(UserCommand.class))).thenReturn(user(true));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":9,\"active\":false,\"password\":\"x\",\"username\":\"jdoe\","
                                + "\"displayName\":\"John Doe\",\"roleIds\":[]}"))
                .andExpect(status().isCreated());

        verify(createUserUseCase).execute(new UserCommand("jdoe", "John Doe", Set.of()));
    }

    @Test
    void createWithInvalidBodyReturns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\" \",\"displayName\":\"John Doe\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.errors.length()").value(2));
    }

    @Test
    void createWithDuplicateUsernameReturns409AndUnknownRoleReturns404() throws Exception {
        String body = "{\"username\":\"jdoe\",\"displayName\":\"John Doe\",\"roleIds\":[7]}";
        when(createUserUseCase.execute(any(UserCommand.class)))
                .thenThrow(new DuplicateUsernameException("jdoe"))
                .thenThrow(new RoleNotFoundException(List.of(7L)));

        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("A user with the username 'jdoe' already exists"));
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Roles not found with ids [7]"));
    }

    @Test
    void findAllAndFindByIdReturn200() throws Exception {
        when(listUsersUseCase.execute()).thenReturn(List.of(user(true), user(false)));
        when(getUserByIdUseCase.execute(5L)).thenReturn(user(true));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].active").value(false));
        mockMvc.perform(get("/api/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jdoe"));
    }

    @Test
    void findByIdReturns404WhenMissingAnd400ForNonNumericId() throws Exception {
        when(getUserByIdUseCase.execute(99L)).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(PROBLEM_JSON));
        mockMvc.perform(get("/api/users/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON));
    }

    @Test
    void updateCannotModifyActiveThroughTheRequestBody() throws Exception {
        when(updateUserUseCase.execute(eq(5L), any(UserCommand.class))).thenReturn(user(true));

        mockMvc.perform(put("/api/users/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"jdoe\",\"displayName\":\"John Doe\",\"roleIds\":[1],\"active\":false}"))
                .andExpect(status().isOk());

        verify(updateUserUseCase).execute(5L, new UserCommand("jdoe", "John Doe", Set.of(1L)));
    }

    @Test
    void updateReturns404And409() throws Exception {
        String body = "{\"username\":\"jdoe\",\"displayName\":\"John Doe\",\"roleIds\":[]}";
        when(updateUserUseCase.execute(eq(99L), any(UserCommand.class))).thenThrow(new UserNotFoundException(99L));
        when(updateUserUseCase.execute(eq(5L), any(UserCommand.class))).thenThrow(new DuplicateUsernameException("jdoe"));

        mockMvc.perform(put("/api/users/99").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/api/users/5").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void activateAndDeactivateReturn200Or404() throws Exception {
        when(activateUserUseCase.execute(5L)).thenReturn(user(true));
        when(deactivateUserUseCase.execute(5L)).thenReturn(user(false));
        when(activateUserUseCase.execute(99L)).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(patch("/api/users/5/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
        mockMvc.perform(patch("/api/users/5/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
        mockMvc.perform(patch("/api/users/99/activate"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteIsNotSupported() throws Exception {
        mockMvc.perform(delete("/api/users/5"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(PROBLEM_JSON));
    }
}
