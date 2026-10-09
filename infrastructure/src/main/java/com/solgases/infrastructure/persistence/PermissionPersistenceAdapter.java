package com.solgases.infrastructure.persistence;

import com.solgases.application.exception.ConflictException;
import com.solgases.application.exception.DuplicatePermissionCodeException;
import com.solgases.application.exception.DuplicatePermissionKeyException;
import com.solgases.application.exception.PermissionNotFoundException;
import com.solgases.application.port.out.PermissionPersistencePort;
import com.solgases.domain.model.Permission;
import com.solgases.infrastructure.persistence.entity.PermissionJpaEntity;
import com.solgases.infrastructure.persistence.mapper.PermissionPersistenceMapper;
import com.solgases.infrastructure.persistence.repository.PermissionJpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
public class PermissionPersistenceAdapter implements PermissionPersistencePort {

    private final PermissionJpaRepository permissionRepository;

    public PermissionPersistenceAdapter(PermissionJpaRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return permissionRepository.findById(id).map(PermissionPersistenceMapper::toDomain);
    }

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAllByOrderByIdAsc().stream().map(PermissionPersistenceMapper::toDomain).toList();
    }

    @Override
    public List<Permission> findAllByIds(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return permissionRepository.findAllById(ids).stream().map(PermissionPersistenceMapper::toDomain).toList();
    }

    @Override
    public List<Permission> findAllByKeys(Collection<String> keys) {
        if (keys.isEmpty()) {
            return List.of();
        }
        return permissionRepository.findAllByKeyIn(keys).stream().map(PermissionPersistenceMapper::toDomain).toList();
    }

    @Override
    public boolean existsByKey(String key) {
        return permissionRepository.existsByKey(key);
    }

    @Override
    public boolean existsByCode(String code) {
        return permissionRepository.existsByCode(code);
    }

    @Override
    public boolean existsByCodeAndIdNot(String code, Long id) {
        return permissionRepository.existsByCodeAndIdNot(code, id);
    }

    @Override
    public Permission saveNew(Permission permission) {
        return saveOrConflict(new PermissionJpaEntity(permission.key(), permission.code()));
    }

    @Override
    public Permission saveChanges(Permission permission) {
        PermissionJpaEntity entity = permissionRepository.findById(permission.id())
                .orElseThrow(() -> new PermissionNotFoundException(permission.id()));
        entity.changeCode(permission.code());
        return saveOrConflict(entity);
    }

    private Permission saveOrConflict(PermissionJpaEntity entity) {
        try {
            return PermissionPersistenceMapper.toDomain(permissionRepository.saveAndFlush(entity));
        } catch (DataIntegrityViolationException ex) {
            throw toConflict(ex, entity);
        }
    }

    private static ConflictException toConflict(DataIntegrityViolationException ex, PermissionJpaEntity entity) {
        String cause = String.valueOf(ex.getMostSpecificCause().getMessage()).toLowerCase();
        if (cause.contains(PermissionJpaEntity.UK_KEY)) {
            return new DuplicatePermissionKeyException(entity.getKey());
        }
        if (cause.contains(PermissionJpaEntity.UK_CODE)) {
            return new DuplicatePermissionCodeException(entity.getCode());
        }
        return new ConflictException("The permission conflicts with existing data");
    }
}
