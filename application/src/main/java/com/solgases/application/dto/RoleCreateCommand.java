package com.solgases.application.dto;

import java.util.Set;

public record RoleCreateCommand(String key, String name, Set<Long> permissionIds) {
}
