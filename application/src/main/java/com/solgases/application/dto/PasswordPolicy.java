package com.solgases.application.dto;

/**
 * Approved password policy (Increment 7): between 15 and 128 characters, counted as Unicode code points rather
 * than UTF-16 units. Any character is allowed, spaces included, and there are no composition rules. Passwords
 * are used exactly as entered: they are never trimmed, normalized or truncated.
 */
public final class PasswordPolicy {

    public static final int MIN_LENGTH = 15;
    public static final int MAX_LENGTH = 128;

    private PasswordPolicy() {
    }

    /** Rejects a new password outside the length limits, before it is hashed. The message never includes it. */
    public static void requireValidNewPassword(char[] password) {
        int length = password == null ? 0 : Character.codePointCount(password, 0, password.length);
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "The password must have between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters");
        }
    }

    /** Whether a password received for authentication exceeds the maximum length. */
    public static boolean exceedsMaxLength(CharSequence password) {
        // A code point takes one or two UTF-16 units, so the exact count is only needed in between
        if (password.length() <= MAX_LENGTH) {
            return false;
        }
        if (password.length() > 2 * MAX_LENGTH) {
            return true;
        }
        return Character.codePointCount(password, 0, password.length()) > MAX_LENGTH;
    }
}
