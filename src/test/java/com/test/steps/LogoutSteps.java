package com.test.steps;

import com.test.attachments.CommonAttachments;
import com.test.base.BaseTest;
import com.test.config.ActionType;
import com.test.config.ConnectionConfig;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class LogoutSteps {

    private static BaseTest baseTest = new BaseTest();

    @Step("Отправка LOGOUT запроса")
    public static Response sendLogout(String token) {
        Response response = baseTest.sendRequest(token, ActionType.LOGOUT);
        CommonAttachments.attachRequestResponseDetails("POST",
                ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT,
                token, ActionType.LOGOUT, ConnectionConfig.API_KEY, response,
                "LOGOUT ЗАПРОС");
        return response;
    }

    @Step("Отправка LOGOUT запроса с лишними параметрами")
    public static Response sendLogoutWithExtraParams(String token) {
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGOUT")
                .formParam("extra1", "value1")
                .formParam("extra2", "value2")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);
        CommonAttachments.attachRequestResponseDetails("POST",
                ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT,
                token, ActionType.LOGOUT, ConnectionConfig.API_KEY, response,
                "LOGOUT С ЛИШНИМИ ПАРАМЕТРАМИ",
                "extra1", "value1", "extra2", "value2");
        return response;
    }
}