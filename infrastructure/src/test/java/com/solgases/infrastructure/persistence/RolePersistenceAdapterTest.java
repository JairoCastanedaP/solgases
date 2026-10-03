package com.solgases.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.solgases.application.exception.ConflictException;
import com.solgases.application.exception.DuplicateRoleKeyException;
import com.solgases.application.exception.DuplicateRoleNameException;
import com.solgases.domain.model.Permission;
import com.solgases.domain.model.Role;
import com.solgases.infrastructure.persistence.entity.PermissionJpaEntity;
import com.solgases.infrastructure.persistence.entity.RoleJpaEntity;
import com.solgases.infrastructure.persistence.repository.PermissionJpaRepository;
import com.solgases.infrastructure.persistence.repository.RoleJpaRepository;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RolePersistenceAdapterTest {

    @Mock
    private RoleJpaRepository roleRepository;

    @Mock
    private PermissionJpaRepository permissionRepository;

    @InjectMocks
    private RolePersistenceAdapter adapter;

    private static <T> T withId(T entity, long id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }

    private static DataIntegrityViolationException mysqlDuplicate(String constraint) {
        return new DataIntegrityViolationException("could not execute statement",
                new SQLIntegrityConstraintViolationException("Duplicate entry 'x' for key 'tbl_role." + constraint + "'"));
    }

    @Test
    void saveNewAssignsRequestedPermissions() {
        PermissionJpaEntity permission = withId(new PermissionJpaEntity("P", "p"), 10L);
        when(permissionRepository.findAllById(Set.of(10L))).thenReturn(List.of(permission));
        when(roleRepository.saveAndFlush(any(RoleJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Role saved = adapter.saveNew(Role.newRole("R_KEY", "Role",
                Set.of(new Permission(10L, "P", "p", null, null))));

        assertThat(saved.key()).isEqualTo("R_KEY");
        assertThat(saved.permissionIds()).containsExactly(10L);
    }

    @Test
    void saveNewWithoutPermissionsDoesNotQueryThem() {
        when(roleRepository.saveAndFlush(any(RoleJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(adapter.saveNew(Role.newRole("R_KEY", "Role", Set.of())).permissions()).isEmpty();
        verify(permissionRepository, never()).findAllById(any());
    }

    @Test
    void saveNewTranslatesEachUniqueViolationToItsConflict() {
        when(roleRepository.saveAndFlush(any(RoleJpaEntity.class)))
                .thenThrow(mysqlDuplicate(RoleJpaEntity.UK_KEY))
                .thenThrow(mysqlDuplicate(RoleJpaEntity.UK_NAME))
                .thenThrow(new DataIntegrityViolationException("other"));
        Role role = Role.newRole("R_KEY", "Role", Set.of());

        assertThatThrownBy(() -> adapter.saveNew(role)).isInstanceOf(DuplicateRoleKeyException.class);
        assertThatThrownBy(() -> adapter.saveNew(role)).isInstanceOf(DuplicateRoleNameException.class);
        assertThatThrownBy(() -> adapter.saveNew(role)).isExactlyInstanceOf(ConflictException.class);
    }

    @Test
    void saveChangesRenamesAndReplacesPermissionsButKeepsKey() {
        RoleJpaEntity entity = withId(new RoleJpaEntity("R_KEY", "Old"), 3L);
        entity.getPermissions().add(withId(new PermissionJpaEntity("P", "p"), 10L));
        when(roleRepository.findWithPermissionsById(3L)).thenReturn(Optional.of(entity));
        when(roleRepository.saveAndFlush(entity)).thenReturn(entity);

        Role saved = adapter.saveChanges(new Role(3L, "IGNORED", "New", Set.of(), null, null));

        assertThat(saved.key()).isEqualTo("R_KEY");
        assertThat(saved.name()).isEqualTo("New");
        assertThat(saved.permissions()).isEmpty();
    }

    @Test
    void findAllByIdsWithEmptyIdsDoesNotQuery() {
        assertThat(adapter.findAllByIds(Set.of())).isEmpty();
        verify(roleRepository, never()).findAllWithPermissionsByIdIn(any());
    }
}
