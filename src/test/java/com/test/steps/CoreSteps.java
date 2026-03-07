package com.test.steps;

import com.test.attachments.CommonAttachments;
import com.test.base.BaseTest;
import com.test.config.ActionType;
import com.test.config.ConnectionConfig;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CoreSteps {

    private static BaseTest baseTest = new BaseTest();

    @Step("Проверка доступности хоста")
    public static Response checkHostAvailability() {
        Response response = RestAssured
                .given()
                .get(ConnectionConfig.BASE_URL);
        CommonAttachments.attachRequestResponseDetails("GET",
                ConnectionConfig.BASE_URL, null, null, null, response,
                "ПРОВЕРКА ДОСТУПНОСТИ ХОСТА");
        return response;
    }

    @Step("Проверка существования эндпоинта")
    public static Response checkEndpointExists(String token) {
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);
        CommonAttachments.attachRequestResponseDetails("POST",
                ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT,
                token, ActionType.LOGIN, ConnectionConfig.API_KEY, response,
                "ПРОВЕРКА СУЩЕСТВОВАНИЯ ЭНДПОИНТА");
        return response;
    }

    @Step("Проверка метода {method}")
    public static Response checkHttpMethod(String method, String token) {
        Response response = null;
        String url = ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT;
        switch (method.toUpperCase()) {
            case "GET":
                response = RestAssured.given().header("X-Api-Key", ConnectionConfig.API_KEY).get(url);
                break;
            case "POST":
                response = RestAssured.given().header("X-Api-Key", ConnectionConfig.API_KEY)
                        .contentType("application/x-www-form-urlencoded")
                        .formParam("token", token).formParam("action", "LOGIN").post(url);
                break;
            case "PUT":
                response = RestAssured.given().header("X-Api-Key", ConnectionConfig.API_KEY)
                        .contentType("application/x-www-form-urlencoded")
                        .formParam("token", token).formParam("action", "LOGIN").put(url);
                break;
            case "DELETE":
                response = RestAssured.given().header("X-Api-Key", ConnectionConfig.API_KEY)
                        .contentType("application/x-www-form-urlencoded")
                        .formParam("token", token).formParam("action", "LOGIN").delete(url);
                break;
            case "PATCH":
                response = RestAssured.given().header("X-Api-Key", ConnectionConfig.API_KEY)
                        .contentType("application/x-www-form-urlencoded")
                        .formParam("token", token).formParam("action", "LOGIN").patch(url);
                break;
            case "OPTIONS":
                response = RestAssured.given().header("X-Api-Key", ConnectionConfig.API_KEY).options(url);
                break;
            case "HEAD":
                response = RestAssured.given().header("X-Api-Key", ConnectionConfig.API_KEY).head(url);
                break;
        }
        CommonAttachments.attachRequestResponseDetails(method, url,
                token, ActionType.LOGIN, ConnectionConfig.API_KEY, response,
                "ПРОВЕРКА МЕТОДА " + method);
        return response;
    }

    @Step("Проверка Accept заголовка")
    public static Response checkAcceptHeader(String token, String acceptType) {
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .accept(acceptType)
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);
        CommonAttachments.attachRequestResponseDetails("POST",
                ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT,
                token, ActionType.LOGIN, ConnectionConfig.API_KEY, response,
                "ПРОВЕРКА ACCEPT: " + acceptType);
        return response;
    }

    @Step("Проверка API ключа")
    public static Response checkApiKey(String apiKey, String token) {
        String keyToUse = apiKey != null ? apiKey : "";
        Response response = RestAssured
                .given()
                .header("X-Api-Key", keyToUse)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);
        CommonAttachments.attachRequestResponseDetails("POST",
                ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT,
                token, ActionType.LOGIN, keyToUse, response,
                "ПРОВЕРКА API КЛЮЧА: " + (apiKey == null ? "отсутствует" : apiKey));
        return response;
    }

    @Step("Проверка Content-Type")
    public static Response checkContentType(String contentType, String token) {
        Response response;
        String url = ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT;
        if (contentType == null) {
            response = RestAssured
                    .given()
                    .header("X-Api-Key", ConnectionConfig.API_KEY)
                    .formParam("token", token)
                    .formParam("action", "LOGIN")
                    .post(url);
        } else {
            response = RestAssured
                    .given()
                    .header("X-Api-Key", ConnectionConfig.API_KEY)
                    .contentType(contentType)
                    .body("token=" + token + "&action=LOGIN")
                    .post(url);
        }
        CommonAttachments.attachRequestResponseDetails("POST", url,
                token, ActionType.LOGIN, ConnectionConfig.API_KEY, response,
                "ПРОВЕРКА CONTENT-TYPE: " + (contentType == null ? "отсутствует" : contentType));
        return response;
    }

    @Step("Проверка дублирования заголовка X-Api-Key")
    public static Response checkDuplicateApiKeyHeader(String token) {
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .header("X-Api-Key", "another-key")
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);
        CommonAttachments.attachRequestResponseDetails("POST",
                ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT,
                token, ActionType.LOGIN, ConnectionConfig.API_KEY, response,
                "ПРОВЕРКА ДУБЛИРОВАНИЯ API KEY");
        return response;
    }

    @Step("Проверка дублирования параметра action")
    public static Response checkDuplicateActionParameter(String token) {
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .formParam("action", "ACTION")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);
        CommonAttachments.attachRequestResponseDetails("POST",
                ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT,
                token, ActionType.LOGIN, ConnectionConfig.API_KEY, response,
                "ПРОВЕРКА ДУБЛИРОВАНИЯ ACTION");
        return response;
    }

    @Step("Проверка дублирования параметра token")
    public static Response checkDuplicateTokenParameter(String token1, String token2) {
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token1)
                .formParam("token", token2)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);
        CommonAttachments.attachRequestResponseDetails("POST",
                ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT,
                token1, ActionType.LOGIN, ConnectionConfig.API_KEY, response,
                "ПРОВЕРКА ДУБЛИРОВАНИЯ TOKEN", "token(дубликат)", token2);
        return response;
    }

    @Step("Проверка пустого запроса")
    public static Response checkEmptyRequest() {
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);
        CommonAttachments.attachRequestResponseDetails("POST",
                ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT,
                null, null, ConnectionConfig.API_KEY, response,
                "ПУСТОЙ ЗАПРОС");
        return response;
    }

    @Step("Проверка времени ответа (макс. {maxTime} мс)")
    public static void checkResponseTime(String token, long maxTime) {
        long startTime = System.currentTimeMillis();
        Response response = baseTest.sendRequest(token, ActionType.LOGIN);
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        CommonAttachments.attachRequestResponseDetails("POST",
                ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT,
                token, ActionType.LOGIN, ConnectionConfig.API_KEY, response,
                "ПРОВЕРКА ВРЕМЕНИ ОТВЕТА");
        assertTrue(responseTime < maxTime,
                String.format("Время ответа (%d мс) превышает лимит (%d мс)", responseTime, maxTime));
        CommonAttachments.attachInfo(String.format("Время ответа: %d мс", responseTime));
    }
}