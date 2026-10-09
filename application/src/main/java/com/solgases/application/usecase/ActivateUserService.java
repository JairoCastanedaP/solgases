package com.solgases.application.usecase;

import com.solgases.application.dto.UserResult;
import com.solgases.application.exception.UserNotFoundException;
import com.solgases.application.port.in.ActivateUserUseCase;
import com.solgases.application.port.out.UserPersistencePort;
import com.solgases.domain.model.User;
import org.springframework.transaction.annotation.Transactional;

public class ActivateUserService implements ActivateUserUseCase {

    private final UserPersistencePort userPersistencePort;

    public ActivateUserService(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    @Transactional
    public UserResult execute(Long id) {
        User user = userPersistencePort.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return UserResult.from(userPersistencePort.saveChanges(user.activate()));
    }
}
