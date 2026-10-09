package com.solgases.infrastructure.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/** Validation of the User, Role and Permission request DTOs. */
class UserRoleRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    private static <T> Set<String> invalidFields(T request) {
        return validator.validate(request).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    // UserRequest

    @Test
    void validUserRequestWithOrWithoutRolesHasNoViolations() {
        assertThat(invalidFields(new UserRequest("jdoe", "John Doe", Set.of(1L, 2L)))).isEmpty();
        assertThat(invalidFields(new UserRequest("jdoe", "John Doe", Set.of()))).isEmpty();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void blankUsernameAndDisplayNameAreRejected(String value) {
        assertThat(invalidFields(new UserRequest(value, value, Set.of()))).containsExactlyInAnyOrder("username", "displayName");
    }

    @Test
    void userFieldsLongerThanMaxLengthAreRejected() {
        assertThat(invalidFields(new UserRequest("a".repeat(50), "a".repeat(100), Set.of()))).isEmpty();
        assertThat(invalidFields(new UserRequest("a".repeat(51), "a".repeat(101), Set.of())))
                .containsExactlyInAnyOrder("username", "displayName");
    }

    @Test
    void missingRoleIdsOrNullElementIsRejected() {
        assertThat(invalidFields(new UserRequest("jdoe", "John Doe", null))).containsExactly("roleIds");
        Set<Long> withNull = new HashSet<>();
        withNull.add(null);
        assertThat(invalidFields(new UserRequest("jdoe", "John Doe", withNull)))
                .singleElement().asString().startsWith("roleIds");
    }

    // Role requests

    @Test
    void validRoleRequestsHaveNoViolations() {
        assertThat(invalidFields(new RoleCreateRequest("R_KEY", "Role", Set.of()))).isEmpty();
        assertThat(invalidFields(new RoleUpdateRequest("Role", Set.of(1L)))).isEmpty();
    }

    @Test
    void invalidRoleCreateRequestIsRejected() {
        assertThat(invalidFields(new RoleCreateRequest(" ", "", null)))
                .containsExactlyInAnyOrder("key", "name", "permissionIds");
        assertThat(invalidFields(new RoleCreateRequest("k".repeat(51), "n".repeat(101), Set.of())))
                .containsExactlyInAnyOrder("key", "name");
    }

    @Test
    void invalidRoleUpdateRequestIsRejected() {
        assertThat(invalidFields(new RoleUpdateRequest(null, null))).containsExactlyInAnyOrder("name", "permissionIds");
    }

    // Permission requests

    @Test
    void validPermissionRequestsHaveNoViolations() {
        assertThat(invalidFields(new PermissionCreateRequest("P_KEY", "p.code"))).isEmpty();
        assertThat(invalidFields(new PermissionUpdateRequest("p.code"))).isEmpty();
    }

    @Test
    void invalidPermissionRequestsAreRejected() {
        assertThat(invalidFields(new PermissionCreateRequest("", null))).containsExactlyInAnyOrder("key", "code");
        assertThat(invalidFields(new PermissionCreateRequest("k".repeat(51), "c".repeat(101))))
                .containsExactlyInAnyOrder("key", "code");
        assertThat(invalidFields(new PermissionUpdateRequest(" "))).containsExactly("code");
    }
}
