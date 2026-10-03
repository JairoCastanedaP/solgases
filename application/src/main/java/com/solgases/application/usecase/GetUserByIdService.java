package com.solgases.application.usecase;

import com.solgases.application.dto.UserResult;
import com.solgases.application.exception.UserNotFoundException;
import com.solgases.application.port.in.GetUserByIdUseCase;
import com.solgases.application.port.out.UserPersistencePort;
import org.springframework.transaction.annotation.Transactional;

public class GetUserByIdService implements GetUserByIdUseCase {

    private final UserPersistencePort userPersistencePort;

    public GetUserByIdService(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResult execute(Long id) {
        return UserResult.from(userPersistencePort.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }
}
