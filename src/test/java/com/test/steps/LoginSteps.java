package com.test.steps;

import com.test.attachments.CommonAttachments;
import com.test.base.BaseTest;
import com.test.config.ActionType;
import com.test.config.ConnectionConfig;
import com.test.mocks.MockManager;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class LoginSteps {

    private static BaseTest baseTest = new BaseTest();

    @Step("Настройка мока: /auth возвращает успех (200)")
    public static void mockAuthSuccess() {
        MockManager.mockAuthSuccess();
        CommonAttachments.attachInfo("Мок /auth настроен на успешный ответ (200 OK)");
    }

    @Step("Настройка мока: /auth возвращает ошибку {statusCode}")
    public static void mockAuthError(int statusCode, String errorBody) {
        MockManager.mockAuthError(statusCode, errorBody);
        CommonAttachments.attachInfo(String.format("Мок /auth настроен на ошибку %d", statusCode));
    }

    @Step("Настройка мока: /auth с таймаутом")
    public static void mockAuthTimeout() {
        MockManager.mockAuthTimeout();
        CommonAttachments.attachInfo("Мок /auth настроен с задержкой 10 секунд");
    }

    @Step("Отправка ПЕРВОГО запроса LOGIN")
    public static Response sendFirstLogin(String token) {
        Response response = baseTest.sendRequest(token, ActionType.LOGIN);
        CommonAttachments.attachRequestResponseDetails("POST",
                ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT,
                token, ActionType.LOGIN, ConnectionConfig.API_KEY, response,
                "ПЕРВЫЙ LOGIN ЗАПРОС");
        return response;
    }

    @Step("Отправка ВТОРОГО запроса LOGIN (повторный)")
    public static Response sendSecondLogin(String token) {
        Response response = baseTest.sendRequest(token, ActionType.LOGIN);
        CommonAttachments.attachRequestResponseDetails("POST",
                ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT,
                token, ActionType.LOGIN, ConnectionConfig.API_KEY, response,
                "ВТОРОЙ LOGIN ЗАПРОС (ПОВТОРНЫЙ)");
        return response;
    }

    @Step("Отправка LOGIN с лишними параметрами")
    public static Response sendLoginWithExtraParams(String token) {
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .formParam("extra1", "value1")
                .formParam("extra2", "value2")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);
        CommonAttachments.attachRequestResponseDetails("POST",
                ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT,
                token, ActionType.LOGIN, ConnectionConfig.API_KEY, response,
                "LOGIN С ЛИШНИМИ ПАРАМЕТРАМИ",
                "extra1", "value1", "extra2", "value2");
        return response;
    }

    @Step("Отправка LOGIN с дублирующимися параметрами")
    public static Response sendLoginWithDuplicateParams(String token1, String token2) {
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token1)
                .formParam("token", token2)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);
        String description = String.format("LOGIN ЗАПРОС (дубликаты token: %s и %s)", token1, token2);
        CommonAttachments.attachRequestResponseDetails("POST",
                ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT,
                token1, ActionType.LOGIN, ConnectionConfig.API_KEY, response,
                description, "token(дубликат)", token2);
        return response;
    }
}