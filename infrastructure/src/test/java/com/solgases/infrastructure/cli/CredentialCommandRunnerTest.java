package com.solgases.infrastructure.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.solgases.application.dto.AuthenticationCommand;
import com.solgases.application.dto.RolePermissionSeedCatalog;
import com.solgases.application.dto.UserCommand;
import com.solgases.application.exception.AuthenticationFailedException;
import com.solgases.application.exception.CredentialAlreadyExistsException;
import com.solgases.application.exception.FirstAdministratorAlreadyExistsException;
import com.solgases.application.port.in.AuthenticateUserUseCase;
import com.solgases.application.port.in.CreateFirstAdministratorUseCase;
import com.solgases.application.port.in.CreateUserUseCase;
import com.solgases.application.port.in.LoadRolePermissionSeedUseCase;
import com.solgases.application.port.in.ProvisionUserCredentialUseCase;
import com.solgases.application.port.out.UserPersistencePort;
import com.solgases.domain.model.Role;
import com.solgases.domain.model.User;
import com.solgases.infrastructure.SolgasesApplication;
import com.solgases.infrastructure.TestJwtSecret;
import com.solgases.infrastructure.persistence.entity.UserJpaEntity;
import com.solgases.infrastructure.persistence.repository.UserCredentialJpaRepository;
import com.solgases.infrastructure.persistence.repository.UserJpaRepository;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Runs the credential commands against an in-memory H2 database with synthetic users and random passwords; no
 * real user or credential is involved. The password prompt is replaced by a scripted one.
 */
@SpringBootTest(classes = SolgasesApplication.class)
@AutoConfigureTestDatabase
@ExtendWith(OutputCaptureExtension.class)
class CredentialCommandRunnerTest {

    @DynamicPropertySource
    static void securityProperties(DynamicPropertyRegistry registry) {
        TestJwtSecret.register(registry);
    }

    @Autowired
    private CreateFirstAdministratorUseCase createFirstAdministratorUseCase;
    @Autowired
    private ProvisionUserCredentialUseCase provisionUserCredentialUseCase;
    @Autowired
    private AuthenticateUserUseCase authenticateUserUseCase;
    @Autowired
    private CreateUserUseCase createUserUseCase;
    @Autowired
    private LoadRolePermissionSeedUseCase loadRolePermissionSeedUseCase;
    @Autowired
    private RolePermissionSeedCatalog catalog;
    @Autowired
    private UserPersistencePort userPersistencePort;
    @Autowired
    private UserJpaRepository userRepository;
    @Autowired
    private UserCredentialJpaRepository credentialRepository;
    @Autowired
    private ApplicationContext context;
    private TransactionTemplate transaction;

    @Autowired
    void setTransactionManager(PlatformTransactionManager transactionManager) {
        transaction = new TransactionTemplate(transactionManager);
    }

    @BeforeEach
    void setUp() {
        cleanUp();
        // Other test classes may have cleaned the roles, so the idempotent seed is run again
        loadRolePermissionSeedUseCase.execute(catalog);
    }

    @AfterEach
    void cleanUp() {
        credentialRepository.deleteAll();
        userRepository.deleteAll();
    }

    /** Returns the scripted answers in order and keeps them, so that the test can check they were cleared. */
    private static final class ScriptedPasswordPrompt implements PasswordPrompt {

        private final Deque<char[]> answers = new ArrayDeque<>();
        private final List<char[]> returned = new ArrayList<>();

        ScriptedPasswordPrompt(String... answers) {
            for (String answer : answers) {
                this.answers.add(answer.toCharArray());
            }
        }

        @Override
        public char[] readPassword(String prompt) {
            char[] answer = answers.removeFirst();
            returned.add(answer);
            return answer;
        }

        boolean allCleared() {
            return returned.stream().allMatch(answer -> new String(answer).chars().allMatch(c -> c == 0));
        }
    }

    private CredentialCommandRunner runner(CredentialCommand command, String username, String displayName,
            PasswordPrompt prompt, ApplicationContext applicationContext) {
        return new CredentialCommandRunner(command, username, displayName, createFirstAdministratorUseCase,
                provisionUserCredentialUseCase, prompt, applicationContext);
    }

    private void run(CredentialCommand command, String username, String displayName, PasswordPrompt prompt) {
        runner(command, username, displayName, prompt, context).run(new DefaultApplicationArguments());
    }

    private static String syntheticUsername() {
        return "synthetic-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private static String randomPassword() {
        return "pw-" + UUID.randomUUID();
    }

    @Test
    void firstAdministratorIsCreatedWithAnArgon2idHashAndCanAuthenticate(CapturedOutput output) {
        String username = syntheticUsername();
        String password = randomPassword();
        ScriptedPasswordPrompt prompt = new ScriptedPasswordPrompt(password, password);

        run(CredentialCommand.CREATE_FIRST_ADMIN, username, "Synthetic Admin", prompt);

        // Roles and permissions are read in one transaction, as the use cases do
        User administrator = transaction.execute(status -> userPersistencePort.findByUsername(username))
                .orElseThrow();
        assertThat(administrator.active()).isTrue();
        assertThat(administrator.roles()).extracting(Role::key).containsExactly("ADMIN");
        String hash = credentialRepository.findById(administrator.id()).orElseThrow().getPasswordHash();
        assertThat(hash).startsWith("$argon2id$").doesNotContain(password);
        assertThat(authenticateUserUseCase.execute(new AuthenticationCommand(username, password)).userId())
                .isEqualTo(administrator.id());
        assertThat(prompt.allCleared()).isTrue();
        assertThat(output).contains("First administrator created with user id " + administrator.id())
                .doesNotContain(password);
    }

