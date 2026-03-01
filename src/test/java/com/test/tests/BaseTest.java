package com.test.tests;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.test.utils.TokenGenerator;
import org.junit.jupiter.api.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
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

    //sendRequest и его перегрузки
    protected Response sendRequest(ActionType action) {
        return sendRequest(TokenGenerator.generateValidToken(), action, ConnectionConfig.API_KEY);
    }

    protected Response sendRequest(String token, ActionType action) {
        return sendRequest(token, action, ConnectionConfig.API_KEY);
    }


    protected Response sendRequest(String token, ActionType action, String apiKey) {
        return RestAssured
                .given()
                .header("X-Api-Key", apiKey)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", action.toString())
                .when()
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT)
                .then()
                .extract()
                .response();
    }

    protected Response sendRequest(String token, ActionType action, String apiKey, String baseUrl, String endpoint) {
        return RestAssured
                .given()
                .header("X-Api-Key", apiKey)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", action.toString())
                .when()
                .post(baseUrl + endpoint)
                .then()
                .extract()
                .response();
    }
}