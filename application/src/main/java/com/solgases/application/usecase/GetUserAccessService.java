package com.solgases.application.usecase;

import com.solgases.application.dto.UserAccess;
import com.solgases.application.port.in.GetUserAccessUseCase;
import com.solgases.application.port.out.UserPersistencePort;
import com.solgases.domain.model.Permission;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resolves the current state and permissions of a user from persistence, so that deactivations and
 * permission changes apply to the next request. Permissions are identified by their stable internal key,
 * never by editable role names or permission codes.
 */
public class GetUserAccessService implements GetUserAccessUseCase {

    private final UserPersistencePort userPersistencePort;

    public GetUserAccessService(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserAccess> execute(Long userId) {
        return userPersistencePort.findById(userId).map(user -> new UserAccess(user.id(), user.active(),
                user.roles().stream()
                        .flatMap(role -> role.permissions().stream())
                        .map(Permission::key)
                        .collect(Collectors.toSet())));
    }
}
