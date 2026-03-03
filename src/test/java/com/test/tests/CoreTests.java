package com.test.tests;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import com.test.config.ConnectionConfig;
import com.test.config.ActionType;
import com.test.utils.TokenGenerator;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Инфраструктура")
@Feature("Core")
@DisplayName("Тесты доступности и базовых настроек")
public class CoreTests extends BaseTest {

    @Test
    @DisplayName("Доступность хоста")
    @Story("Позитивные сценарии: Core")
    @Description("Проверяем, что хост приложения доступен по сети")
    @Severity(SeverityLevel.BLOCKER)
    public void testHostReachable() {
        Response response = RestAssured
                .given()
                .get(ConnectionConfig.BASE_URL);

        assertTrue(response.statusCode() < 500,
                "Хост " + ConnectionConfig.BASE_URL + " недоступен. Код ответа: " + response.statusCode());
    }

    @Test
    @DisplayName("Правильность порта")
    @Story("Позитивные сценарии: Core")
    @Description("Проверяем, что приложение слушает правильный порт")
    @Severity(SeverityLevel.BLOCKER)
    public void testCorrectPort() {
        int port = 8080;
        String expectedBaseUrl = "http://localhost:" + port;

        assertEquals(expectedBaseUrl, ConnectionConfig.BASE_URL,
                "Базовый URL должен использовать порт " + port);
    }

    @Test
    @DisplayName("Существование эндпоинта")
    @Story("Позитивные сценарии: Core")
    @Description("Проверяем, что эндпоинт /endpoint существует")
    @Severity(SeverityLevel.BLOCKER)
    public void testEndpointExists() {
        String token = TokenGenerator.generateValidToken();
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertNotEquals(404, response.statusCode(),
                "Эндпоинт " + ConnectionConfig.ENDPOINT + " не найден. Код: " + response.statusCode());
    }

    @Test
    @DisplayName("Метод POST")
    @Story("Позитивные сценарии: Core")
    @Description("Проверяем, что эндпоинт принимает POST запросы и возвращает 200")
    @Severity(SeverityLevel.CRITICAL)
    public void testPostMethod() {
        mockAuthSuccess();

        String token = TokenGenerator.generateValidToken();
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(200, response.statusCode(),
                "Метод POST должен возвращать 200 OK. Получен: " + response.statusCode());
        assertEquals("OK", response.jsonPath().getString("result"),
                "result должен быть OK");
    }

