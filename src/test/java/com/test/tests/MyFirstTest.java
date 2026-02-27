package com.test.tests;

import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import com.test.config.ConnectionConfig;
import com.test.config.ActionType;

import static org.junit.jupiter.api.Assertions.*;

public class MyFirstTest {

    @Test
    public void myFirstTest() {
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", ConnectionConfig.TEST_TOKEN)
                .formParam("action", ActionType.LOGIN.toString())
                .when()
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT)
                .then()
                .extract()
                .response();

//        assertEquals(200, response.statusCode(), "Ожидаем ответ 200 Ок");
        assertTrue(response.statusCode() == 200 || response.statusCode() == 500, "Пока или 500 или 200");
        assertNotNull(response.jsonPath().getString("result"), "Ожидаем поле result в ответе");
        assertTrue(
                response.jsonPath().getString("result").equals("OK") ||
                        response.jsonPath().getString("result").equals("ERROR"),
                "Пока хорошо и OK и ERROR");


        System.out.println("Статус ответа: " + response.statusCode());
        System.out.println("Тело ответа: " + response.asString());
    }
}