package com.solgases.infrastructure.persistence;

import com.solgases.application.exception.DuplicateUsernameException;
import com.solgases.application.exception.UserNotFoundException;
import com.solgases.application.port.out.UserPersistencePort;
import com.solgases.domain.model.User;
import com.solgases.infrastructure.persistence.entity.RoleJpaEntity;
import com.solgases.infrastructure.persistence.entity.UserJpaEntity;
import com.solgases.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.solgases.infrastructure.persistence.repository.RoleJpaRepository;
import com.solgases.infrastructure.persistence.repository.UserJpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

/**
 * Users are read with their roles and permissions in a fixed number of queries: one for users joined with
 * their roles, and one for those roles joined with their permissions. Both must run in the same transaction,
 * so that the persistence context completes the roles of the first query with the permissions of the second.
 */
@Repository
public class UserPersistenceAdapter implements UserPersistencePort {

    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;

    public UserPersistenceAdapter(UserJpaRepository userRepository, RoleJpaRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findWithRolesById(id).map(user -> {
            loadRolePermissions(List.of(user));
            return UserPersistenceMapper.toDomain(user);
        });
    }

    @Override
    public List<User> findAll() {
        List<UserJpaEntity> users = userRepository.findAllWithRoles();
        loadRolePermissions(users);
        return users.stream().map(UserPersistenceMapper::toDomain).toList();
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByUsernameAndIdNot(String username, Long id) {
        return userRepository.existsByUsernameAndIdNot(username, id);
    }

    @Override
    public User saveNew(User user) {
        UserJpaEntity entity = new UserJpaEntity(user.username(), user.displayName());
        entity.replaceRoles(rolesFor(user.roleIds()));
        return saveOrConflict(entity);
    }

    @Override
    public User saveChanges(User user) {
        UserJpaEntity entity = userRepository.findWithRolesById(user.id())
                .orElseThrow(() -> new UserNotFoundException(user.id()));
        entity.replaceDetails(user.username(), user.displayName());
        entity.replaceRoles(rolesFor(user.roleIds()));
        if (user.active()) {
            entity.activate();
        } else {
            entity.deactivate();
        }
        return saveOrConflict(entity);
    }

    private void loadRolePermissions(Collection<UserJpaEntity> users) {
        Set<Long> roleIds = users.stream()
                .flatMap(user -> user.getRoles().stream())
                .map(RoleJpaEntity::getId)
                .collect(Collectors.toSet());
        if (!roleIds.isEmpty()) {
            // The result is not needed: the managed roles already referenced by the users get their permissions
            roleRepository.findAllWithPermissionsByIdIn(roleIds);
        }
    }

    private List<RoleJpaEntity> rolesFor(Collection<Long> roleIds) {
        return roleIds.isEmpty() ? List.of() : roleRepository.findAllWithPermissionsByIdIn(roleIds);
    }

    private User saveOrConflict(UserJpaEntity entity) {
        try {
            return UserPersistenceMapper.toDomain(userRepository.saveAndFlush(entity));
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateUsernameException(entity.getUsername());
        }
    }
}
