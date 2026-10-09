package com.solgases.infrastructure.config;

import com.solgases.application.port.in.AuthenticateUserUseCase;
import com.solgases.application.port.in.GetUserAccessUseCase;
import com.solgases.application.port.out.CredentialPersistencePort;
import com.solgases.application.port.out.PasswordHashVerifier;
import com.solgases.application.port.out.UserPersistencePort;
import com.solgases.application.usecase.AuthenticateUserService;
import com.solgases.application.usecase.GetUserAccessService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthenticationUseCaseConfiguration {

    @Bean
    AuthenticateUserUseCase authenticateUserUseCase(CredentialPersistencePort credentialPersistencePort,
            UserPersistencePort userPersistencePort, PasswordHashVerifier passwordHashVerifier) {
        return new AuthenticateUserService(credentialPersistencePort, userPersistencePort, passwordHashVerifier);
    }

    @Bean
    GetUserAccessUseCase getUserAccessUseCase(UserPersistencePort userPersistencePort) {
        return new GetUserAccessService(userPersistencePort);
    }
}
