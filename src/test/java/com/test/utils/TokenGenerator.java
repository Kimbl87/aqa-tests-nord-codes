package com.test.utils;

import java.security.SecureRandom;
import java.util.Random;

public class TokenGenerator {
    private static final String CHARS = "0123456789ABCDEF";
    private static final int TOKEN_LENGTH = 32;
    private static final Random random = new SecureRandom();

    public static String generateValidToken() {
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return token.toString();
    }
}