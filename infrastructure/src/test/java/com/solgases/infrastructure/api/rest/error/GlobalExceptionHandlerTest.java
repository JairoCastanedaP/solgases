package com.solgases.infrastructure.api.rest.error;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.solgases.application.port.in.CreatePermissionUseCase;
import com.solgases.application.port.in.GetPermissionByIdUseCase;
import com.solgases.application.port.in.ListPermissionsUseCase;
import com.solgases.application.port.in.UpdatePermissionUseCase;
import com.solgases.infrastructure.api.rest.PermissionController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/** Verifies the cross-cutting error translation using one controller as a vehicle. */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

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
    void unexpectedErrorReturns500ProblemDetailWithoutInternalDetails() throws Exception {
        when(listPermissionsUseCase.execute()).thenThrow(new IllegalStateException("internal detail jdbc:mysql://db"));

        mockMvc.perform(get("/api/permissions"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred"))
                .andExpect(content().string(not(containsString("internal detail"))));
    }

    @Test
    void standardSpringErrorsKeepTheirOwnStatus() throws Exception {
        mockMvc.perform(delete("/api/permissions/1"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
        mockMvc.perform(post("/api/permissions").contentType(MediaType.APPLICATION_JSON).content("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
        mockMvc.perform(get("/api/permissions/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
