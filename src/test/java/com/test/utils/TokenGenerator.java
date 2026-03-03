package com.test.utils;

import java.security.SecureRandom;
import java.util.Random;

public class TokenGenerator {
    /*В требованиях указано token - строка длиной 32 символа, состоящая только из символов A-Z0-9, но в реализации
     A-F0-9 и если сгенерировать токен по требованиям - большинство тестов упадёт из-за непрошедший авторизации
    */
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

    public static String generateDigitsOnlyToken() {
        String digits = "0123456789";
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(digits.charAt(random.nextInt(digits.length())));
        }
        return token.toString();
    }

    public static String generateAtoFToken() {
        String atof = "ABCDEF";
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(atof.charAt(random.nextInt(atof.length())));
        }
        return token.toString();
    }

    public static String generateGtoZToken() {
        String gtoz = "GHIJKLMNOPQRSTUVWXYZ";
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(gtoz.charAt(random.nextInt(gtoz.length())));
        }
        return token.toString();
    }

    public static String generateLowercaseToken() {
        String lowercase = "abcdef";
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(lowercase.charAt(random.nextInt(lowercase.length())));
        }
        return token.toString();
    }
}