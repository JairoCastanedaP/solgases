package com.solgases.infrastructure.security;

import org.springframework.http.HttpMethod;

/**
 * One entry of the endpoint–permission matrix: requests with the method and path pattern require the
 * permission with the given stable internal key (never an editable permission code or role name).
 */
public record EndpointPermissionRule(HttpMethod method, String pathPattern, String permissionKey) {
}
