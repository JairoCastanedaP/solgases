package com.solgases.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.solgases.application.exception.DuplicatePermissionCodeException;
import com.solgases.application.exception.DuplicatePermissionKeyException;
import com.solgases.domain.model.Permission;
import com.solgases.infrastructure.persistence.entity.PermissionJpaEntity;
import com.solgases.infrastructure.persistence.repository.PermissionJpaRepository;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PermissionPersistenceAdapterTest {

    @Mock
    private PermissionJpaRepository permissionRepository;

    @InjectMocks
    private PermissionPersistenceAdapter adapter;

    private static DataIntegrityViolationException mysqlDuplicate(String constraint) {
        return new DataIntegrityViolationException("could not execute statement",
                new SQLIntegrityConstraintViolationException(
                        "Duplicate entry 'x' for key 'tbl_permission." + constraint + "'"));
    }

    @Test
    void saveNewTranslatesEachUniqueViolationToItsConflict() {
        when(permissionRepository.saveAndFlush(any(PermissionJpaEntity.class)))
                .thenThrow(mysqlDuplicate(PermissionJpaEntity.UK_KEY))
                .thenThrow(mysqlDuplicate(PermissionJpaEntity.UK_CODE));
        Permission permission = Permission.newPermission("P_KEY", "p.code");

        assertThatThrownBy(() -> adapter.saveNew(permission))
                .isInstanceOf(DuplicatePermissionKeyException.class)
                .hasMessage("A permission with the key 'P_KEY' already exists");
        assertThatThrownBy(() -> adapter.saveNew(permission))
                .isInstanceOf(DuplicatePermissionCodeException.class)
                .hasMessage("A permission with the code 'p.code' already exists");
    }

    @Test
    void saveChangesUpdatesCodeButNeverTheKey() {
        PermissionJpaEntity entity = new PermissionJpaEntity("P_KEY", "old");
        ReflectionTestUtils.setField(entity, "id", 1L);
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(permissionRepository.saveAndFlush(entity)).thenReturn(entity);

        Permission saved = adapter.saveChanges(new Permission(1L, "IGNORED", "new", null, null));

        assertThat(saved.key()).isEqualTo("P_KEY");
        assertThat(saved.code()).isEqualTo("new");
    }
}
