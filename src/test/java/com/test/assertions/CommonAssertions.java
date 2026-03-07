package com.test.assertions;

import io.restassured.response.Response;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommonAssertions {

    public static void verifyStatusCode(Response response, int expectedCode, String message) {
        int actualStatus = response.statusCode();
        assertEquals(expectedCode, actualStatus,
                String.format("%s. Ожидался код %d, но получен: %d", message, expectedCode, actualStatus));
    }

    public static void verifyResultField(Response response, String expectedValue) {
        String actualResult = response.jsonPath().getString("result");
        assertEquals(expectedValue, actualResult,
                String.format("Поле result должно быть '%s', но получено: '%s'", expectedValue, actualResult));
    }

    public static void verifyMessageField(Response response, String expectedMessage) {
        String actualMessage = response.jsonPath().getString("message");
        assertEquals(expectedMessage, actualMessage,
                String.format("Поле message должно быть '%s', но получено: '%s'", expectedMessage, actualMessage));
    }

    public static void verifyMessageContains(Response response, String expectedSubstring) {
        String actualMessage = response.jsonPath().getString("message");
        assertTrue(actualMessage.contains(expectedSubstring),
                String.format("Сообщение должно содержать '%s', но получено: '%s'", expectedSubstring, actualMessage));
    }

    public static void verifyMessageAbsent(Response response) {
        Object actualMessage = response.jsonPath().get("message");
        assertNull(actualMessage,
                String.format("Поле message должно отсутствовать, но получено: '%s'", actualMessage));
    }

    public static void verifyAllSuccess(List<Response> responses) {
        for (int i = 0; i < responses.size(); i++) {
            Response response = responses.get(i);
            verifyStatusCode(response, 200, String.format("Запрос #%d", i + 1));
            verifyResultField(response, "OK");
            verifyMessageAbsent(response);
        }
    }
}