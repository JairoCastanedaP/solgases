package com.solgases.infrastructure.api.rest;

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

import com.solgases.infrastructure.api.rest.error.GlobalExceptionHandler;
import com.solgases.application.dto.UnitOfMeasureCommand;
import com.solgases.application.dto.UnitOfMeasureResult;
import com.solgases.application.exception.DuplicateUnitOfMeasureCodeException;
import com.solgases.application.exception.UnitOfMeasureNotFoundException;
import com.solgases.application.port.in.ActivateUnitOfMeasureUseCase;
import com.solgases.application.port.in.CreateUnitOfMeasureUseCase;
import com.solgases.application.port.in.DeactivateUnitOfMeasureUseCase;
import com.solgases.application.port.in.GetUnitOfMeasureByIdUseCase;
import com.solgases.application.port.in.ListUnitOfMeasuresUseCase;
import com.solgases.application.port.in.UpdateUnitOfMeasureUseCase;
import com.solgases.infrastructure.api.dto.UnitOfMeasureRequest;
import com.solgases.infrastructure.api.dto.UnitOfMeasureResponse;
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
    private CreateUnitOfMeasureUseCase createUseCase;

    @Mock
    private ListUnitOfMeasuresUseCase listUseCase;

    @Mock
    private GetUnitOfMeasureByIdUseCase getByIdUseCase;

    @Mock
    private UpdateUnitOfMeasureUseCase updateUseCase;

    @Mock
    private ActivateUnitOfMeasureUseCase activateUseCase;

    @Mock
    private DeactivateUnitOfMeasureUseCase deactivateUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UnitOfMeasureController(createUseCase, listUseCase,
                        getByIdUseCase, updateUseCase, activateUseCase, deactivateUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createReturns201WithLocationAndBody() throws Exception {
        when(createUseCase.execute(new UnitOfMeasureCommand("UN", "Unidad")))
                .thenReturn(new UnitOfMeasureResult(1L, "UN", "Unidad", true));

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
        when(createUseCase.execute(any(UnitOfMeasureCommand.class)))
                .thenThrow(new DuplicateUnitOfMeasureCodeException("UN"));

        mockMvc.perform(post("/api/units-of-measure")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"UN\",\"name\":\"Unidad\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(PROBLEM_JSON));
    }

    @Test
    void findAllReturns200WithList() throws Exception {
        when(listUseCase.execute()).thenReturn(List.of(new UnitOfMeasureResult(1L, "UN", "Unidad", true)));

        mockMvc.perform(get("/api/units-of-measure"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findByIdReturns404WhenMissing() throws Exception {
        when(getByIdUseCase.execute(99L)).thenThrow(new UnitOfMeasureNotFoundException(99L));

        mockMvc.perform(get("/api/units-of-measure/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(PROBLEM_JSON));
    }

    @Test
    void updateReturns200AndCannotModifyActive() throws Exception {
        when(updateUseCase.execute(eq(1L), any(UnitOfMeasureCommand.class)))
                .thenReturn(new UnitOfMeasureResult(1L, "KG", "Kilogramo", true));

        mockMvc.perform(put("/api/units-of-measure/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"KG\",\"name\":\"Kilogramo\",\"active\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void updateReturns409WhenCodeBelongsToAnotherUnit() throws Exception {
        when(updateUseCase.execute(eq(1L), any(UnitOfMeasureCommand.class)))
                .thenThrow(new DuplicateUnitOfMeasureCodeException("KG"));

        mockMvc.perform(put("/api/units-of-measure/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"KG\",\"name\":\"Kilogramo\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void activateAndDeactivateReturn200WithoutBody() throws Exception {
        when(activateUseCase.execute(1L)).thenReturn(new UnitOfMeasureResult(1L, "UN", "Unidad", true));
        when(deactivateUseCase.execute(1L)).thenReturn(new UnitOfMeasureResult(1L, "UN", "Unidad", false));

        mockMvc.perform(patch("/api/units-of-measure/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
        mockMvc.perform(patch("/api/units-of-measure/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void activateReturns404WhenMissing() throws Exception {
        when(activateUseCase.execute(99L)).thenThrow(new UnitOfMeasureNotFoundException(99L));

        mockMvc.perform(patch("/api/units-of-measure/99/activate"))
                .andExpect(status().isNotFound());
    }
}
