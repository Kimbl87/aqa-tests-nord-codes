package com.test.assertions;

import com.test.attachments.CommonAttachments;
import io.restassured.response.Response;

public class ActionAssertions {

    public static void verifyActionSuccess(Response response) {
        CommonAssertions.verifyStatusCode(response, 200, "Успешный ACTION");
        CommonAssertions.verifyResultField(response, "OK");
        CommonAssertions.verifyMessageAbsent(response);
        CommonAttachments.attachVerificationResult("Успешный ACTION", "OK");
    }

    public static void verifyActionError(Response response) {
        CommonAssertions.verifyStatusCode(response, 500, "Ошибка ACTION");
        CommonAssertions.verifyResultField(response, "ERROR");
        CommonAssertions.verifyMessageField(response, "Internal Server Error");
        CommonAttachments.attachVerificationResult("Ошибка ACTION", "Корректно обработана ошибка");
    }

    public static void verifyActionWithoutLogin(Response response, String token) {
        CommonAssertions.verifyStatusCode(response, 403, "ACTION без логина");
        CommonAssertions.verifyResultField(response, "ERROR");
        CommonAssertions.verifyMessageContains(response, "Token '" + token + "' not found");
        CommonAttachments.attachVerificationResult("ACTION без логина",
                String.format("Получена ошибка 403 для токена %s", token));
    }

    public static void verifyActionEmptyResponse(Response response) {
        CommonAssertions.verifyStatusCode(response, 400, "Пустой ответ от сервиса");
        CommonAssertions.verifyResultField(response, "ERROR");
        CommonAttachments.attachVerificationResult("Пустой ответ", "Получена ошибка 400");
    }

    public static void verifyActionInvalidJson(Response response) {
        CommonAssertions.verifyStatusCode(response, 400, "Некорректный JSON");
        CommonAssertions.verifyResultField(response, "ERROR");
        CommonAttachments.attachVerificationResult("Некорректный JSON", "Получена ошибка 400");
    }
}