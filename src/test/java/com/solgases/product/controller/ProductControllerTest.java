package com.solgases.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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

import com.solgases.category.dto.CategoryResponse;
import com.solgases.exception.GlobalExceptionHandler;
import com.solgases.exception.ResourceConflictException;
import com.solgases.exception.ResourceNotFoundException;
import com.solgases.product.dto.ProductRequest;
import com.solgases.product.dto.ProductResponse;
import com.solgases.product.service.ProductService;
import com.solgases.unitofmeasure.dto.UnitOfMeasureResponse;
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

    @Mock
    private ProductService productService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private static ProductResponse sampleResponse(Long id, boolean active) {
        return new ProductResponse(id, "EPP-001", "Casco", "desc", "3M", "H-700", new BigDecimal("85000.00"), active,
                new CategoryResponse(1L, "EPP", true), new UnitOfMeasureResponse(1L, "UN", "Unidad", true));
    }

    private static final String VALID_BODY =
            "{\"sku\":\"EPP-001\",\"name\":\"Casco\",\"description\":\"desc\",\"brand\":\"3M\",\"reference\":\"H-700\","
                    + "\"price\":85000.00,\"categoryId\":1,\"unitOfMeasureId\":1}";

    @Test
    void createReturns201WithLocationAndBody() throws Exception {
        when(productService.create(any(ProductRequest.class))).thenReturn(sampleResponse(5L, true));

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
        when(productService.create(any(ProductRequest.class))).thenReturn(sampleResponse(1L, true));

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
        when(productService.create(any(ProductRequest.class)))
                .thenThrow(new ResourceConflictException("A product with the SKU 'EPP-001' already exists"));

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(PROBLEM_JSON));
    }

    @Test
    void createWithNonExistingCategoryReturns404() throws Exception {
        when(productService.create(any(ProductRequest.class)))
                .thenThrow(new ResourceNotFoundException("Category not found with id 99"));

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void createWithInactiveCategoryReturns409() throws Exception {
        when(productService.create(any(ProductRequest.class)))
                .thenThrow(new ResourceConflictException("Category with id 1 is not active"));

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    @Test
    void findAllWithoutFiltersUsesNullParameters() throws Exception {
        when(productService.findAll(isNull(), isNull(), isNull())).thenReturn(List.of(sampleResponse(1L, true)));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findAllWithCombinedFiltersPassesThemToTheService() throws Exception {
        when(productService.findAll("casco", 1L, true)).thenReturn(List.of(sampleResponse(1L, true)));

        mockMvc.perform(get("/api/products").param("name", "casco").param("categoryId", "1").param("active", "true"))
                .andExpect(status().isOk());

        verify(productService).findAll("casco", 1L, true);
    }

    @Test
    void findByIdReturns200() throws Exception {
        when(productService.findById(1L)).thenReturn(sampleResponse(1L, true));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("EPP-001"));
    }

    @Test
    void findByIdReturns404WhenMissing() throws Exception {
        when(productService.findById(99L)).thenThrow(new ResourceNotFoundException("Product not found with id 99"));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(PROBLEM_JSON));
    }

    @Test
    void updateReturns200() throws Exception {
        when(productService.update(eq(1L), any(ProductRequest.class))).thenReturn(sampleResponse(1L, false));

        mockMvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isOk());
    }

    @Test
    void updateCannotModifyActiveThroughTheRequestBody() throws Exception {
        when(productService.update(eq(1L), any(ProductRequest.class))).thenReturn(sampleResponse(1L, true));

        mockMvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\":false,\"sku\":\"EPP-001\",\"name\":\"Casco\",\"price\":85000.00,"
                                + "\"categoryId\":1,\"unitOfMeasureId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void updateReturns404WhenMissing() throws Exception {
        when(productService.update(eq(99L), any(ProductRequest.class)))
                .thenThrow(new ResourceNotFoundException("Product not found with id 99"));

        mockMvc.perform(put("/api/products/99").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReturns409WhenSkuBelongsToAnotherProduct() throws Exception {
        when(productService.update(eq(1L), any(ProductRequest.class)))
                .thenThrow(new ResourceConflictException("A product with the SKU 'EPP-001' already exists"));

        mockMvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    @Test
    void updateWithInactiveUnitOfMeasureReturns409() throws Exception {
        when(productService.update(eq(1L), any(ProductRequest.class)))
                .thenThrow(new ResourceConflictException("Unit of measure with id 1 is not active"));

        mockMvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    @Test
    void activateReturns200WithoutBody() throws Exception {
        when(productService.activate(1L)).thenReturn(sampleResponse(1L, true));

        mockMvc.perform(patch("/api/products/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void deactivateReturns200WithoutBody() throws Exception {
        when(productService.deactivate(1L)).thenReturn(sampleResponse(1L, false));

        mockMvc.perform(patch("/api/products/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void activateAndDeactivateReturn404WhenMissing() throws Exception {
        when(productService.activate(99L)).thenThrow(new ResourceNotFoundException("Product not found with id 99"));
        when(productService.deactivate(99L)).thenThrow(new ResourceNotFoundException("Product not found with id 99"));

        mockMvc.perform(patch("/api/products/99/activate")).andExpect(status().isNotFound());
        mockMvc.perform(patch("/api/products/99/deactivate")).andExpect(status().isNotFound());
    }
}
