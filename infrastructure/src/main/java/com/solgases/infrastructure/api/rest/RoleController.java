package com.solgases.infrastructure.api.rest;

import com.solgases.application.port.in.CreateRoleUseCase;
import com.solgases.application.port.in.GetRoleByIdUseCase;
import com.solgases.application.port.in.ListRolesUseCase;
import com.solgases.application.port.in.UpdateRoleUseCase;
import com.solgases.infrastructure.api.dto.RoleCreateRequest;
import com.solgases.infrastructure.api.dto.RoleResponse;
import com.solgases.infrastructure.api.dto.RoleUpdateRequest;
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
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Administration of roles and their permissions. No authentication is applied "
        + "yet: intended for local development use only.")
public class RoleController {

    private static final String PROBLEM_JSON = "application/problem+json";

    private final CreateRoleUseCase createRoleUseCase;
    private final ListRolesUseCase listRolesUseCase;
    private final GetRoleByIdUseCase getRoleByIdUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;

    public RoleController(CreateRoleUseCase createRoleUseCase, ListRolesUseCase listRolesUseCase,
            GetRoleByIdUseCase getRoleByIdUseCase, UpdateRoleUseCase updateRoleUseCase) {
        this.createRoleUseCase = createRoleUseCase;
        this.listRolesUseCase = listRolesUseCase;
        this.getRoleByIdUseCase = getRoleByIdUseCase;
        this.updateRoleUseCase = updateRoleUseCase;
    }

    @PostMapping
    @Operation(summary = "Create a role",
            description = "Creates a role with its internal key, name and permissions (possibly none). "
                    + "The key cannot be changed afterwards.")
    @ApiResponse(responseCode = "201", description = "Role created",
            content = @Content(schema = @Schema(implementation = RoleResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "One or more permissions do not exist",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "A role with the same key or name already exists",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody RoleCreateRequest request) {
        RoleResponse created = RoleApiMapper.toResponse(createRoleUseCase.execute(RoleApiMapper.toCommand(request)));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @Operation(summary = "List roles",
            description = "Returns all roles with their permissions. No filtering, sorting or pagination is applied.")
    @ApiResponse(responseCode = "200", description = "List of roles",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoleResponse.class))))
    public List<RoleResponse> findAll() {
        return listRolesUseCase.execute().stream().map(RoleApiMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a role by id")
    @ApiResponse(responseCode = "200", description = "Role found",
            content = @Content(schema = @Schema(implementation = RoleResponse.class)))
    @ApiResponse(responseCode = "400", description = "The id is not a valid number",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Role not found",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    public RoleResponse findById(@Parameter(description = "Role identifier", example = "1") @PathVariable Long id) {
        return RoleApiMapper.toResponse(getRoleByIdUseCase.execute(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a role",
            description = "Replaces the name and the permissions of the role. The key is never modified.")
    @ApiResponse(responseCode = "200", description = "Role updated",
            content = @Content(schema = @Schema(implementation = RoleResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid id or request body",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Role or one or more permissions not found",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Another role already uses the same name",
            content = @Content(mediaType = PROBLEM_JSON, schema = @Schema(implementation = ProblemDetail.class)))
    public RoleResponse update(@Parameter(description = "Role identifier", example = "1") @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request) {
        return RoleApiMapper.toResponse(updateRoleUseCase.execute(id, RoleApiMapper.toCommand(request)));
    }
}
