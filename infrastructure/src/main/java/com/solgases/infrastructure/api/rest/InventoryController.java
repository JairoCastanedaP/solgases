package com.solgases.infrastructure.api.rest;

import com.solgases.infrastructure.api.dto.InventoryAdjustmentRequest;
import com.solgases.infrastructure.api.dto.InventoryMovementRequest;
import com.solgases.infrastructure.api.dto.InventoryMovementResponse;
import com.solgases.infrastructure.api.dto.InventoryResponse;
import com.solgases.application.port.in.GetInventoryMovementUseCase;
import com.solgases.application.port.in.GetInventoryUseCase;
import com.solgases.application.port.in.ListInventoryMovementsUseCase;
import com.solgases.application.port.in.RegisterInventoryAdjustmentUseCase;
import com.solgases.application.port.in.RegisterInventoryEntryUseCase;
import com.solgases.application.port.in.RegisterInventoryExitUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/products/{productId}/inventory")
@Tag(name = "Inventory", description = "Stock and inventory movements of a product")
public class InventoryController {

    private static final String PROBLEM_JSON = "application/problem+json";

    private final GetInventoryUseCase getInventory;
    private final ListInventoryMovementsUseCase listMovements;
    private final GetInventoryMovementUseCase getMovement;
    private final RegisterInventoryEntryUseCase registerEntry;
    private final RegisterInventoryExitUseCase registerExit;
    private final RegisterInventoryAdjustmentUseCase registerAdjustment;

    public InventoryController(GetInventoryUseCase getInventory, ListInventoryMovementsUseCase listMovements,
            GetInventoryMovementUseCase getMovement, RegisterInventoryEntryUseCase registerEntry,
            RegisterInventoryExitUseCase registerExit, RegisterInventoryAdjustmentUseCase registerAdjustment) {
        this.getInventory = getInventory;
        this.listMovements = listMovements;
        this.getMovement = getMovement;
        this.registerEntry = registerEntry;
        this.registerExit = registerExit;
        this.registerAdjustment = registerAdjustment;
    }

    @GetMapping
    @Operation(summary = "Get the current stock of a product",
            description = "Returns 0 if the product has no inventory movement yet; no record is created by this call.")
    @ApiResponse(responseCode = "200", description = "Current stock",
            content = @Content(schema = @Schema(implementation = InventoryResponse.class)))
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    public InventoryResponse getCurrentQuantity(
            @Parameter(description = "Product identifier", example = "1") @PathVariable Long productId) {
        var result = getInventory.execute(productId);
        return InventoryApiMapper.toResponse(result);
    }

    @GetMapping("/movements")
    @Operation(summary = "List the inventory movements of a product",
            description = "Returns every movement registered for the product, most recent first.")
    @ApiResponse(responseCode = "200", description = "List of movements",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = InventoryMovementResponse.class))))
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    public List<InventoryMovementResponse> getMovements(
            @Parameter(description = "Product identifier", example = "1") @PathVariable Long productId) {
        return listMovements.execute(productId).stream().map(InventoryApiMapper::toResponse).toList();
    }

    @GetMapping("/movements/{movementId}")
    @Operation(summary = "Get a single inventory movement of a product")
    @ApiResponse(responseCode = "200", description = "Movement found",
            content = @Content(schema = @Schema(implementation = InventoryMovementResponse.class)))
    @ApiResponse(responseCode = "404", description = "Product or movement not found",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    public InventoryMovementResponse getMovement(
            @Parameter(description = "Product identifier", example = "1") @PathVariable Long productId,
            @Parameter(description = "Movement identifier", example = "42") @PathVariable Long movementId) {
        return InventoryApiMapper.toResponse(getMovement.execute(productId, movementId));
    }

    @PostMapping("/entries")
    @Operation(summary = "Register a stock entry",
            description = "Increases the current stock. Creates the Inventory row if this is the product's first movement.")
    @ApiResponse(responseCode = "201", description = "Entry registered",
            content = @Content(schema = @Schema(implementation = InventoryMovementResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "The product is not active, or a concurrent update was detected",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<InventoryMovementResponse> registerEntry(
            @Parameter(description = "Product identifier", example = "1") @PathVariable Long productId,
            @Valid @RequestBody InventoryMovementRequest request) {
        return created(productId, InventoryApiMapper.toResponse(
                registerEntry.execute(productId, InventoryApiMapper.toCommand(request))));
    }

    @PostMapping("/exits")
    @Operation(summary = "Register a stock exit",
            description = "Decreases the current stock. Rejected if it would leave the stock negative.")
    @ApiResponse(responseCode = "201", description = "Exit registered",
            content = @Content(schema = @Schema(implementation = InventoryMovementResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409",
            description = "The product is not active, the stock is insufficient, or a concurrent update was detected",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<InventoryMovementResponse> registerExit(
            @Parameter(description = "Product identifier", example = "1") @PathVariable Long productId,
            @Valid @RequestBody InventoryMovementRequest request) {
        return created(productId, InventoryApiMapper.toResponse(
                registerExit.execute(productId, InventoryApiMapper.toCommand(request))));
    }

    @PostMapping("/adjustments")
    @Operation(summary = "Register a stock adjustment",
            description = "Increases or decreases the current stock according to direction. "
                    + "A DECREASE adjustment is rejected if it would leave the stock negative.")
    @ApiResponse(responseCode = "201", description = "Adjustment registered",
            content = @Content(schema = @Schema(implementation = InventoryMovementResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409",
            description = "The product is not active, the stock is insufficient, or a concurrent update was detected",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<InventoryMovementResponse> registerAdjustment(
            @Parameter(description = "Product identifier", example = "1") @PathVariable Long productId,
            @Valid @RequestBody InventoryAdjustmentRequest request) {
        return created(productId, InventoryApiMapper.toResponse(
                registerAdjustment.execute(productId, InventoryApiMapper.toCommand(request))));
    }

    private ResponseEntity<InventoryMovementResponse> created(Long productId, InventoryMovementResponse response) {
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/products/{productId}/inventory/movements/{movementId}")
                .buildAndExpand(productId, response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }
}
