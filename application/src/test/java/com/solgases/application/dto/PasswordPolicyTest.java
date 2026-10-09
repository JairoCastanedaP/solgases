package com.solgases.application.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PasswordPolicyTest {

    // U+1F512 (lock) is one code point encoded as two UTF-16 units
    private static final String SUPPLEMENTARY = "🔒";

    private static char[] repeat(String unit, int times) {
        return unit.repeat(times).toCharArray();
    }

    @ParameterizedTest(name = "{0} characters are accepted")
    @ValueSource(ints = {15, 64, 128})
    void lengthsWithinTheLimitsAreAccepted(int length) {
        assertThatCode(() -> PasswordPolicy.requireValidNewPassword(repeat("a", length))).doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "{0} characters are rejected")
    @ValueSource(ints = {0, 14, 129})
    void lengthsOutsideTheLimitsAreRejectedWithoutRevealingThePassword(int length) {
        char[] password = repeat("b", length);

        assertThatThrownBy(() -> PasswordPolicy.requireValidNewPassword(password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The password must have between 15 and 128 characters");
    }

    @Test
    void nullIsRejected() {
        assertThatThrownBy(() -> PasswordPolicy.requireValidNewPassword(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "{0} supplementary characters are accepted")
    @ValueSource(ints = {15, 64, 128})
    void unicodeIsCountedInCodePointsNotInUtf16Units(int length) {
        char[] password = repeat(SUPPLEMENTARY, length);
        assertThat(password).hasSize(2 * length);

        assertThatCode(() -> PasswordPolicy.requireValidNewPassword(password)).doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "{0} supplementary characters are rejected")
    @ValueSource(ints = {14, 129})
    void unicodeOutsideTheLimitsIsRejected(int length) {
        // 14 code points are 28 UTF-16 units: counting units instead of code points would accept them
        assertThatThrownBy(() -> PasswordPolicy.requireValidNewPassword(repeat(SUPPLEMENTARY, length)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "[{index}] is accepted as entered")
    @ValueSource(strings = {
        "               ",
        "  leading and trailing  ",
        "contraseña con ñ y acentos",
        "密码密码密码密码密码密码密码密"})
    void spacesAndUnicodeAreAllowedWithoutCompositionRules(String password) {
        assertThatCode(() -> PasswordPolicy.requireValidNewPassword(password.toCharArray()))
                .doesNotThrowAnyException();
    }

    @Test
    void authenticationMaximumCountsCodePoints() {
        assertThat(PasswordPolicy.exceedsMaxLength("a".repeat(128))).isFalse();
        assertThat(PasswordPolicy.exceedsMaxLength("a".repeat(129))).isTrue();
        assertThat(PasswordPolicy.exceedsMaxLength(SUPPLEMENTARY.repeat(128))).isFalse();
        assertThat(PasswordPolicy.exceedsMaxLength(SUPPLEMENTARY.repeat(129))).isTrue();
        assertThat(PasswordPolicy.exceedsMaxLength("a".repeat(10_000))).isTrue();
        // The minimum only applies to new passwords
        assertThat(PasswordPolicy.exceedsMaxLength("short")).isFalse();
    }
}
