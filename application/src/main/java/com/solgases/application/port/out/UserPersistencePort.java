package com.solgases.application.port.out;

import com.solgases.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserPersistencePort {

    /** Returns the user with its roles and their permissions. */
    Optional<User> findById(Long id);

    /** Returns all users with their roles and permissions, using a fixed number of queries. */
    List<User> findAll();

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);

    User saveNew(User user);

    User saveChanges(User user);
}
