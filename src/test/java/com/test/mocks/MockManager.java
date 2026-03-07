package com.test.mocks;

import com.github.tomakehurst.wiremock.client.WireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class MockManager {

    public static void mockAuthSuccess() {
        stubFor(post(urlEqualTo("/auth"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"success\"}")));
    }

    public static void mockAuthError(int statusCode, String errorBody) {
        stubFor(post(urlEqualTo("/auth"))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withBody(errorBody)));
    }

    public static void mockAuthFailure() {
        stubFor(post(urlEqualTo("/auth"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"unauthorized\"}")));
    }

    public static void mockAuthTimeout() {
        stubFor(post(urlEqualTo("/auth"))
                .willReturn(aResponse()
                        .withFixedDelay(10000)
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"success\"}")));
    }

    public static void mockActionSuccess() {
        stubFor(post(urlEqualTo("/doAction"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"done\"}")));
    }

    public static void mockActionError(int statusCode, String errorBody) {
        stubFor(post(urlEqualTo("/doAction"))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withBody(errorBody)));
    }

    public static void mockActionFailure() {
        stubFor(post(urlEqualTo("/doAction"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"internal error\"}")));
    }

    public static void mockActionTimeout() {
        stubFor(post(urlEqualTo("/doAction"))
                .willReturn(aResponse()
                        .withFixedDelay(10000)
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"done\"}")));
    }

    public static void mockEmptyResponse(String endpoint) {
        stubFor(post(urlEqualTo(endpoint))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("")));
    }

    public static void mockInvalidJson(String endpoint) {
        stubFor(post(urlEqualTo(endpoint))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{invalid: json}")));
    }

    public static void resetMocks() {
        WireMock.reset();
    }
}