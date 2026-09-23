package com.solgases.unitofmeasure.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.solgases.exception.GlobalExceptionHandler;
import com.solgases.exception.ResourceConflictException;
import com.solgases.exception.ResourceNotFoundException;
import com.solgases.unitofmeasure.dto.UnitOfMeasureRequest;
import com.solgases.unitofmeasure.dto.UnitOfMeasureResponse;
import com.solgases.unitofmeasure.service.UnitOfMeasureService;
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
class UnitOfMeasureControllerTest {

    private static final MediaType PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON;

    @Mock
    private UnitOfMeasureService unitOfMeasureService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UnitOfMeasureController(unitOfMeasureService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createReturns201WithLocationAndBody() throws Exception {
        when(unitOfMeasureService.create(new UnitOfMeasureRequest("UN", "Unidad")))
                .thenReturn(new UnitOfMeasureResponse(1L, "UN", "Unidad", true));

        mockMvc.perform(post("/api/units-of-measure")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"UN\",\"name\":\"Unidad\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/units-of-measure/1"))
                .andExpect(jsonPath("$.code").value("UN"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void createWithBlankCodeReturns400() throws Exception {
        mockMvc.perform(post("/api/units-of-measure")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"\",\"name\":\"Unidad\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.errors[0].field").value("code"));
    }

    @Test
    void createWithDuplicateCodeReturns409() throws Exception {
        when(unitOfMeasureService.create(any(UnitOfMeasureRequest.class)))
                .thenThrow(new ResourceConflictException("A unit of measure with the code 'UN' already exists"));

        mockMvc.perform(post("/api/units-of-measure")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"UN\",\"name\":\"Unidad\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(PROBLEM_JSON));
    }

    @Test
    void findAllReturns200WithList() throws Exception {
        when(unitOfMeasureService.findAll()).thenReturn(List.of(new UnitOfMeasureResponse(1L, "UN", "Unidad", true)));

        mockMvc.perform(get("/api/units-of-measure"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findByIdReturns404WhenMissing() throws Exception {
        when(unitOfMeasureService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Unit of measure not found with id 99"));

        mockMvc.perform(get("/api/units-of-measure/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(PROBLEM_JSON));
    }

    @Test
    void updateReturns200AndCannotModifyActive() throws Exception {
        when(unitOfMeasureService.update(eq(1L), any(UnitOfMeasureRequest.class)))
                .thenReturn(new UnitOfMeasureResponse(1L, "KG", "Kilogramo", true));

        mockMvc.perform(put("/api/units-of-measure/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"KG\",\"name\":\"Kilogramo\",\"active\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void updateReturns409WhenCodeBelongsToAnotherUnit() throws Exception {
        when(unitOfMeasureService.update(eq(1L), any(UnitOfMeasureRequest.class)))
                .thenThrow(new ResourceConflictException("A unit of measure with the code 'KG' already exists"));

        mockMvc.perform(put("/api/units-of-measure/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"KG\",\"name\":\"Kilogramo\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void activateAndDeactivateReturn200WithoutBody() throws Exception {
        when(unitOfMeasureService.activate(1L)).thenReturn(new UnitOfMeasureResponse(1L, "UN", "Unidad", true));
        when(unitOfMeasureService.deactivate(1L)).thenReturn(new UnitOfMeasureResponse(1L, "UN", "Unidad", false));

        mockMvc.perform(patch("/api/units-of-measure/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
        mockMvc.perform(patch("/api/units-of-measure/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void activateReturns404WhenMissing() throws Exception {
        when(unitOfMeasureService.activate(99L))
                .thenThrow(new ResourceNotFoundException("Unit of measure not found with id 99"));

        mockMvc.perform(patch("/api/units-of-measure/99/activate"))
                .andExpect(status().isNotFound());
    }
}
