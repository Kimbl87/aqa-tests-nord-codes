package com.test.assertions;

import com.test.attachments.CommonAttachments;
import io.restassured.response.Response;

public class LoginAssertions {

    public static void verifyLoginSuccess(Response response) {
        CommonAssertions.verifyStatusCode(response, 200, "Успешный LOGIN");
        CommonAssertions.verifyResultField(response, "OK");
        CommonAssertions.verifyMessageAbsent(response);
        CommonAttachments.attachVerificationResult("Успешный LOGIN", "OK");
    }

    public static void verifyAuthError(Response response, int expectedStatusCode, String expectedErrorBody) {
        CommonAssertions.verifyStatusCode(response, 500,
                String.format("Ошибка внешнего сервиса (ожидался %d)", expectedStatusCode));
        CommonAssertions.verifyResultField(response, "ERROR");
        CommonAssertions.verifyMessageField(response, "Internal Server Error");
        CommonAttachments.attachVerificationResult("Ошибка внешнего сервиса",
                String.format("Корректно обработана ошибка %d", expectedStatusCode));
    }

    public static void verifyAuthTimeout(Response response) {
        CommonAssertions.verifyStatusCode(response, 500, "Таймаут внешнего сервиса");
        CommonAssertions.verifyResultField(response, "ERROR");
        CommonAssertions.verifyMessageField(response, "Internal Server Error");
        CommonAttachments.attachVerificationResult("Таймаут", "Корректно обработан таймаут");
    }

    public static void verifyAuthServiceUnavailable(Response response) {
        CommonAssertions.verifyStatusCode(response, 500, "Сервис недоступен");
        CommonAssertions.verifyResultField(response, "ERROR");
        CommonAssertions.verifyMessageField(response, "Internal Server Error");
        CommonAttachments.attachVerificationResult("Сервис недоступен", "Корректно обработана недоступность");
    }

    public static void verifyLoginConflict(Response response, String token) {
        CommonAssertions.verifyStatusCode(response, 409, "Повторный LOGIN");
        CommonAssertions.verifyResultField(response, "ERROR");
        CommonAssertions.verifyMessageContains(response, "already exists");
        CommonAttachments.attachVerificationResult("Конфликт при повторном LOGIN",
                String.format("Получен ответ 409 для токена %s", token));
    }

    public static void verifyDuplicateParamsError(Response response) {
        CommonAssertions.verifyStatusCode(response, 400, "Дублирующиеся параметры");
        CommonAssertions.verifyResultField(response, "ERROR");
        CommonAttachments.attachVerificationResult("Дублирующиеся параметры", "Получена ошибка 400");
    }

    public static void verifyInvalidTokenError(Response response, String token) {
        CommonAssertions.verifyStatusCode(response, 400, "Невалидный токен");
        CommonAssertions.verifyResultField(response, "ERROR");
        CommonAttachments.attachVerificationResult("Невалидный токен",
                String.format("Получена ошибка 400 для токена: %s", token));
    }
}