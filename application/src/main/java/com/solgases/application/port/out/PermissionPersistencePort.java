package com.solgases.application.port.out;

import com.solgases.domain.model.Permission;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PermissionPersistencePort {

    Optional<Permission> findById(Long id);

    List<Permission> findAll();

    /** Returns the permissions that exist among the given ids. */
    List<Permission> findAllByIds(Collection<Long> ids);

    /** Returns the permissions that exist among the given internal keys. */
    List<Permission> findAllByKeys(Collection<String> keys);

    boolean existsByKey(String key);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    Permission saveNew(Permission permission);

    Permission saveChanges(Permission permission);
}
