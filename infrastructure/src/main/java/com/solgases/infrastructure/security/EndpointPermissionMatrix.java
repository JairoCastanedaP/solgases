package com.solgases.infrastructure.security;

import java.util.List;

/** Endpoint–permission matrix. Requests not covered by any rule are denied. */
public record EndpointPermissionMatrix(List<EndpointPermissionRule> rules) {

    public EndpointPermissionMatrix {
        rules = rules == null ? List.of() : List.copyOf(rules);
    }
}
