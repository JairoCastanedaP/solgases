package com.solgases.infrastructure.api.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.solgases.application.dto.CategoryResult;
import com.solgases.application.exception.CategoryNotFoundException;
import com.solgases.infrastructure.api.rest.error.GlobalExceptionHandler;
import com.solgases.application.exception.ConflictException;
import com.solgases.application.dto.ProductCommand;
import com.solgases.application.dto.ProductQuery;
import com.solgases.application.dto.ProductResult;
import com.solgases.application.exception.DuplicateProductSkuException;
import com.solgases.application.exception.ProductNotFoundException;
import com.solgases.application.port.in.ActivateProductUseCase;
import com.solgases.application.port.in.CreateProductUseCase;
import com.solgases.application.port.in.DeactivateProductUseCase;
import com.solgases.application.port.in.GetProductByIdUseCase;
import com.solgases.application.port.in.ListProductsUseCase;
import com.solgases.application.port.in.UpdateProductUseCase;
import com.solgases.infrastructure.api.dto.ProductResponse;
import com.solgases.application.dto.UnitOfMeasureResult;
import java.math.BigDecimal;
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
class ProductControllerTest {

    private static final MediaType PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON;

    @Mock private CreateProductUseCase createUseCase;
    @Mock private ListProductsUseCase listUseCase;
    @Mock private GetProductByIdUseCase getByIdUseCase;
    @Mock private UpdateProductUseCase updateUseCase;
    @Mock private ActivateProductUseCase activateUseCase;
    @Mock private DeactivateProductUseCase deactivateUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(createUseCase, listUseCase, getByIdUseCase,
                        updateUseCase, activateUseCase, deactivateUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private static ProductResult sampleResult(Long id, boolean active) {
        return new ProductResult(id, "EPP-001", "Casco", "desc", "3M", "H-700", new BigDecimal("85000.00"), active,
                new CategoryResult(1L, "EPP", true), new UnitOfMeasureResult(1L, "UN", "Unidad", true));
    }

    private static final String VALID_BODY =
            "{\"sku\":\"EPP-001\",\"name\":\"Casco\",\"description\":\"desc\",\"brand\":\"3M\",\"reference\":\"H-700\","
                    + "\"price\":85000.00,\"categoryId\":1,\"unitOfMeasureId\":1}";

    @Test
    void createReturns201WithLocationAndBody() throws Exception {
        when(createUseCase.execute(any(ProductCommand.class))).thenReturn(sampleResult(5L, true));

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/products/5"))
                .andExpect(jsonPath("$.sku").value("EPP-001"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.category.id").value(1))
                .andExpect(jsonPath("$.unitOfMeasure.code").value("UN"));
    }

    @Test
    void createIgnoresIdAndActiveSentByTheClient() throws Exception {
        when(createUseCase.execute(any(ProductCommand.class))).thenReturn(sampleResult(1L, true));

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":999,\"active\":false,\"sku\":\"EPP-001\",\"name\":\"Casco\","
                                + "\"price\":85000.00,\"categoryId\":1,\"unitOfMeasureId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void createWithBlankNameReturns400() throws Exception {
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"EPP-001\",\"name\":\"\",\"price\":1,\"categoryId\":1,\"unitOfMeasureId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.errors[0].field").value("name"));
    }

    @Test
    void createWithNonPositivePriceReturns400() throws Exception {
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"EPP-001\",\"name\":\"Casco\",\"price\":0,\"categoryId\":1,\"unitOfMeasureId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("price"));
    }

    @Test
    void createWithDuplicateSkuReturns409() throws Exception {
        when(createUseCase.execute(any(ProductCommand.class))).thenThrow(new DuplicateProductSkuException("EPP-001"));

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(PROBLEM_JSON));
    }

    @Test
    void createWithNonExistingCategoryReturns404() throws Exception {
        when(createUseCase.execute(any(ProductCommand.class))).thenThrow(new CategoryNotFoundException(99L));

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void createWithInactiveCategoryReturns409() throws Exception {
        when(createUseCase.execute(any(ProductCommand.class)))
                .thenThrow(new ConflictException("Category with id 1 is not active"));

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    @Test
    void findAllWithoutFiltersUsesNullParameters() throws Exception {
        when(listUseCase.execute(new ProductQuery(null, null, null))).thenReturn(List.of(sampleResult(1L, true)));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findAllWithCombinedFiltersPassesThemToTheService() throws Exception {
        when(listUseCase.execute(new ProductQuery("casco", 1L, true))).thenReturn(List.of(sampleResult(1L, true)));

        mockMvc.perform(get("/api/products").param("name", "casco").param("categoryId", "1").param("active", "true"))
                .andExpect(status().isOk());

        verify(listUseCase).execute(new ProductQuery("casco", 1L, true));
    }

    @Test
    void findByIdReturns200() throws Exception {
        when(getByIdUseCase.execute(1L)).thenReturn(sampleResult(1L, true));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("EPP-001"));
    }

    @Test
    void findByIdReturns404WhenMissing() throws Exception {
        when(getByIdUseCase.execute(99L)).thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(PROBLEM_JSON));
    }

    @Test
    void updateReturns200() throws Exception {
        when(updateUseCase.execute(eq(1L), any(ProductCommand.class))).thenReturn(sampleResult(1L, false));

        mockMvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isOk());
    }

    @Test
    void updateCannotModifyActiveThroughTheRequestBody() throws Exception {
        when(updateUseCase.execute(eq(1L), any(ProductCommand.class))).thenReturn(sampleResult(1L, true));

        mockMvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\":false,\"sku\":\"EPP-001\",\"name\":\"Casco\",\"price\":85000.00,"
                                + "\"categoryId\":1,\"unitOfMeasureId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void updateReturns404WhenMissing() throws Exception {
        when(updateUseCase.execute(eq(99L), any(ProductCommand.class))).thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(put("/api/products/99").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReturns409WhenSkuBelongsToAnotherProduct() throws Exception {
        when(updateUseCase.execute(eq(1L), any(ProductCommand.class))).thenThrow(new DuplicateProductSkuException("EPP-001"));

        mockMvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    @Test
    void updateWithInactiveUnitOfMeasureReturns409() throws Exception {
        when(updateUseCase.execute(eq(1L), any(ProductCommand.class)))
                .thenThrow(new ConflictException("Unit of measure with id 1 is not active"));

        mockMvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    @Test
    void activateReturns200WithoutBody() throws Exception {
        when(activateUseCase.execute(1L)).thenReturn(sampleResult(1L, true));

        mockMvc.perform(patch("/api/products/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void deactivateReturns200WithoutBody() throws Exception {
        when(deactivateUseCase.execute(1L)).thenReturn(sampleResult(1L, false));

        mockMvc.perform(patch("/api/products/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void activateAndDeactivateReturn404WhenMissing() throws Exception {
        when(activateUseCase.execute(99L)).thenThrow(new ProductNotFoundException(99L));
        when(deactivateUseCase.execute(99L)).thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(patch("/api/products/99/activate")).andExpect(status().isNotFound());
        mockMvc.perform(patch("/api/products/99/deactivate")).andExpect(status().isNotFound());
    }
}
