package com.solgases.infrastructure.config;

import com.solgases.application.port.out.CredentialPersistencePort;
import com.solgases.application.port.out.PasswordHashEncoder;
import com.solgases.application.port.out.RolePersistencePort;
import com.solgases.application.port.out.UserPersistencePort;
import com.solgases.application.usecase.CreateFirstAdministratorService;
import com.solgases.application.usecase.ProvisionUserCredentialService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CredentialUseCaseConfiguration {

    @Bean
    CreateFirstAdministratorService createFirstAdministratorService(UserPersistencePort userPersistencePort,
            RolePersistencePort rolePersistencePort, CredentialPersistencePort credentialPersistencePort,
            PasswordHashEncoder passwordHashEncoder) {
        return new CreateFirstAdministratorService(userPersistencePort, rolePersistencePort,
                credentialPersistencePort, passwordHashEncoder, InitialRolePermissionCatalog.ADMIN);
    }

    @Bean
    ProvisionUserCredentialService provisionUserCredentialService(UserPersistencePort userPersistencePort,
            CredentialPersistencePort credentialPersistencePort, PasswordHashEncoder passwordHashEncoder) {
        return new ProvisionUserCredentialService(userPersistencePort, credentialPersistencePort,
                passwordHashEncoder);
    }
}
