package com.solgases.infrastructure.cli;

import static org.assertj.core.api.Assertions.assertThat;

import com.solgases.application.dto.AuthenticationCommand;
import com.solgases.application.port.in.AuthenticateUserUseCase;
import com.solgases.application.port.out.UserPersistencePort;
import com.solgases.domain.model.Role;
import com.solgases.domain.model.User;
import com.solgases.infrastructure.SolgasesApplication;
import com.solgases.infrastructure.TestJwtSecret;
import com.solgases.infrastructure.persistence.repository.UserCredentialJpaRepository;
import com.solgases.infrastructure.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.convention.TestBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Starts the whole application as the credential command does: without a web application, with the command
 * properties set, against an in-memory H2 database and with a scripted password prompt. The runners execute
 * during startup, so the context only starts if the catalog is loaded before the first administrator is created.
 * Synthetic data only.
 */
@SpringBootTest(classes = SolgasesApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                CredentialCommand.PROPERTY + "=create-first-admin",
                "solgases.credentials.username=" + CredentialCommandStartupTest.USERNAME,
                "solgases.credentials.display-name=Synthetic Startup Admin"})
@AutoConfigureTestDatabase
@ExtendWith(OutputCaptureExtension.class)
class CredentialCommandStartupTest {

    static final String USERNAME = "synthetic-startup-admin";
    private static final String PASSWORD = "synthetic startup password";

    @DynamicPropertySource
    static void securityProperties(DynamicPropertyRegistry registry) {
        TestJwtSecret.register(registry);
    }

    @TestBean
    private PasswordPrompt passwordPrompt;

    // Replaces the console prompt: every read returns a new copy, which the command clears after use
    static PasswordPrompt passwordPrompt() {
        return prompt -> PASSWORD.toCharArray();
    }

    @Autowired
    private ApplicationContext context;
    @Autowired
    private UserPersistencePort userPersistencePort;
    @Autowired
    private UserJpaRepository userRepository;
    @Autowired
    private UserCredentialJpaRepository credentialRepository;
    @Autowired
    private AuthenticateUserUseCase authenticateUserUseCase;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void commandModeStartsWithoutWebServerOrSecurityFilterChain() {
        assertThat(context).isNotInstanceOf(WebServerApplicationContext.class);
        assertThat(context.getBeanNamesForType(SecurityFilterChain.class)).isEmpty();
        assertThat(context.getBean(CredentialCommandRunner.class)).isNotNull();
    }

    @Test
    void catalogIsLoadedBeforeTheCommandCreatesTheFirstAdministrator(CapturedOutput output) {
        int catalogLoaded = output.getOut().indexOf("Initial role/permission catalog loaded");
        int administratorCreated = output.getOut().indexOf("First administrator created with user id");
        assertThat(catalogLoaded).isNotNegative();
        assertThat(administratorCreated).isGreaterThan(catalogLoaded);
        assertThat(output).doesNotContain(PASSWORD);

        User administrator = new TransactionTemplate(transactionManager)
                .execute(status -> userPersistencePort.findByUsername(USERNAME))
                .orElseThrow();
        assertThat(administrator.active()).isTrue();
        assertThat(administrator.roles()).extracting(Role::key).containsExactly("ADMIN");
        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(credentialRepository.findById(administrator.id()).orElseThrow().getPasswordHash())
                .startsWith("$argon2id$");
        assertThat(authenticateUserUseCase.execute(new AuthenticationCommand(USERNAME, PASSWORD)).userId())
                .isEqualTo(administrator.id());
    }

    @Test
    void commandLineRequestSelectsANonWebApplication() {
        assertThat(SolgasesApplication.application(new String[] {"--" + CredentialCommand.PROPERTY + "=x"})
                .getWebApplicationType()).isEqualTo(WebApplicationType.NONE);
        assertThat(SolgasesApplication.application(new String[0]).getWebApplicationType())
                .isEqualTo(WebApplicationType.SERVLET);
    }
}
