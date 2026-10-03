package com.solgases.application.usecase;

import com.solgases.application.dto.UserResult;
import com.solgases.application.port.in.ListUsersUseCase;
import com.solgases.application.port.out.UserPersistencePort;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public class ListUsersService implements ListUsersUseCase {

    private final UserPersistencePort userPersistencePort;

    public ListUsersService(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResult> execute() {
        return userPersistencePort.findAll().stream().map(UserResult::from).toList();
    }
}
