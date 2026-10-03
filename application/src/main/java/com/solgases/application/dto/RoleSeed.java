package com.solgases.application.dto;

import java.util.Set;

/** A role of the initial catalog, with the keys of the permissions it receives when first created. */
public record RoleSeed(String key, String name, Set<String> permissionKeys) {
}
