package com.solgases.application.port.out;

import com.solgases.application.dto.StoredCredential;
import java.util.Optional;

public interface CredentialPersistencePort {

    /** Stored credential of the user with the given username, if the user has one. */
    Optional<StoredCredential> findByUsername(String username);

    boolean existsByUserId(Long userId);

    /** Stores the first credential of an existing user. Only the password hash is stored. */
    void saveNew(Long userId, String passwordHash);
}
