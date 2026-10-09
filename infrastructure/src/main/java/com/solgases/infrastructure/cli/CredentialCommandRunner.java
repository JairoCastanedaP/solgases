package com.solgases.infrastructure.cli;

import com.solgases.application.dto.CredentialProvisioningCommand;
import com.solgases.application.dto.FirstAdministratorCommand;
import com.solgases.application.port.in.CreateFirstAdministratorUseCase;
import com.solgases.application.port.in.ProvisionUserCredentialUseCase;
import com.solgases.domain.model.User;
import java.util.Arrays;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ApplicationContext;

/**
 * Runs one local credential command after the initial role/permission catalog has been loaded. The password is
 * read twice from the prompt, only its Argon2id hash is stored, and the characters are cleared afterwards.
 * Logs contain the user id, never the username or the password.
 */
public class CredentialCommandRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CredentialCommandRunner.class);

    private final CredentialCommand command;
    private final String username;
    private final String displayName;
    private final CreateFirstAdministratorUseCase createFirstAdministratorUseCase;
    private final ProvisionUserCredentialUseCase provisionUserCredentialUseCase;
    private final PasswordPrompt passwordPrompt;
    private final ApplicationContext context;

    public CredentialCommandRunner(CredentialCommand command, String username, String displayName,
            CreateFirstAdministratorUseCase createFirstAdministratorUseCase,
            ProvisionUserCredentialUseCase provisionUserCredentialUseCase, PasswordPrompt passwordPrompt,
            ApplicationContext context) {
        this.command = command;
        this.username = username;
        this.displayName = displayName;
        this.createFirstAdministratorUseCase = createFirstAdministratorUseCase;
        this.provisionUserCredentialUseCase = provisionUserCredentialUseCase;
        this.passwordPrompt = passwordPrompt;
        this.context = context;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (context instanceof WebServerApplicationContext) {
            throw new IllegalStateException("Credential commands must run without a web server "
                    + "(pass --" + CredentialCommand.PROPERTY + " on the command line)");
        }
        requireText(username, "solgases.credentials.username", User.USERNAME_MAX_LENGTH);
        switch (command) {
            case CREATE_FIRST_ADMIN -> {
                requireText(displayName, "solgases.credentials.display-name", User.DISPLAY_NAME_MAX_LENGTH);
                Long userId = withConfirmedPassword(password -> createFirstAdministratorUseCase.execute(
                        new FirstAdministratorCommand(username, displayName, password)));
                log.info("First administrator created with user id {}", userId);
            }
            case PROVISION_CREDENTIAL -> {
                Long userId = withConfirmedPassword(password -> provisionUserCredentialUseCase.execute(
                        new CredentialProvisioningCommand(username, password)));
                log.info("Credential provisioned for user id {}", userId);
            }
        }
    }

    private Long withConfirmedPassword(Function<char[], Long> action) {
        char[] password = passwordPrompt.readPassword("Password: ");
        char[] confirmation = null;
        try {
            confirmation = passwordPrompt.readPassword("Repeat the password: ");
            if (!Arrays.equals(password, confirmation)) {
                throw new IllegalArgumentException("The passwords do not match");
            }
            return action.apply(password);
        } finally {
            clear(password);
            clear(confirmation);
        }
    }

    private static void clear(char[] characters) {
        if (characters != null) {
            Arrays.fill(characters, '\0');
        }
    }

    private static void requireText(String value, String property, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("--" + property + " is required");
        }
        if (value.length() > maxLength) {
            throw new IllegalArgumentException("--" + property + " must have at most " + maxLength + " characters");
        }
    }
}
