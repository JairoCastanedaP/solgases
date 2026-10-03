package com.solgases.infrastructure.config;

import com.solgases.application.port.out.RolePersistencePort;
import com.solgases.application.port.out.UserPersistencePort;
import com.solgases.application.usecase.ActivateUserService;
import com.solgases.application.usecase.CreateUserService;
import com.solgases.application.usecase.DeactivateUserService;
import com.solgases.application.usecase.GetUserByIdService;
import com.solgases.application.usecase.ListUsersService;
import com.solgases.application.usecase.UpdateUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserUseCaseConfiguration {

    @Bean
    CreateUserService createUserService(UserPersistencePort userPersistencePort,
            RolePersistencePort rolePersistencePort) {
        return new CreateUserService(userPersistencePort, rolePersistencePort);
    }

    @Bean
    ListUsersService listUsersService(UserPersistencePort userPersistencePort) {
        return new ListUsersService(userPersistencePort);
    }

    @Bean
    GetUserByIdService getUserByIdService(UserPersistencePort userPersistencePort) {
        return new GetUserByIdService(userPersistencePort);
    }

    @Bean
    UpdateUserService updateUserService(UserPersistencePort userPersistencePort,
            RolePersistencePort rolePersistencePort) {
        return new UpdateUserService(userPersistencePort, rolePersistencePort);
    }

    @Bean
    ActivateUserService activateUserService(UserPersistencePort userPersistencePort) {
        return new ActivateUserService(userPersistencePort);
    }

    @Bean
    DeactivateUserService deactivateUserService(UserPersistencePort userPersistencePort) {
        return new DeactivateUserService(userPersistencePort);
    }
}
