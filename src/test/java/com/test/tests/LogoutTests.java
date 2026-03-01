package com.test.tests;

import com.test.utils.TokenGenerator;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;
import com.test.config.ActionType;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutTests extends BaseTest {

    @Test
    public void testLogoutWithoutLogin() {
        String token = TokenGenerator.generateValidToken();
        Response response = sendRequest(token, ActionType.LOGOUT);

        assertEquals(403, response.statusCode(), "Статус должен быть 200");
        assertEquals(String.format("Token '%s' not found", token), response.jsonPath().getString("message"),
                String.format("Token '%s' not found", token));

        System.out.println("Статус: " + response.statusCode());
        System.out.println("Тело: " + response.asString());
    }

    @Test
    public void testLogoutAfterLogin() {
        // Сначала логинимся
        String token = TokenGenerator.generateValidToken();

        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());

        Response logoutResponse = sendRequest(token, ActionType.LOGOUT);

        assertEquals(200, logoutResponse.statusCode(), "Статус должен быть 200");
        assertEquals("OK", logoutResponse.jsonPath().getString("result"), "result должен быть OK");
        assertNull(logoutResponse.jsonPath().getString("message"), "message должен отсутствовать");

        System.out.println("Статус: " + logoutResponse.statusCode());
        System.out.println("Тело: " + logoutResponse.asString());
    }

}