package com.test.tests;

import org.junit.jupiter.api.Test;
import io.restassured.response.Response;
import com.test.config.ActionType;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTests extends BaseTest {

    //  cd C:\Data\Nord
//  C:\Data\Nord\java\bin\java -jar -Dsecret=qazWSXedc -Dmock=http://localhost:8888/ internal-0.0.1-SNAPSHOT.jar
    @Test
    public void testLoginSuccess() {
        mockAuthSuccess();
        Response response = sendRequest(ActionType.LOGIN);

        assertEquals(200, response.statusCode(), "Ожидаем ответ 200 Ок");
        assertEquals("OK", response.jsonPath().getString("result"), "result должен быть OK");
        assertNull(response.jsonPath().getString("message"), "message должен отсутствовать при успехе");

        System.out.println("Статус ответа: " + response.statusCode());
        System.out.println("Тело ответа: " + response.asString());
    }

    @Test
    public void testLoginFailure() {
        mockAuthFailure();
        Response response = sendRequest(ActionType.LOGIN);

        assertEquals(500, response.statusCode(), "Ожидаем ответ 500 Internal Server Error");//о ТЗ не написано, что ожиидаем, нужно ли проверять?
        assertEquals("ERROR", response.jsonPath().getString("result"), "result должен быть ERROR");
        assertEquals("Internal Server Error", response.jsonPath().getString("message"),
                "message должен быть Internal Server Error");

        System.out.println("Статус ответа: " + response.statusCode());
        System.out.println("Тело ответа: " + response.asString());
    }
}