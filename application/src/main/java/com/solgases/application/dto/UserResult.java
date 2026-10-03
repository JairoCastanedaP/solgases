package com.solgases.application.dto;

import com.solgases.domain.model.User;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public record UserResult(Long id, String username, String displayName, boolean active, List<RoleResult> roles,
        Instant createdAt, Instant updatedAt) {

    public static UserResult from(User user) {
        List<RoleResult> roles = user.roles().stream()
                .map(RoleResult::from)
                .sorted(Comparator.comparing(RoleResult::id))
                .toList();
        return new UserResult(user.id(), user.username(), user.displayName(), user.active(), roles, user.createdAt(),
                user.updatedAt());
    }
}
