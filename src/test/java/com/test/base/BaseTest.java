package com.test.base;

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

    @BeforeAll
    static void startMock() {
        mockServer = new WireMockServer(8888);
        mockServer.start();
        WireMock.configureFor(8888);
    }

    @AfterAll
    static void stopMock() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    @BeforeEach
    void setUp() {
        if (mockServer != null) {
            mockServer.resetAll();
        }
    }

    protected RequestSpecification baseRequest(String apiKey) {
        return RestAssured
                .given()
                .header("X-Api-Key", apiKey)
                .contentType("application/x-www-form-urlencoded");
    }

    public Response sendRequestWithMethod(String httpMethod, String token, ActionType action, String apiKey) {
        RequestSpecification request = baseRequest(apiKey)
                .formParam("token", token)
                .formParam("action", action.toString());

        Response response;
        String url = ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT;

        switch (httpMethod.toUpperCase()) {
            case "POST":
                response = request.when().post(url).then().extract().response();
                break;
            case "GET":
                response = RestAssured
                        .given()
                        .header("X-Api-Key", apiKey)
                        .when()
                        .get(url)
                        .then()
                        .extract()
                        .response();
                break;
            case "PUT":
                response = request.when().put(url).then().extract().response();
                break;
            case "DELETE":
                response = request.when().delete(url).then().extract().response();
                break;
            case "PATCH":
                response = request.when().patch(url).then().extract().response();
                break;
            case "OPTIONS":
                response = RestAssured
                        .given()
                        .header("X-Api-Key", apiKey)
                        .when()
                        .options(url)
                        .then()
                        .extract()
                        .response();
                break;
            case "HEAD":
                response = RestAssured
                        .given()
                        .header("X-Api-Key", apiKey)
                        .when()
                        .head(url)
                        .then()
                        .extract()
                        .response();
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + httpMethod);
        }

        return response;
    }

    public Response sendRequestWithMethod(String httpMethod, ActionType action) {
        return sendRequestWithMethod(httpMethod, TokenGenerator.generateValidToken(), action, ConnectionConfig.API_KEY);
    }

    public Response sendRequestWithMethod(String httpMethod, String token, ActionType action) {
        return sendRequestWithMethod(httpMethod, token, action, ConnectionConfig.API_KEY);
    }

    public Response sendRequestWithMethod(String httpMethod, ActionType action, String apiKey) {
        return sendRequestWithMethod(httpMethod, TokenGenerator.generateValidToken(), action, apiKey);
    }

    public Response sendRequest(ActionType action) {
        return sendRequestWithMethod("POST", action);
    }

    public Response sendRequest(String token, ActionType action) {
        return sendRequestWithMethod("POST", token, action);
    }

    public Response sendRequest(String token, ActionType action, String apiKey) {
        return sendRequestWithMethod("POST", token, action, apiKey);
    }
}