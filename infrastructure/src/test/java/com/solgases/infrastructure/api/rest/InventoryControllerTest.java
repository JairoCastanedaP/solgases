package com.solgases.infrastructure.api.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.solgases.infrastructure.api.rest.error.GlobalExceptionHandler;
import com.solgases.application.exception.ConflictException;
import com.solgases.application.dto.InventoryAdjustmentCommand;
import com.solgases.application.dto.InventoryMovementCommand;
import com.solgases.application.dto.InventoryMovementResult;
import com.solgases.application.dto.InventoryResult;
import com.solgases.application.exception.InventoryMovementNotFoundException;
import com.solgases.application.port.in.*;
import com.solgases.infrastructure.api.rest.InventoryController;
import com.solgases.domain.model.MovementDirection;
import com.solgases.domain.model.MovementType;
import com.solgases.application.exception.ProductNotFoundException;
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
    @Mock GetInventoryUseCase getInventory;
    @Mock ListInventoryMovementsUseCase listMovements;
    @Mock GetInventoryMovementUseCase getMovement;
    @Mock RegisterInventoryEntryUseCase registerEntry;
    @Mock RegisterInventoryExitUseCase registerExit;
    @Mock RegisterInventoryAdjustmentUseCase registerAdjustment;
    private MockMvc mockMvc;

    @BeforeEach void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new InventoryController(getInventory, listMovements, getMovement,
                registerEntry, registerExit, registerAdjustment)).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    private static InventoryMovementResult movement(long id, MovementType type, MovementDirection direction) {
        return new InventoryMovementResult(id, 1L, type, direction, new BigDecimal("10.000"), "Reason", "user",
                Instant.parse("2026-09-24T10:00:00Z"));
    }

    @Test void getCurrentQuantityReturns200() throws Exception {
        when(getInventory.execute(1L)).thenReturn(new InventoryResult(1L, new BigDecimal("5.000")));
        mockMvc.perform(get("/api/products/1/inventory")).andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1)).andExpect(jsonPath("$.currentQuantity").value(5.0));
    }

    @Test void getCurrentQuantityReturns404WhenProductMissing() throws Exception {
        when(getInventory.execute(99L)).thenThrow(new ProductNotFoundException(99L));
        mockMvc.perform(get("/api/products/99/inventory")).andExpect(status().isNotFound());
    }

    @Test void getMovementsReturnsList() throws Exception {
        when(listMovements.execute(1L)).thenReturn(List.of(movement(1, MovementType.ENTRY, MovementDirection.INCREASE)));
        mockMvc.perform(get("/api/products/1/inventory/movements")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].type").value("ENTRY"));
    }

    @Test void getMovementReturns404WhenMissing() throws Exception {
        when(getMovement.execute(1L, 99L)).thenThrow(new InventoryMovementNotFoundException(99L, 1L));
        mockMvc.perform(get("/api/products/1/inventory/movements/99")).andExpect(status().isNotFound());
    }

    @Test void registerEntryReturns201AndLocation() throws Exception {
        when(registerEntry.execute(org.mockito.ArgumentMatchers.eq(1L), any(InventoryMovementCommand.class)))
                .thenReturn(movement(42, MovementType.ENTRY, MovementDirection.INCREASE));
        mockMvc.perform(post("/api/products/1/inventory/entries").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":10,\"reason\":\"Compra\",\"responsibleUser\":\"user\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/products/1/inventory/movements/42"))
                .andExpect(jsonPath("$.type").value("ENTRY"));
    }

    @Test void registerExitPreservesConflictResponse() throws Exception {
        when(registerExit.execute(org.mockito.ArgumentMatchers.eq(1L), any(InventoryMovementCommand.class)))
                .thenThrow(new ConflictException("Insufficient stock"));
        mockMvc.perform(post("/api/products/1/inventory/exits").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":3,\"reason\":\"Venta\",\"responsibleUser\":\"user\"}"))
                .andExpect(status().isConflict()).andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test void registerAdjustmentMapsDirectionAndReturns201() throws Exception {
        when(registerAdjustment.execute(org.mockito.ArgumentMatchers.eq(1L), any(InventoryAdjustmentCommand.class)))
                .thenReturn(movement(44, MovementType.ADJUSTMENT, MovementDirection.DECREASE));
        mockMvc.perform(post("/api/products/1/inventory/adjustments").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":2,\"direction\":\"DECREASE\",\"reason\":\"Conteo\",\"responsibleUser\":\"user\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.direction").value("DECREASE"));
    }

    @Test void invalidEntryReturns400() throws Exception {
        mockMvc.perform(post("/api/products/1/inventory/entries").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":0,\"reason\":\"\",\"responsibleUser\":\"user\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors").isArray());
    }
}