    @Test
    void anotherFirstAdministratorIsRefusedWhileAnActiveAdministratorExists() {
        String password = randomPassword();
        run(CredentialCommand.CREATE_FIRST_ADMIN, syntheticUsername(), "Synthetic Admin",
                new ScriptedPasswordPrompt(password, password));
        String secondUsername = syntheticUsername();

        assertThatThrownBy(() -> run(CredentialCommand.CREATE_FIRST_ADMIN, secondUsername, "Second Admin",
                new ScriptedPasswordPrompt(password, password)))
                .isInstanceOf(FirstAdministratorAlreadyExistsException.class);

        assertThat(userRepository.findByUsername(secondUsername)).isEmpty();
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void firstAdministratorCanBeCreatedWhenTheOnlyAdministratorIsInactive() {
        String password = randomPassword();
        String firstUsername = syntheticUsername();
        run(CredentialCommand.CREATE_FIRST_ADMIN, firstUsername, "Synthetic Admin",
                new ScriptedPasswordPrompt(password, password));
        UserJpaEntity first = userRepository.findByUsername(firstUsername).orElseThrow();
        first.deactivate();
        userRepository.saveAndFlush(first);

        run(CredentialCommand.CREATE_FIRST_ADMIN, syntheticUsername(), "Replacement Admin",
                new ScriptedPasswordPrompt(password, password));

        assertThat(userRepository.count()).isEqualTo(2);
    }

    @Test
    void credentialIsProvisionedForAnExistingUserAndNeverOverwritten() {
        String username = syntheticUsername();
        Long userId = createUserUseCase.execute(new UserCommand(username, "Synthetic User", Set.of())).id();
        String password = randomPassword();

        run(CredentialCommand.PROVISION_CREDENTIAL, username, null, new ScriptedPasswordPrompt(password, password));

        String hash = credentialRepository.findById(userId).orElseThrow().getPasswordHash();
        assertThat(hash).startsWith("$argon2id$");
        assertThat(authenticateUserUseCase.execute(new AuthenticationCommand(username, password)).userId())
                .isEqualTo(userId);

        String otherPassword = randomPassword();
        assertThatThrownBy(() -> run(CredentialCommand.PROVISION_CREDENTIAL, username, null,
                new ScriptedPasswordPrompt(otherPassword, otherPassword)))
                .isInstanceOf(CredentialAlreadyExistsException.class)
                .hasMessageContaining("password change flow");
        assertThat(credentialRepository.findById(userId).orElseThrow().getPasswordHash()).isEqualTo(hash);
    }

    @Test
    void mismatchedConfirmationStoresNothingAndClearsBothAnswers() {
        ScriptedPasswordPrompt prompt = new ScriptedPasswordPrompt(randomPassword(), randomPassword());

        assertThatThrownBy(() -> run(CredentialCommand.CREATE_FIRST_ADMIN, syntheticUsername(), "Synthetic Admin",
                prompt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The passwords do not match");

        assertThat(userRepository.count()).isZero();
        assertThat(prompt.allCleared()).isTrue();
    }

    @Test
    void passwordsOutsideThePolicyStoreNothingAndAreCleared() {
        for (String outside : List.of("x".repeat(14), "x".repeat(129))) {
            ScriptedPasswordPrompt prompt = new ScriptedPasswordPrompt(outside, outside);

            assertThatThrownBy(() -> run(CredentialCommand.CREATE_FIRST_ADMIN, syntheticUsername(),
                    "Synthetic Admin", prompt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("The password must have between 15 and 128 characters");
            assertThat(prompt.allCleared()).isTrue();
        }
        assertThat(userRepository.count()).isZero();
        assertThat(credentialRepository.count()).isZero();
    }

    @Test
    void passwordsAtTheLimitsWithSpacesAndUnicodeAreStoredExactlyAndAuthenticate() {
        // 15 and 128 code points; the second one has 256 UTF-16 units
        for (String password : List.of("  ñ espacios \uD83D\uDD12 ", "\uD83D\uDD12".repeat(128))) {
            String username = syntheticUsername();
            Long userId = createUserUseCase.execute(new UserCommand(username, "Synthetic User", Set.of())).id();

            run(CredentialCommand.PROVISION_CREDENTIAL, username, null, new ScriptedPasswordPrompt(password, password));

            assertThat(authenticateUserUseCase.execute(new AuthenticationCommand(username, password)).userId())
                    .isEqualTo(userId);
            // The value is used as entered: when trimming changes it, the trimmed variant does not match
            if (!password.strip().equals(password)) {
                assertThatThrownBy(() -> authenticateUserUseCase.execute(new AuthenticationCommand(username,
                        password.strip())))
                        .isInstanceOf(AuthenticationFailedException.class);
            }
        }
    }

    @Test
    void missingArgumentsAreRejectedBeforeReadingThePassword() {
        ScriptedPasswordPrompt prompt = new ScriptedPasswordPrompt();

        assertThatThrownBy(() -> run(CredentialCommand.CREATE_FIRST_ADMIN, " ", "Synthetic Admin", prompt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("solgases.credentials.username");
        assertThatThrownBy(() -> run(CredentialCommand.CREATE_FIRST_ADMIN, syntheticUsername(), null, prompt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("solgases.credentials.display-name");
        assertThat(userRepository.count()).isZero();
    }

    @Test
    void commandsRefuseToRunWithAWebServer() {
        ScriptedPasswordPrompt prompt = new ScriptedPasswordPrompt();
        CredentialCommandRunner webRunner = runner(CredentialCommand.CREATE_FIRST_ADMIN, syntheticUsername(),
                "Synthetic Admin", prompt, mock(WebServerApplicationContext.class));

        assertThatThrownBy(() -> webRunner.run(new DefaultApplicationArguments()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("without a web server");
        assertThat(userRepository.count()).isZero();
    }
}
