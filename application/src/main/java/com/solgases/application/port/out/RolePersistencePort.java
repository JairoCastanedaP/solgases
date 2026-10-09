package com.solgases.application.port.out;

import com.solgases.domain.model.Role;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RolePersistencePort {

    /** Returns the role with its permissions. */
    Optional<Role> findById(Long id);

    /** Returns the role with the given internal key, with its permissions. */
    Optional<Role> findByKey(String key);

    /** Returns all roles with their permissions. */
    List<Role> findAll();

    /** Returns the roles that exist among the given ids, with their permissions. */
    List<Role> findAllByIds(Collection<Long> ids);

    boolean existsByKey(String key);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    Role saveNew(Role role);

    Role saveChanges(Role role);
}
