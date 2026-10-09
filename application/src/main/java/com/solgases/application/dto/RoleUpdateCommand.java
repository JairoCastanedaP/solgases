package com.solgases.application.dto;

import java.util.Set;

public record RoleUpdateCommand(String name, Set<Long> permissionIds) {
}
