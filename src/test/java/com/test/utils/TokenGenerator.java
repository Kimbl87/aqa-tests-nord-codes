package com.test.utils;

import io.qameta.allure.Attachment;

import java.security.SecureRandom;
import java.util.Random;

public class TokenGenerator {
    private static final String CHARS = "0123456789ABCDEF";
    private static final int TOKEN_LENGTH = 32;
    private static final Random random = new SecureRandom();

    public static String generateValidToken() {
        return generateValidToken(true);
    }

    public static String generateValidToken(boolean attachToReport) {
        String token = generateTokenFromCharset(CHARS);
        if (attachToReport) {
            attachToken(token);
        }
        return token;
    }

    public static String generateDigitsOnlyToken() {
        return generateDigitsOnlyToken(true);
    }

    public static String generateDigitsOnlyToken(boolean attachToReport) {
        String digits = "0123456789";
        String token = generateTokenFromCharset(digits);
        if (attachToReport) {
            attachToken(token);
        }
        return token;
    }

    public static String generateAtoFToken() {
        return generateAtoFToken(true);
    }

    public static String generateAtoFToken(boolean attachToReport) {
        String atof = "ABCDEF";
        String token = generateTokenFromCharset(atof);
        if (attachToReport) {
            attachToken(token);
        }
        return token;
    }

    public static String generateGtoZToken() {
        return generateGtoZToken(true);
    }

    public static String generateGtoZToken(boolean attachToReport) {
        String gtoz = "GHIJKLMNOPQRSTUVWXYZ";
        String token = generateTokenFromCharset(gtoz);
        if (attachToReport) {
            attachToken(token);
        }
        return token;
    }

    public static String generateLowercaseToken() {
        return generateLowercaseToken(true);
    }

    public static String generateLowercaseToken(boolean attachToReport) {
        String lowercase = "abcdef";
        String token = generateTokenFromCharset(lowercase);
        if (attachToReport) {
            attachToken(token);
        }
        return token;
    }

    private static String generateTokenFromCharset(String charset) {
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(charset.charAt(random.nextInt(charset.length())));
        }
        return token.toString();
    }

    @Attachment(value = "Сгенерированный токен", type = "text/plain")
    private static String attachToken(String token) {
        return token;
    }
}