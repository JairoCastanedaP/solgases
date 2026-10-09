package com.solgases.application.port.out;

import com.solgases.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserPersistencePort {

    /** Returns the user with its roles and their permissions. */
    Optional<User> findById(Long id);

    /** Returns the user with the given username, with its roles and their permissions. */
    Optional<User> findByUsername(String username);

    /** Returns all users with their roles and permissions, using a fixed number of queries. */
    List<User> findAll();

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);

    /** Whether an active user has the role with the given internal key. */
    boolean existsActiveUserWithRole(String roleKey);

    User saveNew(User user);

    User saveChanges(User user);
}
