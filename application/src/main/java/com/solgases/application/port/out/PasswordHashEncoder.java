package com.solgases.application.port.out;

public interface PasswordHashEncoder {

    /** Encodes a raw password into a hash that embeds its salt and cost parameters. */
    String encode(CharSequence rawPassword);
}
