package com.solgases.infrastructure.cli;

import com.solgases.application.port.in.CreateFirstAdministratorUseCase;
import com.solgases.application.port.in.ProvisionUserCredentialUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/** Registers the credential command runner only when a credential command is requested. */
@Configuration
@ConditionalOnProperty(name = CredentialCommand.PROPERTY)
public class CredentialCommandConfiguration {

    @Bean
    PasswordPrompt passwordPrompt() {
        return new ConsolePasswordPrompt();
    }

    // Runs after RolePermissionSeedRunner, so that the administrator role exists
    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    CredentialCommandRunner credentialCommandRunner(@Value("${" + CredentialCommand.PROPERTY + "}") String command,
            @Value("${solgases.credentials.username:}") String username,
            @Value("${solgases.credentials.display-name:}") String displayName,
            CreateFirstAdministratorUseCase createFirstAdministratorUseCase,
            ProvisionUserCredentialUseCase provisionUserCredentialUseCase, PasswordPrompt passwordPrompt,
            ApplicationContext context) {
        return new CredentialCommandRunner(CredentialCommand.fromValue(command), username, displayName,
                createFirstAdministratorUseCase, provisionUserCredentialUseCase, passwordPrompt, context);
    }
}
