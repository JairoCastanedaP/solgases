package com.solgases.infrastructure.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class CredentialCommandTest {

    @Test
    void isRequestedOnlyByTheCommandLineProperty() {
        assertThat(CredentialCommand.isRequested(new String[] {"--solgases.credentials.command=create-first-admin"}))
                .isTrue();
        assertThat(CredentialCommand.isRequested(new String[] {"--spring.profiles.active=local"})).isFalse();
        assertThat(CredentialCommand.isRequested(new String[0])).isFalse();
    }

    @Test
    void commandsAreParsedByTheirValue() {
        assertThat(CredentialCommand.fromValue("create-first-admin")).isEqualTo(CredentialCommand.CREATE_FIRST_ADMIN);
        assertThat(CredentialCommand.fromValue("provision-credential"))
                .isEqualTo(CredentialCommand.PROVISION_CREDENTIAL);
        assertThatThrownBy(() -> CredentialCommand.fromValue("reset-password"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