    @Test
    @DisplayName("Метод GET")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что эндпоинт НЕ принимает GET запросы")
    @Severity(SeverityLevel.NORMAL)
    public void testGetMethodNotAllowed() {
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .get(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(405, response.statusCode(),
                "Метод GET должен возвращать 405 Method Not Allowed. Получен: " + response.statusCode());
    }

    @Test
    @DisplayName("Метод PUT")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что PUT метод не поддерживается")
    @Severity(SeverityLevel.NORMAL)
    public void testPutMethodNotAllowed() {
        String token = TokenGenerator.generateValidToken();
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .put(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(405, response.statusCode(),
                "Метод PUT должен возвращать 405 Method Not Allowed. Получен: " + response.statusCode());
    }

    @Test
    @DisplayName("Метод DELETE")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что DELETE метод не поддерживается")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteMethodNotAllowed() {
        String token = TokenGenerator.generateValidToken();
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .delete(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(405, response.statusCode(),
                "Метод DELETE должен возвращать 405 Method Not Allowed. Получен: " + response.statusCode());
    }

    @Test
    @DisplayName("Метод PATCH")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что PATCH метод не поддерживается")
    @Severity(SeverityLevel.NORMAL)
    public void testPatchMethodNotAllowed() {
        String token = TokenGenerator.generateValidToken();
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .patch(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(405, response.statusCode(),
                "Метод PATCH должен возвращать 405 Method Not Allowed. Получен: " + response.statusCode());
    }

    @Test
    @DisplayName("Метод OPTIONS")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что OPTIONS метод не поддерживается")
    @Severity(SeverityLevel.NORMAL)
    public void testOptionsMethod() {
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .options(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(405, response.statusCode(),
                "Метод OPTIONS должен возвращать 405 Method Not Allowed. Получен: " + response.statusCode());
    }

    @Test
    @DisplayName("Метод HEAD")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что HEAD метод не поддерживается")
    @Severity(SeverityLevel.NORMAL)
    public void testHeadMethod() {
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .head(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(405, response.statusCode(),
                "Метод HEAD должен возвращать 405 Method Not Allowed. Получен: " + response.statusCode());
    }

    @Test
    @DisplayName("Accept заголовок (JSON)")
    @Story("Позитивные сценарии: Core")
    @Description("Проверяем, что приложение отдает JSON")
    @Severity(SeverityLevel.NORMAL)
    public void testAcceptHeader() {
        String token = TokenGenerator.generateValidToken();
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .accept("application/json")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        String contentType = response.getHeader("Content-Type");
        assertEquals("application/json", contentType);
    }

    @Test
    @DisplayName("Отсутствие Content-Type")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем поведение при отсутствии Content-Type")
    @Severity(SeverityLevel.NORMAL)
    public void testMissingContentType() {
        String token = TokenGenerator.generateValidToken();
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertTrue(response.statusCode() == 400 || response.statusCode() == 415,
                "Без Content-Type должен быть 400 или 415. Получен: " + response.statusCode());
    }

    @Test
    @DisplayName("Content-Type XML")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что XML не принимается")
    @Severity(SeverityLevel.NORMAL)
    public void testWrongContentTypeXml() {
        String token = TokenGenerator.generateValidToken();
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/xml")
                .body("<request><token>" + token + "</token><action>LOGIN</action></request>")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertTrue(response.statusCode() == 400 || response.statusCode() == 415,
                "XML Content-Type должен возвращать 400 или 415. Получен: " + response.statusCode());
    }

    @Test
    @DisplayName("Content-Type text/plain")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что text/plain не принимается")
    @Severity(SeverityLevel.NORMAL)
    public void testWrongContentTypeText() {
        String token = TokenGenerator.generateValidToken();
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("text/plain")
                .body("token=" + token + "&action=LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertTrue(response.statusCode() == 400 || response.statusCode() == 415,
                "text/plain Content-Type должен возвращать 400 или 415. Получен: " + response.statusCode());
    }

    @Test
    @DisplayName("Accept заголовок (XML)")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что приложение не отдает XML")
    @Severity(SeverityLevel.NORMAL)
    public void testWrongAcceptHeader() {
        String token = TokenGenerator.generateValidToken();
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .accept("application/xml")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(406, response.statusCode(),
                "Accept XML должен возвращать 406 Not Acceptable. Получен: " + response.statusCode());
    }

    @Test
    @DisplayName("X-Api-Key корректный")
    @Story("Позитивные сценарии: Core")
    @Description("Проверяем, что с правильным ключом запрос проходит")
    @Severity(SeverityLevel.CRITICAL)
    public void testValidApiKey() {
        mockAuthSuccess();
        String token = TokenGenerator.generateValidToken();
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(200, response.statusCode(),
                "С правильным API ключом должен быть 200. Получен: " + response.statusCode());
    }

    @Test
    @DisplayName("X-Api-Key отсутствует")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что без API ключа запрос отклоняется")
    @Severity(SeverityLevel.CRITICAL)
    public void testMissingApiKey() {
        String token = TokenGenerator.generateValidToken();
        Response response = RestAssured
                .given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(401, response.statusCode(),
                "Без API ключа должен быть 401. Получен: " + response.statusCode());
        assertEquals("ERROR", response.jsonPath().getString("result"));
        assertTrue(
                response.jsonPath().getString("message").contains("Missing or invalid API Key"),
                "Сообщение должно содержать 'Missing or invalid API Key'. Получено: " + response.jsonPath().getString("message")
        );
    }

    @Test
    @DisplayName("X-Api-Key пустой")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что с пустым API ключом запрос отклоняется")
    @Severity(SeverityLevel.CRITICAL)
    public void testEmptyApiKey() {
        String token = TokenGenerator.generateValidToken();
        Response response = RestAssured
                .given()
                .header("X-Api-Key", "")
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(401, response.statusCode(),
                "С пустым API ключом должен быть 401. Получен: " + response.statusCode());
        assertEquals("ERROR", response.jsonPath().getString("result"));
        assertTrue(
                response.jsonPath().getString("message").contains("Missing or invalid API Key"),
                "Сообщение должно содержать 'Missing or invalid API Key'. Получено: " + response.jsonPath().getString("message")
        );
    }

    @Test
    @DisplayName("X-Api-Key неверный (похожий)")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что ключ, похожий на правильный, но не совпадающий, не принимается")
    @Severity(SeverityLevel.CRITICAL)
    public void testInvalidApiKey() {
        String token = TokenGenerator.generateValidToken();
        Response response = RestAssured
                .given()
                .header("X-Api-Key", "qazWSXedc1")
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(401, response.statusCode(),
                "С неверным API ключом должен быть 401. Получен: " + response.statusCode());
        assertEquals("ERROR", response.jsonPath().getString("result"));
        assertTrue(
                response.jsonPath().getString("message").contains("Missing or invalid API Key"),
                "Сообщение должно содержать 'Missing or invalid API Key'. Получено: " + response.jsonPath().getString("message")
        );
    }

    @Test
    @DisplayName("X-Api-Key с другим регистром")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что ключ чувствителен к регистру")
    @Severity(SeverityLevel.NORMAL)
    public void testApiKeyCaseSensitive() {
        String token = TokenGenerator.generateValidToken();
        Response response = RestAssured
                .given()
                .header("X-Api-Key", "QAZWSXEDC")
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(401, response.statusCode(),
                "Ключ в другом регистре должен давать 401. Получен: " + response.statusCode());
        assertEquals("ERROR", response.jsonPath().getString("result"));
    }

    @Test
    @DisplayName("Дублирование заголовка X-Api-Key")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что дублирующиеся заголовки вызывают ошибку 400")
    @Severity(SeverityLevel.NORMAL)
    public void testDuplicateApiKeyHeader() {
        String token = TokenGenerator.generateValidToken();

        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .header("X-Api-Key", "another-key")
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(400, response.statusCode());
        assertEquals("ERROR", response.jsonPath().getString("result"));
    }

    @Test
    @DisplayName("Дублирование параметра action")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что дублирующиеся параметры action вызывают ошибку 400")
    @Severity(SeverityLevel.NORMAL)
    public void testDuplicateActionParameter() {
        String token = TokenGenerator.generateValidToken();

        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .formParam("action", "ACTION")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(400, response.statusCode());
        assertEquals("ERROR", response.jsonPath().getString("result"));
    }

    @Test
    @DisplayName("Дублирование параметра token")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что дублирующиеся параметры token вызывают ошибку 400")
    @Severity(SeverityLevel.NORMAL)
    public void testDuplicateTokenParameter() {
        String token = TokenGenerator.generateValidToken();
        String duplicateToken = TokenGenerator.generateValidToken();

        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("token", duplicateToken)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(400, response.statusCode());
        assertEquals("ERROR", response.jsonPath().getString("result"));
    }

    @Test
    @DisplayName("Структура успешного ответа")
    @Story("Позитивные сценарии: Core")
    @Description("Проверяем, что успешный ответ содержит поле result и не содержит message")
    @Severity(SeverityLevel.NORMAL)
    public void testSuccessResponseStructure() {
        mockAuthSuccess();

        String token = TokenGenerator.generateValidToken();
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().getString("result"),
                "В ответе должно быть поле result. Получено: " + response.asString());
        assertEquals("OK", response.jsonPath().getString("result"));
        assertNull(response.jsonPath().getString("message"),
                "При успехе поле message должно отсутствовать. Получено: " + response.asString());
    }

    @Test
    @DisplayName("Структура ответа с ошибкой")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что ответ об ошибке содержит поля result и message")
    @Severity(SeverityLevel.NORMAL)
    public void testErrorResponseStructure() {
        String token = TokenGenerator.generateValidToken();
        Response response = RestAssured
                .given()
                .header("X-Api-Key", "wrong-key")
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(401, response.statusCode(),
                "С неверным API ключом должен быть 401. Получен: " + response.statusCode());

        assertNotNull(response.jsonPath().getString("result"),
                "В ответе должно быть поле result. Получено: " + response.asString());
        assertNotNull(response.jsonPath().getString("message"),
                "В ответе должно быть поле message. Получено: " + response.asString());
    }

    @Test
    @DisplayName("Время ответа")
    @Story("Позитивные сценарии: Core")
    @Description("Проверяем, что приложение отвечает в разумное время")
    @Severity(SeverityLevel.NORMAL)
    public void testResponseTime() {
        mockAuthSuccess();
        String token = TokenGenerator.generateValidToken();

        long startTime = System.currentTimeMillis();

        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        assertTrue(responseTime < 5000,
                "Время ответа превышает 5 секунд: " + responseTime + "ms");
        assertEquals(200, response.statusCode(),
                "Сервер должен вернуть 200. Получен: " + response.statusCode());
    }

    @Test
    @DisplayName("Пустой запрос")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что пустой POST запрос обрабатывается")
    @Severity(SeverityLevel.NORMAL)
    public void testEmptyRequest() {
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(400, response.statusCode(),
                "Пустой запрос должен возвращать 400. Получен: " + response.statusCode());
        assertEquals("ERROR", response.jsonPath().getString("result"));
        assertNotNull(response.jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Запрос с лишними параметрами")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что лишние параметры игнорируются или вызывают ошибку")
    @Severity(SeverityLevel.MINOR)
    public void testExtraParameters() {
        String token = TokenGenerator.generateValidToken();
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .formParam("extra1", "value1")
                .formParam("extra2", "value2")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertTrue(response.statusCode() == 400,
                "Лишние параметры должны вызывать ошибку (400). Получен: " + response.statusCode());
    }

    @Test
    @DisplayName("Запрос с дублирующимися параметрами")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем обработку дублирующихся параметров")
    @Severity(SeverityLevel.MINOR)
    public void testDuplicateParameters() {
        String token = TokenGenerator.generateValidToken();
        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("token", token + "1")
                .formParam("action", "LOGIN")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertTrue(response.statusCode() == 200 || response.statusCode() == 400,
                "Дублирующиеся параметры должны либо обрабатываться (200), либо вызывать ошибку (400). Получен: " + response.statusCode());
    }
}