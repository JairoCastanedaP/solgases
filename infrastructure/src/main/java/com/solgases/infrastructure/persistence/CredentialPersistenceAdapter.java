package com.solgases.infrastructure.persistence;

import com.solgases.application.dto.StoredCredential;
import com.solgases.application.port.out.CredentialPersistencePort;
import com.solgases.infrastructure.persistence.entity.UserCredentialJpaEntity;
import com.solgases.infrastructure.persistence.repository.UserCredentialJpaRepository;
import com.solgases.infrastructure.persistence.repository.UserJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CredentialPersistenceAdapter implements CredentialPersistencePort {

    private final UserCredentialJpaRepository credentialRepository;
    private final UserJpaRepository userRepository;

    public CredentialPersistenceAdapter(UserCredentialJpaRepository credentialRepository,
            UserJpaRepository userRepository) {
        this.credentialRepository = credentialRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<StoredCredential> findByUsername(String username) {
        return credentialRepository.findByUsername(username)
                .map(credential -> new StoredCredential(credential.getUserId(), credential.getPasswordHash()));
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return credentialRepository.existsById(userId);
    }

    @Override
    public void saveNew(Long userId, String passwordHash) {
        credentialRepository.saveAndFlush(
                new UserCredentialJpaEntity(userRepository.getReferenceById(userId), passwordHash));
    }
}
