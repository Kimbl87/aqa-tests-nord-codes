package com.test.steps;

import com.test.attachments.CommonAttachments;
import com.test.base.BaseTest;
import com.test.config.ActionType;
import com.test.config.ConnectionConfig;
import com.test.mocks.MockManager;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;

public class ActionSteps {

    private static BaseTest baseTest = new BaseTest();

    @Step("Настройка мока: /doAction возвращает успех (200)")
    public static void mockActionSuccess() {
        MockManager.mockActionSuccess();
        CommonAttachments.attachInfo("Мок /doAction настроен на успешный ответ (200 OK)");
    }

    @Step("Настройка мока: /doAction возвращает ошибку {statusCode}")
    public static void mockActionError(int statusCode, String errorBody) {
        MockManager.mockActionError(statusCode, errorBody);
        CommonAttachments.attachInfo(String.format("Мок /doAction настроен на ошибку %d", statusCode));
    }

    @Step("Настройка мока: /doAction возвращает ошибку (500)")
    public static void mockActionFailure() {
        MockManager.mockActionFailure();
        CommonAttachments.attachInfo("Мок /doAction настроен на ошибку 500");
    }

    @Step("Настройка мока: /doAction с задержкой (таймаут)")
    public static void mockActionTimeout() {
        MockManager.mockActionTimeout();
        CommonAttachments.attachInfo("Мок /doAction настроен с задержкой 10 секунд");
    }

    @Step("Настройка мока: /doAction возвращает пустой ответ")
    public static void mockActionEmptyResponse() {
        MockManager.mockEmptyResponse("/doAction");
        CommonAttachments.attachInfo("Мок /doAction настроен на пустой ответ");
    }

    @Step("Настройка мока: /doAction возвращает некорректный JSON")
    public static void mockActionInvalidJson() {
        MockManager.mockInvalidJson("/doAction");
        CommonAttachments.attachInfo("Мок /doAction настроен на некорректный JSON");
    }

    @Step("Отправка ACTION запроса")
    public static Response sendAction(String token) {
        Response response = baseTest.sendRequest(token, ActionType.ACTION);
        CommonAttachments.attachRequestResponseDetails("POST",
                ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT,
                token, ActionType.ACTION, ConnectionConfig.API_KEY, response,
                "ACTION ЗАПРОС");
        return response;
    }

    @Step("Отправка ACTION запроса с лишними параметрами")
    public static Response sendActionWithExtraParams(String token) {
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "ACTION")
                .formParam("extra1", "value1")
                .formParam("extra2", "value2")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);
        CommonAttachments.attachRequestResponseDetails("POST",
                ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT,
                token, ActionType.ACTION, ConnectionConfig.API_KEY, response,
                "ACTION С ЛИШНИМИ ПАРАМЕТРАМИ",
                "extra1", "value1", "extra2", "value2");
        return response;
    }

    @Step("Отправка {count} параллельных ACTION запросов")
    public static void sendConcurrentActions(String token, int count) throws Exception {
        CommonAttachments.attachInfo(String.format("Запуск %d параллельных ACTION запросов", count));
        ExecutorService executor = Executors.newFixedThreadPool(count);
        List<Future<Response>> futures = new ArrayList<>();
        List<Integer> statusCodes = new ArrayList<>();
        List<String> threadNames = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            futures.add(executor.submit(() -> {
                threadNames.add(Thread.currentThread().getName());
                Response response = baseTest.sendRequest(token, ActionType.ACTION);
                statusCodes.add(response.statusCode());
                return response;
            }));
        }
        for (Future<Response> future : futures) {
            future.get();
        }
        executor.shutdown();
        CommonAttachments.attachConcurrentSummary(count, token, threadNames, statusCodes, new ArrayList<>());
    }
}