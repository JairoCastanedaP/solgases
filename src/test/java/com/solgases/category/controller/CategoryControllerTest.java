package com.solgases.category.controller;

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

import com.solgases.category.dto.CategoryRequest;
import com.solgases.category.dto.CategoryResponse;
import com.solgases.category.service.CategoryService;
import com.solgases.exception.GlobalExceptionHandler;
import com.solgases.exception.ResourceConflictException;
import com.solgases.exception.ResourceNotFoundException;
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
    private CategoryService categoryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CategoryController(categoryService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // POST

    @Test
    void createReturns201WithLocationAndBody() throws Exception {
        when(categoryService.create(new CategoryRequest("EPP"))).thenReturn(new CategoryResponse(5L, "EPP", true));

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
        when(categoryService.create(any(CategoryRequest.class))).thenReturn(new CategoryResponse(1L, "EPP", true));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":999,\"name\":\"EPP\",\"active\":false}"))
                .andExpect(status().isCreated());

        ArgumentCaptor<CategoryRequest> captor = ArgumentCaptor.forClass(CategoryRequest.class);
        verify(categoryService).create(captor.capture());
        // The request type only carries the name, so id and active cannot reach the service
        assertThat(captor.getValue()).isEqualTo(new CategoryRequest("EPP"));
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
        when(categoryService.create(any(CategoryRequest.class)))
                .thenThrow(new ResourceConflictException("A category with the name 'EPP' already exists"));

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
        when(categoryService.findAll()).thenReturn(List.of(
                new CategoryResponse(1L, "EPP", true),
                new CategoryResponse(2L, "Extintores", false)));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].active").value(false));
    }

    @Test
    void findByIdReturns200() throws Exception {
        when(categoryService.findById(1L)).thenReturn(new CategoryResponse(1L, "EPP", true));

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("EPP"));
    }

    @Test
    void findByIdReturns404ProblemDetailWhenMissing() throws Exception {
        when(categoryService.findById(99L)).thenThrow(new ResourceNotFoundException("Category not found with id 99"));

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
        when(categoryService.update(eq(1L), eq(new CategoryRequest("Gases"))))
                .thenReturn(new CategoryResponse(1L, "Gases", true));

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Gases\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gases"));
    }

    @Test
    void updateCannotModifyActiveThroughTheRequestBody() throws Exception {
        when(categoryService.update(eq(1L), any(CategoryRequest.class)))
                .thenReturn(new CategoryResponse(1L, "Gases", true));

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Gases\",\"active\":false}"))
                .andExpect(status().isOk());

        // Only the name reaches the service; the active flag sent by the client is dropped
        verify(categoryService).update(1L, new CategoryRequest("Gases"));
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
        when(categoryService.update(eq(99L), any(CategoryRequest.class)))
                .thenThrow(new ResourceNotFoundException("Category not found with id 99"));

        mockMvc.perform(put("/api/categories/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Gases\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReturns409WhenNameBelongsToAnotherCategory() throws Exception {
        when(categoryService.update(eq(1L), any(CategoryRequest.class)))
                .thenThrow(new ResourceConflictException("A category with the name 'Gases' already exists"));

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Gases\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(PROBLEM_JSON));
    }

    // PATCH

    @Test
    void activateReturns200WithoutBody() throws Exception {
        when(categoryService.activate(1L)).thenReturn(new CategoryResponse(1L, "EPP", true));

        mockMvc.perform(patch("/api/categories/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void deactivateReturns200WithoutBody() throws Exception {
        when(categoryService.deactivate(1L)).thenReturn(new CategoryResponse(1L, "EPP", false));

        mockMvc.perform(patch("/api/categories/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void activateAndDeactivateReturn404WhenMissing() throws Exception {
        when(categoryService.activate(99L)).thenThrow(new ResourceNotFoundException("Category not found with id 99"));
        when(categoryService.deactivate(99L)).thenThrow(new ResourceNotFoundException("Category not found with id 99"));

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
