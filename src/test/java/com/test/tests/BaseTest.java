package com.test.tests;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.test.utils.TokenGenerator;
import org.junit.jupiter.api.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import com.test.config.ConnectionConfig;
import com.test.config.ActionType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class BaseTest {

    protected static WireMockServer mockServer;

    //Моки базовый
    @BeforeAll
    static void startMock() {
        mockServer = new WireMockServer(8888);
        mockServer.start();
        WireMock.configureFor(8888);
        System.out.println("Mock запущен");
    }

    @AfterAll
    static void stopMock() {
        mockServer.stop();
        System.out.println("Mock остановлен");
    }

    @BeforeEach
    void setUp() {
        mockServer.resetAll();
    }

    //Моки auth
    protected void mockAuthSuccess() {
        stubFor(post(urlEqualTo("/auth"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"success\"}")));
        System.out.println("  Мок auth: успех");
    }

    protected void mockAuthFailure() {
        stubFor(post(urlEqualTo("/auth"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"unauthorized\"}")));
        System.out.println("Мок auth: ошибка");
    }

    //Моки action
    protected void mockActionSuccess() {
        stubFor(post(urlEqualTo("/doAction"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"done\"}")));
        System.out.println("Мок doAction: успех");
    }

    protected void mockActionFailure() {
        stubFor(post(urlEqualTo("/doAction"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"internal error\"}")));
        System.out.println("Мок doAction: ошибка");
    }

    // ===== УНИВЕРСАЛЬНЫЙ МЕТОД ДЛЯ ВСЕХ HTTP ЗАПРОСОВ =====
    private RequestSpecification baseRequest(String apiKey) {
        return RestAssured
                .given()
                .header("X-Api-Key", apiKey)
                .contentType("application/x-www-form-urlencoded");
    }

    // Основной метод с указанием HTTP метода (полная версия)
    protected Response sendRequestWithMethod(String httpMethod, String token, ActionType action, String apiKey) {
        RequestSpecification request = baseRequest(apiKey)
                .formParam("token", token)
                .formParam("action", action.toString());

        switch (httpMethod.toUpperCase()) {
            case "POST":
                return request.when().post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT).then().extract().response();
            case "GET":
                return RestAssured
                        .given()
                        .header("X-Api-Key", apiKey)
                        .when()
                        .get(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT)
                        .then()
                        .extract()
                        .response();
            case "PUT":
                return request.when().put(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT).then().extract().response();
            case "DELETE":
                return request.when().delete(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT).then().extract().response();
            case "PATCH":
                return request.when().patch(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT).then().extract().response();
            case "OPTIONS":
                return RestAssured
                        .given()
                        .header("X-Api-Key", apiKey)
                        .when()
                        .options(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT)
                        .then()
                        .extract()
                        .response();
            case "HEAD":
                return RestAssured
                        .given()
                        .header("X-Api-Key", apiKey)
                        .when()
                        .head(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT)
                        .then()
                        .extract()
                        .response();
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + httpMethod);
        }
    }

    // Перегрузки для удобства
    protected Response sendRequestWithMethod(String httpMethod, ActionType action) {
        return sendRequestWithMethod(httpMethod, TokenGenerator.generateValidToken(), action, ConnectionConfig.API_KEY);
    }

    protected Response sendRequestWithMethod(String httpMethod, String token, ActionType action) {
        return sendRequestWithMethod(httpMethod, token, action, ConnectionConfig.API_KEY);
    }

    protected Response sendRequestWithMethod(String httpMethod, ActionType action, String apiKey) {
        return sendRequestWithMethod(httpMethod, TokenGenerator.generateValidToken(), action, apiKey);
    }

    // Для обратной совместимости (POST по умолчанию) - имена методов оставляем без изменений
    protected Response sendRequest(ActionType action) {
        return sendRequestWithMethod("POST", action);
    }

    protected Response sendRequest(String token, ActionType action) {
        return sendRequestWithMethod("POST", token, action);
    }

    protected Response sendRequest(String token, ActionType action, String apiKey) {
        return sendRequestWithMethod("POST", token, action, apiKey);
    }
}