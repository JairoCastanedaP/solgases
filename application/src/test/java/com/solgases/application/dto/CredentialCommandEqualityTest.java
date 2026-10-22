package com.solgases.application.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CredentialCommandEqualityTest {

    private static final String PASSWORD = "synthetic-password-01";
    private static final String OTHER_PASSWORD = "synthetic-password-02";

    @Test
    void provisioningCommandsWithDistinctArraysOfTheSameContentAreEqual() {
        CredentialProvisioningCommand command = new CredentialProvisioningCommand("synthetic-user",
                PASSWORD.toCharArray());
        CredentialProvisioningCommand sameContent = new CredentialProvisioningCommand("synthetic-user",
                PASSWORD.toCharArray());

        assertThat(command.password()).isNotSameAs(sameContent.password());
        assertThat(command).isEqualTo(sameContent).hasSameHashCodeAs(sameContent);
    }

    @Test
    void provisioningCommandsDifferingInPasswordOrUsernameAreNotEqual() {
        CredentialProvisioningCommand command = new CredentialProvisioningCommand("synthetic-user",
                PASSWORD.toCharArray());

        assertThat(command)
                .isNotEqualTo(new CredentialProvisioningCommand("synthetic-user", OTHER_PASSWORD.toCharArray()))
                .isNotEqualTo(new CredentialProvisioningCommand("synthetic-other", PASSWORD.toCharArray()));
    }

    @Test
    void firstAdministratorCommandsWithDistinctArraysOfTheSameContentAreEqual() {
        FirstAdministratorCommand command = new FirstAdministratorCommand("synthetic-admin", "Synthetic Admin",
                PASSWORD.toCharArray());
        FirstAdministratorCommand sameContent = new FirstAdministratorCommand("synthetic-admin", "Synthetic Admin",
                PASSWORD.toCharArray());

        assertThat(command.password()).isNotSameAs(sameContent.password());
        assertThat(command).isEqualTo(sameContent).hasSameHashCodeAs(sameContent);
    }

    @Test
    void firstAdministratorCommandsDifferingInAnyFieldAreNotEqual() {
        FirstAdministratorCommand command = new FirstAdministratorCommand("synthetic-admin", "Synthetic Admin",
                PASSWORD.toCharArray());

        assertThat(command)
                .isNotEqualTo(new FirstAdministratorCommand("synthetic-admin", "Synthetic Admin",
                        OTHER_PASSWORD.toCharArray()))
                .isNotEqualTo(new FirstAdministratorCommand("synthetic-other", "Synthetic Admin",
                        PASSWORD.toCharArray()))
                .isNotEqualTo(new FirstAdministratorCommand("synthetic-admin", "Other Admin",
                        PASSWORD.toCharArray()));
    }
}
