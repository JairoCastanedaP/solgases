package com.solgases.infrastructure.persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/** updatedAt must change when the roles of a user or the permissions of a role really change, and only then. */
class RelationshipTimestampTest {

    private static final Instant OLD = Instant.parse("2026-01-01T00:00:00Z");

    private static <T> T withId(T entity, long id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }

    private static UserJpaEntity persistedUser(RoleJpaEntity... roles) {
        UserJpaEntity user = withId(new UserJpaEntity("jdoe", "John Doe"), 1L);
        user.getRoles().addAll(List.of(roles));
        ReflectionTestUtils.setField(user, "updatedAt", OLD);
        return user;
    }

    private static RoleJpaEntity persistedRole(PermissionJpaEntity... permissions) {
        RoleJpaEntity role = withId(new RoleJpaEntity("R_KEY", "Role"), 1L);
        role.getPermissions().addAll(List.of(permissions));
        ReflectionTestUtils.setField(role, "updatedAt", OLD);
        return role;
    }

    @Test
    void replacingRolesWithTheSameIdsKeepsUpdatedAt() {
        RoleJpaEntity role = withId(new RoleJpaEntity("A", "A"), 10L);
        UserJpaEntity user = persistedUser(role);

        // A different instance with the same id counts as the same role
        user.replaceRoles(List.of(withId(new RoleJpaEntity("A", "A"), 10L)));

        assertThat(user.getUpdatedAt()).isEqualTo(OLD);
        assertThat(user.getRoles()).containsExactly(role);
    }

    @Test
    void replacingRolesWithDifferentIdsUpdatesRolesAndUpdatedAt() {
        UserJpaEntity user = persistedUser(withId(new RoleJpaEntity("A", "A"), 10L));
        RoleJpaEntity other = withId(new RoleJpaEntity("B", "B"), 11L);

        user.replaceRoles(List.of(other));

        assertThat(user.getRoles()).containsExactly(other);
        assertThat(user.getUpdatedAt()).isAfter(OLD);
    }

    @Test
    void removingAllRolesUpdatesUpdatedAt() {
        UserJpaEntity user = persistedUser(withId(new RoleJpaEntity("A", "A"), 10L));

        user.replaceRoles(List.of());

        assertThat(user.getRoles()).isEmpty();
        assertThat(user.getUpdatedAt()).isAfter(OLD);
    }

    @Test
    void replacingPermissionsWithTheSameIdsKeepsUpdatedAt() {
        RoleJpaEntity role = persistedRole(withId(new PermissionJpaEntity("P", "p"), 20L));

        role.replacePermissions(List.of(withId(new PermissionJpaEntity("P", "p"), 20L)));

        assertThat(role.getUpdatedAt()).isEqualTo(OLD);
    }

    @Test
    void replacingPermissionsWithDifferentIdsUpdatesUpdatedAt() {
        RoleJpaEntity role = persistedRole();
        PermissionJpaEntity permission = withId(new PermissionJpaEntity("P", "p"), 20L);

        role.replacePermissions(List.of(permission));

        assertThat(role.getPermissions()).containsExactly(permission);
        assertThat(role.getUpdatedAt()).isAfter(OLD);
    }

    @Test
    void newUserIsActive() {
        assertThat(new UserJpaEntity("jdoe", "John Doe").isActive()).isTrue();
    }
}
