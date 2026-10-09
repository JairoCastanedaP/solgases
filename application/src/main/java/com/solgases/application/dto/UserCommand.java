package com.solgases.application.dto;

import java.util.Set;

public record UserCommand(String username, String displayName, Set<Long> roleIds) {
}
