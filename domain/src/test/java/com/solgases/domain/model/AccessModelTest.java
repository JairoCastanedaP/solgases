package com.solgases.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AccessModelTest {

    private static final Instant CREATED = Instant.parse("2026-01-01T00:00:00Z");
    private static final Instant UPDATED = Instant.parse("2026-01-02T00:00:00Z");

    private final Permission permission = new Permission(10L, "P_KEY", "p.code", CREATED, UPDATED);
    private final Role role = new Role(1L, "R_KEY", "Role", Set.of(permission), CREATED, UPDATED);

    @Test
    void newUserIsActiveAndHasNoIdOrTimestamps() {
        User user = User.newUser("jdoe", "John Doe", Set.of(role));

        assertThat(user.active()).isTrue();
        assertThat(user.id()).isNull();
        assertThat(user.createdAt()).isNull();
        assertThat(user.updatedAt()).isNull();
        assertThat(user.roleIds()).containsExactly(1L);
    }

    @Test
    void userWithDetailsKeepsIdActiveFlagAndTimestamps() {
        User inactive = new User(5L, "jdoe", "John Doe", false, Set.of(role), CREATED, UPDATED);

        User changed = inactive.withDetails("jdoe2", "Johnny", Set.of());

        assertThat(changed).isEqualTo(new User(5L, "jdoe2", "Johnny", false, Set.of(), CREATED, UPDATED));
    }

    @Test
    void userActivationChangesOnlyTheActiveFlag() {
        User user = new User(5L, "jdoe", "John Doe", true, Set.of(role), CREATED, UPDATED);

        User deactivated = user.deactivate();

        assertThat(deactivated.active()).isFalse();
        assertThat(deactivated.activate()).isEqualTo(user);
    }

    @Test
    void userRolesAreAnImmutableCopyAndNullMeansNoRoles() {
        Set<Role> roles = new HashSet<>(Set.of(role));
        User user = new User(5L, "jdoe", "John Doe", true, roles, CREATED, UPDATED);
        roles.clear();

        assertThat(user.roles()).containsExactly(role);
        assertThatThrownBy(() -> user.roles().clear()).isInstanceOf(UnsupportedOperationException.class);
        assertThat(new User(5L, "jdoe", "John Doe", true, null, CREATED, UPDATED).roles()).isEmpty();
    }

    @Test
    void roleWithDetailsNeverChangesTheKey() {
        Role changed = role.withDetails("Renamed", Set.of());

        assertThat(changed).isEqualTo(new Role(1L, "R_KEY", "Renamed", Set.of(), CREATED, UPDATED));
        assertThat(role.permissionIds()).containsExactly(10L);
    }

    @Test
    void newRoleAllowsNoPermissionsAndNullMeansNoPermissions() {
        assertThat(Role.newRole("R_KEY", "Role", Set.of()).permissions()).isEmpty();
        assertThat(Role.newRole("R_KEY", "Role", null).permissions()).isEmpty();
    }

    @Test
    void permissionWithCodeNeverChangesTheKey() {
        assertThat(permission.withCode("new.code"))
                .isEqualTo(new Permission(10L, "P_KEY", "new.code", CREATED, UPDATED));
        assertThat(Permission.newPermission("P_KEY", "p.code").id()).isNull();
    }
}
