package com.solgases.inventory.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.solgases.exception.GlobalExceptionHandler;
import com.solgases.exception.ResourceConflictException;
import com.solgases.exception.ResourceNotFoundException;
import com.solgases.inventory.dto.InventoryAdjustmentRequest;
import com.solgases.inventory.dto.InventoryMovementRequest;
import com.solgases.inventory.dto.InventoryMovementResponse;
import com.solgases.inventory.dto.InventoryResponse;
import com.solgases.inventory.entity.MovementDirection;
import com.solgases.inventory.entity.MovementType;
import com.solgases.inventory.service.InventoryService;
import java.math.BigDecimal;
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
class InventoryControllerTest {

    private static final MediaType PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON;

    @Mock
    private InventoryService inventoryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new InventoryController(inventoryService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private static InventoryMovementResponse movementResponse(Long id, MovementType type, MovementDirection direction) {
        return new InventoryMovementResponse(id, 1L, type, direction, new BigDecimal("10.000"), "Reason", "user",
                Instant.parse("2026-09-24T10:00:00Z"));
    }

    // GET current quantity

    @Test
    void getCurrentQuantityReturns200() throws Exception {
        when(inventoryService.getCurrentQuantity(1L)).thenReturn(new InventoryResponse(1L, new BigDecimal("5.000")));

        mockMvc.perform(get("/api/products/1/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.currentQuantity").value(5.0));
    }

    @Test
    void getCurrentQuantityReturns404WhenProductMissing() throws Exception {
        when(inventoryService.getCurrentQuantity(99L)).thenThrow(new ResourceNotFoundException("Product not found with id 99"));

        mockMvc.perform(get("/api/products/99/inventory"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(PROBLEM_JSON));
    }

    // GET movements

    @Test
    void getMovementsReturns200WithList() throws Exception {
        when(inventoryService.getMovements(1L)).thenReturn(List.of(movementResponse(1L, MovementType.ENTRY, MovementDirection.INCREASE)));

        mockMvc.perform(get("/api/products/1/inventory/movements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getMovementsReturns404WhenProductMissing() throws Exception {
        when(inventoryService.getMovements(99L)).thenThrow(new ResourceNotFoundException("Product not found with id 99"));

        mockMvc.perform(get("/api/products/99/inventory/movements"))
                .andExpect(status().isNotFound());
    }

    // GET single movement

    @Test
    void getMovementReturns200() throws Exception {
        when(inventoryService.getMovement(1L, 5L)).thenReturn(movementResponse(5L, MovementType.EXIT, MovementDirection.DECREASE));

        mockMvc.perform(get("/api/products/1/inventory/movements/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.type").value("EXIT"));
    }

    @Test
    void getMovementReturns404WhenMissing() throws Exception {
        when(inventoryService.getMovement(1L, 99L)).thenThrow(new ResourceNotFoundException("Movement not found"));

        mockMvc.perform(get("/api/products/1/inventory/movements/99"))
                .andExpect(status().isNotFound());
    }

    // POST entries

    @Test
    void registerEntryReturns201WithLocationToTheMovement() throws Exception {
        when(inventoryService.registerEntry(eq(1L), any(InventoryMovementRequest.class)))
                .thenReturn(movementResponse(42L, MovementType.ENTRY, MovementDirection.INCREASE));

        mockMvc.perform(post("/api/products/1/inventory/entries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":10,\"reason\":\"Compra\",\"responsibleUser\":\"jcastaneda\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/products/1/inventory/movements/42"))
                .andExpect(jsonPath("$.type").value("ENTRY"))
                .andExpect(jsonPath("$.direction").value("INCREASE"));
    }

    @Test
    void registerEntryWithBlankReasonReturns400() throws Exception {
        mockMvc.perform(post("/api/products/1/inventory/entries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":10,\"reason\":\"\",\"responsibleUser\":\"jcastaneda\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(PROBLEM_JSON))
                .andExpect(jsonPath("$.errors[0].field").value("reason"));
    }

    @Test
    void registerEntryWithZeroQuantityReturns400() throws Exception {
        mockMvc.perform(post("/api/products/1/inventory/entries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":0,\"reason\":\"Compra\",\"responsibleUser\":\"jcastaneda\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("quantity"));
    }

    @Test
    void registerEntryReturns404WhenProductMissing() throws Exception {
        when(inventoryService.registerEntry(eq(99L), any(InventoryMovementRequest.class)))
                .thenThrow(new ResourceNotFoundException("Product not found with id 99"));

        mockMvc.perform(post("/api/products/99/inventory/entries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":10,\"reason\":\"Compra\",\"responsibleUser\":\"jcastaneda\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void registerEntryReturns409WhenProductInactive() throws Exception {
        when(inventoryService.registerEntry(eq(1L), any(InventoryMovementRequest.class)))
                .thenThrow(new ResourceConflictException("Product with id 1 is not active"));

        mockMvc.perform(post("/api/products/1/inventory/entries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":10,\"reason\":\"Compra\",\"responsibleUser\":\"jcastaneda\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(PROBLEM_JSON));
    }

    // POST exits

    @Test
    void registerExitReturns201() throws Exception {
        when(inventoryService.registerExit(eq(1L), any(InventoryMovementRequest.class)))
                .thenReturn(movementResponse(43L, MovementType.EXIT, MovementDirection.DECREASE));

        mockMvc.perform(post("/api/products/1/inventory/exits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":3,\"reason\":\"Venta\",\"responsibleUser\":\"jcastaneda\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/products/1/inventory/movements/43"));
    }

    @Test
    void registerExitReturns409WhenStockInsufficient() throws Exception {
        when(inventoryService.registerExit(eq(1L), any(InventoryMovementRequest.class)))
                .thenThrow(new ResourceConflictException("Insufficient stock"));

        mockMvc.perform(post("/api/products/1/inventory/exits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":300,\"reason\":\"Venta\",\"responsibleUser\":\"jcastaneda\"}"))
                .andExpect(status().isConflict());
    }

    // POST adjustments

    @Test
    void registerAdjustmentIncreaseReturns201() throws Exception {
        when(inventoryService.registerAdjustment(eq(1L), any(InventoryAdjustmentRequest.class)))
                .thenReturn(movementResponse(44L, MovementType.ADJUSTMENT, MovementDirection.INCREASE));

        mockMvc.perform(post("/api/products/1/inventory/adjustments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":2,\"direction\":\"INCREASE\",\"reason\":\"Conteo\",\"responsibleUser\":\"jcastaneda\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.direction").value("INCREASE"));
    }

    @Test
    void registerAdjustmentDecreaseReturns201() throws Exception {
        when(inventoryService.registerAdjustment(eq(1L), any(InventoryAdjustmentRequest.class)))
                .thenReturn(movementResponse(45L, MovementType.ADJUSTMENT, MovementDirection.DECREASE));

        mockMvc.perform(post("/api/products/1/inventory/adjustments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":2,\"direction\":\"DECREASE\",\"reason\":\"Conteo\",\"responsibleUser\":\"jcastaneda\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.direction").value("DECREASE"));
    }

    @Test
    void registerAdjustmentWithoutDirectionReturns400() throws Exception {
        mockMvc.perform(post("/api/products/1/inventory/adjustments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":2,\"reason\":\"Conteo\",\"responsibleUser\":\"jcastaneda\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("direction"));
    }

    @Test
    void registerAdjustmentReturns409OnConcurrencyConflict() throws Exception {
        when(inventoryService.registerAdjustment(eq(1L), any(InventoryAdjustmentRequest.class)))
                .thenThrow(new ResourceConflictException("Concurrent inventory update detected"));

        mockMvc.perform(post("/api/products/1/inventory/adjustments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":2,\"direction\":\"DECREASE\",\"reason\":\"Conteo\",\"responsibleUser\":\"jcastaneda\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(PROBLEM_JSON));
    }
}
