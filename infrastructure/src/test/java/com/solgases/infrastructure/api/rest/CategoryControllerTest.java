package com.solgases.infrastructure.api.rest;

import static org.assertj.core.api.Assertions.assertThat;
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

import com.solgases.application.dto.CategoryResult;
import com.solgases.application.dto.CategoryCommand;
import com.solgases.application.exception.CategoryNotFoundException;
import com.solgases.application.exception.DuplicateCategoryNameException;
import com.solgases.application.port.in.ActivateCategoryUseCase;
import com.solgases.application.port.in.CreateCategoryUseCase;
import com.solgases.application.port.in.DeactivateCategoryUseCase;
import com.solgases.application.port.in.GetCategoryByIdUseCase;
import com.solgases.application.port.in.ListCategoriesUseCase;
import com.solgases.application.port.in.UpdateCategoryUseCase;
import com.solgases.infrastructure.api.dto.CategoryRequest;
import com.solgases.infrastructure.api.dto.CategoryResponse;
import com.solgases.infrastructure.api.rest.error.GlobalExceptionHandler;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private static final MediaType PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON;

    @Mock
    private CreateCategoryUseCase createCategoryUseCase;

    @Mock
    private ListCategoriesUseCase listCategoriesUseCase;

    @Mock
    private GetCategoryByIdUseCase getCategoryByIdUseCase;

    @Mock
    private UpdateCategoryUseCase updateCategoryUseCase;

    @Mock
    private ActivateCategoryUseCase activateCategoryUseCase;

    @Mock
    private DeactivateCategoryUseCase deactivateCategoryUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CategoryController(createCategoryUseCase, listCategoriesUseCase,
                        getCategoryByIdUseCase, updateCategoryUseCase, activateCategoryUseCase, deactivateCategoryUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // POST

    @Test
    void createReturns201WithLocationAndBody() throws Exception {
        when(createCategoryUseCase.execute(new CategoryCommand("EPP"))).thenReturn(new CategoryResult(5L, "EPP", true));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"EPP\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/categories/5"))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("EPP"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void createIgnoresIdAndActiveSentByTheClient() throws Exception {
        when(createCategoryUseCase.execute(any(CategoryCommand.class))).thenReturn(new CategoryResult(1L, "EPP", true));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":999,\"name\":\"EPP\",\"active\":false}"))
                .andExpect(status().isCreated());

        ArgumentCaptor<CategoryCommand> captor = ArgumentCaptor.forClass(CategoryCommand.class);
        verify(createCategoryUseCase).execute(captor.capture());
        // The request type only carries the name, so id and active cannot reach the service
        assertThat(captor.getValue()).isEqualTo(new CategoryCommand("EPP"));
    }

    @Test
    void createWithBlankNameReturns400ProblemDetailWithErrors() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").isNotEmpty());
    }

    @Test
    void createWithTooLongNameReturns400() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + "a".repeat(101) + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("name"));
    }

    @Test
    void createWithMalformedJsonReturns400ProblemDetail() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON));
    }

    @Test
    void createWithDuplicateNameReturns409ProblemDetail() throws Exception {
        when(createCategoryUseCase.execute(any(CategoryCommand.class)))
                .thenThrow(new DuplicateCategoryNameException("EPP"));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"EPP\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("A category with the name 'EPP' already exists"));
    }

    // GET

    @Test
    void findAllReturns200WithList() throws Exception {
        when(listCategoriesUseCase.execute()).thenReturn(List.of(
                new CategoryResult(1L, "EPP", true),
                new CategoryResult(2L, "Extintores", false)));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].active").value(false));
    }

    @Test
    void findByIdReturns200() throws Exception {
        when(getCategoryByIdUseCase.execute(1L)).thenReturn(new CategoryResult(1L, "EPP", true));

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("EPP"));
    }

    @Test
    void findByIdReturns404ProblemDetailWhenMissing() throws Exception {
        when(getCategoryByIdUseCase.execute(99L)).thenThrow(new CategoryNotFoundException(99L));

        mockMvc.perform(get("/api/categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void findByIdWithNonNumericIdReturns400ProblemDetail() throws Exception {
        mockMvc.perform(get("/api/categories/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON));
    }

    // PUT

    @Test
    void updateReturns200() throws Exception {
        when(updateCategoryUseCase.execute(eq(1L), eq(new CategoryCommand("Gases"))))
                .thenReturn(new CategoryResult(1L, "Gases", true));

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Gases\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gases"));
    }

    @Test
    void updateCannotModifyActiveThroughTheRequestBody() throws Exception {
        when(updateCategoryUseCase.execute(eq(1L), any(CategoryCommand.class)))
                .thenReturn(new CategoryResult(1L, "Gases", true));

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Gases\",\"active\":false}"))
                .andExpect(status().isOk());

        // Only the name reaches the service; the active flag sent by the client is dropped
        verify(updateCategoryUseCase).execute(1L, new CategoryCommand("Gases"));
    }

    @Test
    void updateWithInvalidBodyReturns400() throws Exception {
        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("name"));
    }

    @Test
    void updateReturns404WhenMissing() throws Exception {
        when(updateCategoryUseCase.execute(eq(99L), any(CategoryCommand.class)))
                .thenThrow(new CategoryNotFoundException(99L));

        mockMvc.perform(put("/api/categories/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Gases\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReturns409WhenNameBelongsToAnotherCategory() throws Exception {
        when(updateCategoryUseCase.execute(eq(1L), any(CategoryCommand.class)))
                .thenThrow(new DuplicateCategoryNameException("Gases"));

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Gases\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(PROBLEM_JSON));
    }

    // PATCH

    @Test
    void activateReturns200WithoutBody() throws Exception {
        when(activateCategoryUseCase.execute(1L)).thenReturn(new CategoryResult(1L, "EPP", true));

        mockMvc.perform(patch("/api/categories/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void deactivateReturns200WithoutBody() throws Exception {
        when(deactivateCategoryUseCase.execute(1L)).thenReturn(new CategoryResult(1L, "EPP", false));

        mockMvc.perform(patch("/api/categories/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void activateAndDeactivateReturn404WhenMissing() throws Exception {
        when(activateCategoryUseCase.execute(99L)).thenThrow(new CategoryNotFoundException(99L));
        when(deactivateCategoryUseCase.execute(99L)).thenThrow(new CategoryNotFoundException(99L));

        mockMvc.perform(patch("/api/categories/99/activate")).andExpect(status().isNotFound());
        mockMvc.perform(patch("/api/categories/99/deactivate")).andExpect(status().isNotFound());
    }

    @Test
    void unsupportedMethodReturns405ProblemDetail() throws Exception {
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(PROBLEM_JSON));
    }
}
