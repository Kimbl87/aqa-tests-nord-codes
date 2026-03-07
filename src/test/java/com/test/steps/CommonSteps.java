package com.test.steps;

import com.test.attachments.CommonAttachments;
import com.test.utils.TokenGenerator;
import io.qameta.allure.Step;

public class CommonSteps {

    @Step("Генерация тестового токена: {purpose}")
    public static String generateToken(String purpose) {
        return TokenGenerator.generateValidToken();
    }

    @Step("Генерация тестового токена из цифр: {purpose}")
    public static String generateDigitsToken(String purpose) {
        return TokenGenerator.generateDigitsOnlyToken();
    }

    @Step("Генерация тестового токена из букв A-F: {purpose}")
    public static String generateAtoFToken(String purpose) {
        return TokenGenerator.generateAtoFToken();
    }

    @Step("Генерация тестового токена в нижнем регистре: {purpose}")
    public static String generateLowercaseToken(String purpose) {
        return TokenGenerator.generateLowercaseToken();
    }

    @Step("Ожидание {millis} мс: {reason}")
    public static void wait(long millis, String reason) throws InterruptedException {
        CommonAttachments.attachInfo(String.format("Ожидание %d мс: %s...", millis, reason));
        Thread.sleep(millis);
        CommonAttachments.attachInfo(String.format("Ожидание %d мс завершено", millis));
    }
}