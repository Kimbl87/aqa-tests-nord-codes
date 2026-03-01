package com.test.tests;

import com.test.utils.TokenGenerator;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;
import com.test.config.ActionType;

import static org.junit.jupiter.api.Assertions.*;

public class ActionTests extends BaseTest {

    @Test
    public void testActionWithoutLogin() {
        String token = TokenGenerator.generateValidToken();
        Response response = sendRequest(token, ActionType.ACTION);

        assertEquals(403, response.statusCode(), "Статус должен быть 403");
        assertEquals("ERROR", response.jsonPath().getString("result"), "result должен быть ERROR");
        assertEquals(String.format("Token '%s' not found", token), response.jsonPath().getString("message"),
                String.format("Token '%s' not found", token));

        System.out.println("Статус: " + response.statusCode());
        System.out.println("Тело: " + response.asString());
    }

    @Test
    public void testActionAfterLogin() {
        mockAuthSuccess();
        mockActionSuccess();
        String token = TokenGenerator.generateValidToken();

        Response loginResponse = sendRequest(token, ActionType.LOGIN);
        assertEquals(200, loginResponse.statusCode());

        Response actionResponse = sendRequest(token, ActionType.ACTION);

        assertEquals(200, actionResponse.statusCode(), "Статус должен быть 200");
        assertEquals("OK", actionResponse.jsonPath().getString("result"), "result должен быть OK");
        assertNull(actionResponse.jsonPath().getString("message"), "message должен отсутсвтовать");

        System.out.println("Статус: " + actionResponse.statusCode());
        System.out.println("Тело: " + actionResponse.asString());
    }

    @Test
    public void testActionFailure() {
        mockAuthSuccess();
        mockActionFailure();

        String token = TokenGenerator.generateValidToken();

        Response loginResponse = sendRequest(token, ActionType.LOGIN);
        assertEquals(200, loginResponse.statusCode());

        Response actionResponse = sendRequest(token, ActionType.ACTION);

        assertEquals(500, actionResponse.statusCode(), "Статус должен быть 500");
        assertEquals("ERROR", actionResponse.jsonPath().getString("result"), "result должен быть ERROR");
        assertEquals("Internal Server Error", actionResponse.jsonPath().getString("message"),
                "message должен быть Internal Server Error");

        System.out.println("Статус: " + actionResponse.statusCode());
        System.out.println("Тело: " + actionResponse.asString());
    }
    @Test
    public void testActionAfterLogout() {
        mockAuthSuccess();
        String token = TokenGenerator.generateValidToken();

        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());
        assertEquals(200, sendRequest(token, ActionType.LOGOUT).statusCode());

        Response response = sendRequest(token, ActionType.ACTION);

        assertEquals(200, response.statusCode());
        assertEquals("ERROR", response.jsonPath().getString("result"));
        assertEquals("Session not found", response.jsonPath().getString("message"));
    }
}