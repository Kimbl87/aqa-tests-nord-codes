package com.test.assertions;

import com.test.attachments.CommonAttachments;
import io.restassured.response.Response;

public class LogoutAssertions {

    public static void verifyLogoutSuccess(Response response) {
        CommonAssertions.verifyStatusCode(response, 200, "Успешный LOGOUT");
        CommonAssertions.verifyResultField(response, "OK");
        CommonAssertions.verifyMessageAbsent(response);
        CommonAttachments.attachVerificationResult("Успешный LOGOUT", "OK");
    }

    public static void verifyLogoutWithoutLogin(Response response, String token) {
        CommonAssertions.verifyStatusCode(response, 403, "LOGOUT без логина");
        CommonAssertions.verifyResultField(response, "ERROR");
        CommonAssertions.verifyMessageContains(response, "Token '" + token + "' not found");
        CommonAttachments.attachVerificationResult("LOGOUT без логина",
                String.format("Получена ошибка 403 для токена %s", token));
    }
}