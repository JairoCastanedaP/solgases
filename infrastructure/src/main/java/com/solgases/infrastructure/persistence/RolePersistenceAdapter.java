package com.solgases.infrastructure.persistence;

import com.solgases.application.exception.ConflictException;
import com.solgases.application.exception.DuplicateRoleKeyException;
import com.solgases.application.exception.DuplicateRoleNameException;
import com.solgases.application.exception.RoleNotFoundException;
import com.solgases.application.port.out.RolePersistencePort;
import com.solgases.domain.model.Role;
import com.solgases.infrastructure.persistence.entity.PermissionJpaEntity;
import com.solgases.infrastructure.persistence.entity.RoleJpaEntity;
import com.solgases.infrastructure.persistence.mapper.RolePersistenceMapper;
import com.solgases.infrastructure.persistence.repository.PermissionJpaRepository;
import com.solgases.infrastructure.persistence.repository.RoleJpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
public class RolePersistenceAdapter implements RolePersistencePort {

    private final RoleJpaRepository roleRepository;
    private final PermissionJpaRepository permissionRepository;

    public RolePersistenceAdapter(RoleJpaRepository roleRepository, PermissionJpaRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleRepository.findWithPermissionsById(id).map(RolePersistenceMapper::toDomain);
    }

    @Override
    public Optional<Role> findByKey(String key) {
        return roleRepository.findByKey(key).flatMap(role -> findById(role.getId()));
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAllWithPermissions().stream().map(RolePersistenceMapper::toDomain).toList();
    }

    @Override
    public List<Role> findAllByIds(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return roleRepository.findAllWithPermissionsByIdIn(ids).stream().map(RolePersistenceMapper::toDomain).toList();
    }

    @Override
    public boolean existsByKey(String key) {
        return roleRepository.existsByKey(key);
    }

    @Override
    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, Long id) {
        return roleRepository.existsByNameAndIdNot(name, id);
    }

    @Override
    public Role saveNew(Role role) {
        RoleJpaEntity entity = new RoleJpaEntity(role.key(), role.name());
        entity.replacePermissions(permissionsFor(role.permissionIds()));
        return saveOrConflict(entity);
    }

    @Override
    public Role saveChanges(Role role) {
        RoleJpaEntity entity = roleRepository.findWithPermissionsById(role.id())
                .orElseThrow(() -> new RoleNotFoundException(role.id()));
        entity.rename(role.name());
        entity.replacePermissions(permissionsFor(role.permissionIds()));
        return saveOrConflict(entity);
    }

    private List<PermissionJpaEntity> permissionsFor(Collection<Long> permissionIds) {
        return permissionIds.isEmpty() ? List.of() : permissionRepository.findAllById(permissionIds);
    }

    private Role saveOrConflict(RoleJpaEntity entity) {
        try {
            return RolePersistenceMapper.toDomain(roleRepository.saveAndFlush(entity));
        } catch (DataIntegrityViolationException ex) {
            throw toConflict(ex, entity);
        }
    }

    private static ConflictException toConflict(DataIntegrityViolationException ex, RoleJpaEntity entity) {
        String cause = String.valueOf(ex.getMostSpecificCause().getMessage()).toLowerCase();
        if (cause.contains(RoleJpaEntity.UK_KEY)) {
            return new DuplicateRoleKeyException(entity.getKey());
        }
        if (cause.contains(RoleJpaEntity.UK_NAME)) {
            return new DuplicateRoleNameException(entity.getName());
        }
        return new ConflictException("The role conflicts with existing data");
    }
}
