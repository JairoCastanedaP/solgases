package com.solgases.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.solgases.application.exception.DuplicateUsernameException;
import com.solgases.domain.model.Role;
import com.solgases.domain.model.User;
import com.solgases.infrastructure.persistence.entity.PermissionJpaEntity;
import com.solgases.infrastructure.persistence.entity.RoleJpaEntity;
import com.solgases.infrastructure.persistence.entity.UserJpaEntity;
import com.solgases.infrastructure.persistence.repository.RoleJpaRepository;
import com.solgases.infrastructure.persistence.repository.UserJpaRepository;
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
class UserPersistenceAdapterTest {

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private RoleJpaRepository roleRepository;

    @InjectMocks
    private UserPersistenceAdapter adapter;

    private static <T> T withId(T entity, long id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }

    private static UserJpaEntity user(long id, String username, RoleJpaEntity... roles) {
        UserJpaEntity user = withId(new UserJpaEntity(username, username), id);
        user.getRoles().addAll(List.of(roles));
        return user;
    }

    @Test
    void findAllUsesOneQueryForUsersAndOneForAllTheirRolesPermissions() {
        RoleJpaEntity roleA = withId(new RoleJpaEntity("A", "A"), 1L);
        roleA.getPermissions().add(withId(new PermissionJpaEntity("P", "p"), 10L));
        RoleJpaEntity roleB = withId(new RoleJpaEntity("B", "B"), 2L);
        when(userRepository.findAllWithRoles()).thenReturn(List.of(
                user(1L, "u1", roleA), user(2L, "u2", roleA, roleB), user(3L, "u3")));

        List<User> users = adapter.findAll();

        assertThat(users).extracting(User::username).containsExactly("u1", "u2", "u3");
        assertThat(users.get(1).roleIds()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(users.getFirst().roles()).singleElement()
                .satisfies(role -> assertThat(role.permissionIds()).containsExactly(10L));
        // A fixed number of queries, regardless of the number of users and roles
        verify(userRepository, times(1)).findAllWithRoles();
        verify(roleRepository, times(1)).findAllWithPermissionsByIdIn(Set.of(1L, 2L));
    }

    @Test
    void findAllWithoutRolesSkipsThePermissionQuery() {
        when(userRepository.findAllWithRoles()).thenReturn(List.of(user(1L, "u1")));

        assertThat(adapter.findAll()).hasSize(1);
        verify(roleRepository, never()).findAllWithPermissionsByIdIn(any());
    }

    @Test
    void findByIdLoadsRolesAndTheirPermissions() {
        RoleJpaEntity role = withId(new RoleJpaEntity("A", "A"), 1L);
        when(userRepository.findWithRolesById(5L)).thenReturn(Optional.of(user(5L, "jdoe", role)));

        assertThat(adapter.findById(5L)).get().extracting(User::roleIds).isEqualTo(Set.of(1L));
        verify(roleRepository).findAllWithPermissionsByIdIn(Set.of(1L));
    }

    @Test
    void saveNewAssignsRequestedRolesAndKeepsUserActive() {
        RoleJpaEntity role = withId(new RoleJpaEntity("A", "A"), 1L);
        when(roleRepository.findAllWithPermissionsByIdIn(Set.of(1L))).thenReturn(List.of(role));
        when(userRepository.saveAndFlush(any(UserJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Role domainRole = new Role(1L, "A", "A", Set.of(), null, null);

        User saved = adapter.saveNew(User.newUser("jdoe", "John Doe", Set.of(domainRole)));

        assertThat(saved.active()).isTrue();
        assertThat(saved.roleIds()).containsExactly(1L);
    }

    @Test
    void saveNewTranslatesDatabaseUniqueViolationToDuplicateUsername() {
        when(userRepository.saveAndFlush(any(UserJpaEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThatThrownBy(() -> adapter.saveNew(User.newUser("jdoe", "John Doe", Set.of())))
                .isInstanceOf(DuplicateUsernameException.class)
                .hasMessage("A user with the username 'jdoe' already exists");
    }

    @Test
    void saveChangesAppliesDetailsRolesAndActiveFlag() {
        UserJpaEntity entity = user(5L, "jdoe", withId(new RoleJpaEntity("A", "A"), 1L));
        when(userRepository.findWithRolesById(5L)).thenReturn(Optional.of(entity));
        when(userRepository.saveAndFlush(entity)).thenReturn(entity);

        User saved = adapter.saveChanges(new User(5L, "jdoe2", "Johnny", false, Set.of(), null, null));

        assertThat(saved.username()).isEqualTo("jdoe2");
        assertThat(saved.active()).isFalse();
        assertThat(saved.roles()).isEmpty();
        verify(roleRepository, never()).findAllWithPermissionsByIdIn(any());
    }
}
