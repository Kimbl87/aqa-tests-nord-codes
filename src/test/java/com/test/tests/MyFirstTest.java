package com.test.tests;

import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class MyFirstTest {

    @Test
    public void myFirstTest() {
        Response response = RestAssured
                .given()
                    .header("X-Api-Key", "qazWSXedc")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("token", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                    .formParam("action", "LOGIN")
                .when()
                    .post("http://localhost:8080/endpoint")
                .then()
                    .extract()
                    .response();

        System.out.println("Статус ответа: " + response.statusCode());
        System.out.println("Тело ответа: " + response.asString());
    }
}