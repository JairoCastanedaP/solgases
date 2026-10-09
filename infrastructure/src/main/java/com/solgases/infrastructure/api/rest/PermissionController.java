package com.solgases.infrastructure.api.rest;

import com.solgases.application.port.in.CreatePermissionUseCase;
import com.solgases.application.port.in.GetPermissionByIdUseCase;
import com.solgases.application.port.in.ListPermissionsUseCase;
import com.solgases.application.port.in.UpdatePermissionUseCase;
import com.solgases.infrastructure.api.dto.PermissionCreateRequest;
import com.solgases.infrastructure.api.dto.PermissionResponse;
import com.solgases.infrastructure.api.dto.PermissionUpdateRequest;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/permissions")
@Tag(name = "Permissions", description = "Administration of permissions. No authentication is applied yet: "
        + "intended for local development use only.")
public class PermissionController {

    private static final String PROBLEM_JSON = "application/problem+json";

    private final CreatePermissionUseCase createPermissionUseCase;
    private final ListPermissionsUseCase listPermissionsUseCase;
    private final GetPermissionByIdUseCase getPermissionByIdUseCase;
    private final UpdatePermissionUseCase updatePermissionUseCase;

    public PermissionController(CreatePermissionUseCase createPermissionUseCase,
            ListPermissionsUseCase listPermissionsUseCase, GetPermissionByIdUseCase getPermissionByIdUseCase,
            UpdatePermissionUseCase updatePermissionUseCase) {
        this.createPermissionUseCase = createPermissionUseCase;
        this.listPermissionsUseCase = listPermissionsUseCase;
        this.getPermissionByIdUseCase = getPermissionByIdUseCase;
        this.updatePermissionUseCase = updatePermissionUseCase;
    }

    @PostMapping
    @Operation(summary = "Create a permission",
            description = "Creates a permission with its internal key and code. The key cannot be changed afterwards.")
    @ApiResponse(responseCode = "201", description = "Permission created",
            content = @Content(schema = @Schema(implementation = PermissionResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "A permission with the same key or code already exists",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<PermissionResponse> create(@Valid @RequestBody PermissionCreateRequest request) {
        PermissionResponse created = PermissionApiMapper.toResponse(
                createPermissionUseCase.execute(PermissionApiMapper.toCommand(request)));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @Operation(summary = "List permissions",
            description = "Returns all permissions. No filtering, sorting or pagination is applied.")
    @ApiResponse(responseCode = "200", description = "List of permissions",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = PermissionResponse.class))))
    public List<PermissionResponse> findAll() {
        return listPermissionsUseCase.execute().stream().map(PermissionApiMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a permission by id")
    @ApiResponse(responseCode = "200", description = "Permission found",
            content = @Content(schema = @Schema(implementation = PermissionResponse.class)))
    @ApiResponse(responseCode = "400", description = "The id is not a valid number",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Permission not found",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    public PermissionResponse findById(
            @Parameter(description = "Permission identifier", example = "1") @PathVariable Long id) {
        return PermissionApiMapper.toResponse(getPermissionByIdUseCase.execute(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a permission",
            description = "Replaces the code of the permission. The key is never modified.")
    @ApiResponse(responseCode = "200", description = "Permission updated",
            content = @Content(schema = @Schema(implementation = PermissionResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid id or request body",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Permission not found",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Another permission already uses the same code",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    public PermissionResponse update(
            @Parameter(description = "Permission identifier", example = "1") @PathVariable Long id,
            @Valid @RequestBody PermissionUpdateRequest request) {
        return PermissionApiMapper.toResponse(updatePermissionUseCase.execute(id, PermissionApiMapper.toCommand(request)));
    }
}
