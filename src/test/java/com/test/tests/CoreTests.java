package com.test.tests;

import com.test.base.BaseTest;
import com.test.assertions.CoreAssertions;
import com.test.steps.CoreSteps;
import com.test.steps.CommonSteps;
import com.test.steps.LoginSteps;
import com.test.config.ConnectionConfig;
import com.test.utils.TokenGenerator;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        Response response = CoreSteps.checkHostAvailability();
        CoreAssertions.verifyHostReachable(response);
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
        CoreAssertions.verifyPortCorrect(port, ConnectionConfig.BASE_URL);
    }

    @Test
    @DisplayName("Существование эндпоинта")
    @Story("Позитивные сценарии: Core")
    @Description("Проверяем, что эндпоинт /endpoint существует")
    @Severity(SeverityLevel.BLOCKER)
    public void testEndpointExists() {
        String token = CommonSteps.generateToken("для проверки эндпоинта");
        Response response = CoreSteps.checkEndpointExists(token);
        CoreAssertions.verifyEndpointExists(response);
    }

    @Test
    @DisplayName("Метод POST")
    @Story("Позитивные сценарии: Core")
    @Description("Проверяем, что эндпоинт принимает POST запросы и возвращает 200")
    @Severity(SeverityLevel.CRITICAL)
    public void testPostMethod() {
        LoginSteps.mockAuthSuccess();
        String token = CommonSteps.generateToken("для проверки POST");
        Response response = CoreSteps.checkHttpMethod("POST", token);
        CoreAssertions.verifyMethodAllowed(response, "POST", 200);
    }

    @Test
    @DisplayName("Метод GET")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что эндпоинт НЕ принимает GET запросы")
    @Severity(SeverityLevel.NORMAL)
    public void testGetMethodNotAllowed() {
        Response response = CoreSteps.checkHttpMethod("GET", null);
        CoreAssertions.verifyMethodAllowed(response, "GET", 405);
    }

    @Test
    @DisplayName("Метод PUT")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что PUT метод не поддерживается")
    @Severity(SeverityLevel.NORMAL)
    public void testPutMethodNotAllowed() {
        String token = CommonSteps.generateToken("для проверки PUT");
        Response response = CoreSteps.checkHttpMethod("PUT", token);
        CoreAssertions.verifyMethodAllowed(response, "PUT", 405);
    }

    @Test
    @DisplayName("Метод DELETE")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что DELETE метод не поддерживается")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteMethodNotAllowed() {
        String token = CommonSteps.generateToken("для проверки DELETE");
        Response response = CoreSteps.checkHttpMethod("DELETE", token);
        CoreAssertions.verifyMethodAllowed(response, "DELETE", 405);
    }

    @Test
    @DisplayName("Метод PATCH")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что PATCH метод не поддерживается")
    @Severity(SeverityLevel.NORMAL)
    public void testPatchMethodNotAllowed() {
        String token = CommonSteps.generateToken("для проверки PATCH");
        Response response = CoreSteps.checkHttpMethod("PATCH", token);
        CoreAssertions.verifyMethodAllowed(response, "PATCH", 405);
    }

    @Test
    @DisplayName("Метод OPTIONS")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что OPTIONS метод не поддерживается")
    @Severity(SeverityLevel.NORMAL)
    public void testOptionsMethod() {
        Response response = CoreSteps.checkHttpMethod("OPTIONS", null);
        CoreAssertions.verifyMethodAllowed(response, "OPTIONS", 405);
    }

    @Test
    @DisplayName("Метод HEAD")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что HEAD метод не поддерживается")
    @Severity(SeverityLevel.NORMAL)
    public void testHeadMethod() {
        Response response = CoreSteps.checkHttpMethod("HEAD", null);
        CoreAssertions.verifyMethodAllowed(response, "HEAD", 405);
    }

    @Test
    @DisplayName("Accept заголовок (JSON)")
    @Story("Позитивные сценарии: Core")
    @Description("Проверяем, что приложение отдает JSON")
    @Severity(SeverityLevel.NORMAL)
    public void testAcceptHeader() {
        LoginSteps.mockAuthSuccess();
        String token = CommonSteps.generateToken("для проверки Accept");
        Response response = CoreSteps.checkAcceptHeader(token, "application/json");
        CoreAssertions.verifyContentType(response, "application/json");
    }

    @Test
    @DisplayName("Отсутствие Content-Type")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем поведение при отсутствии Content-Type")
    @Severity(SeverityLevel.NORMAL)
    public void testMissingContentType() {
        String token = CommonSteps.generateToken("для проверки Content-Type");
        Response response = CoreSteps.checkContentType(null, token);
        CoreAssertions.verifyContentTypeError(response);
    }

    @Test
    @DisplayName("Content-Type XML")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что XML не принимается")
    @Severity(SeverityLevel.NORMAL)
    public void testWrongContentTypeXml() {
        String token = CommonSteps.generateToken("для проверки XML");
        Response response = CoreSteps.checkContentType("application/xml", token);
        CoreAssertions.verifyContentTypeError(response);
    }

    @Test
    @DisplayName("Content-Type text/plain")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что text/plain не принимается")
    @Severity(SeverityLevel.NORMAL)
    public void testWrongContentTypeText() {
        String token = CommonSteps.generateToken("для проверки text/plain");
        Response response = CoreSteps.checkContentType("text/plain", token);
        CoreAssertions.verifyContentTypeError(response);
    }

    @Test
    @DisplayName("Accept заголовок (XML)")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что приложение не отдает XML")
    @Severity(SeverityLevel.NORMAL)
    public void testWrongAcceptHeader() {
        LoginSteps.mockAuthSuccess();
        String token = CommonSteps.generateToken("для проверки Accept XML");
        Response response = CoreSteps.checkAcceptHeader(token, "application/xml");
        assertEquals(406, response.statusCode(), "Accept XML должен возвращать 406");
    }

    @Test
    @DisplayName("X-Api-Key корректный")
    @Story("Позитивные сценарии: Core")
    @Description("Проверяем, что с правильным ключом запрос проходит")
    @Severity(SeverityLevel.CRITICAL)
    public void testValidApiKey() {
        LoginSteps.mockAuthSuccess();
        String token = CommonSteps.generateToken("для проверки API ключа");
        Response response = CoreSteps.checkApiKey(ConnectionConfig.API_KEY, token);
        CoreAssertions.verifyApiKeyValid(response);
    }

    @Test
    @DisplayName("X-Api-Key отсутствует")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что без API ключа запрос отклоняется")
    @Severity(SeverityLevel.CRITICAL)
    public void testMissingApiKey() {
        String token = CommonSteps.generateToken("для проверки отсутствия ключа");
        Response response = CoreSteps.checkApiKey(null, token);
        CoreAssertions.verifyApiKeyError(response);
    }

    @Test
    @DisplayName("X-Api-Key пустой")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что с пустым API ключом запрос отклоняется")
    @Severity(SeverityLevel.CRITICAL)
    public void testEmptyApiKey() {
        String token = CommonSteps.generateToken("для проверки пустого ключа");
        Response response = CoreSteps.checkApiKey("", token);
        CoreAssertions.verifyApiKeyError(response);
    }

    @Test
    @DisplayName("X-Api-Key неверный (похожий)")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что ключ, похожий на правильный, но не совпадающий, не принимается")
    @Severity(SeverityLevel.CRITICAL)
    public void testInvalidApiKey() {
        String token = CommonSteps.generateToken("для проверки неверного ключа");
        Response response = CoreSteps.checkApiKey("qazWSXedc1", token);
        CoreAssertions.verifyApiKeyError(response);
    }

    @Test
    @DisplayName("X-Api-Key с другим регистром")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что ключ чувствителен к регистру")
    @Severity(SeverityLevel.NORMAL)
    public void testApiKeyCaseSensitive() {
        String token = CommonSteps.generateToken("для проверки регистра");
        Response response = CoreSteps.checkApiKey("QAZWSXEDC", token);
        CoreAssertions.verifyApiKeyError(response);
    }

    @Test
    @DisplayName("Дублирование заголовка X-Api-Key")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что дублирующиеся заголовки вызывают ошибку 400")
    @Severity(SeverityLevel.NORMAL)
    public void testDuplicateApiKeyHeader() {
        String token = CommonSteps.generateToken("для проверки дублирования");
        Response response = CoreSteps.checkDuplicateApiKeyHeader(token);
        CoreAssertions.verifyDuplicateParameterError(response);
    }

    @Test
    @DisplayName("Дублирование параметра action")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что дублирующиеся параметры action вызывают ошибку 400")
    @Severity(SeverityLevel.NORMAL)
    public void testDuplicateActionParameter() {
        String token = CommonSteps.generateToken("для проверки дублирования action");
        Response response = CoreSteps.checkDuplicateActionParameter(token);
        CoreAssertions.verifyDuplicateParameterError(response);
    }

    @Test
    @DisplayName("Дублирование параметра token")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что дублирующиеся параметры token вызывают ошибку 400")
    @Severity(SeverityLevel.NORMAL)
    public void testDuplicateTokenParameter() {
        String token = CommonSteps.generateToken("основной");
        String duplicateToken = CommonSteps.generateToken("дубликат");
        Response response = CoreSteps.checkDuplicateTokenParameter(token, duplicateToken);
        CoreAssertions.verifyDuplicateParameterError(response);
    }

    @Test
    @DisplayName("Структура успешного ответа")
    @Story("Позитивные сценарии: Core")
    @Description("Проверяем, что успешный ответ содержит поле result и не содержит message")
    @Severity(SeverityLevel.NORMAL)
    public void testSuccessResponseStructure() {
        LoginSteps.mockAuthSuccess();
        String token = CommonSteps.generateToken("для проверки структуры");
        Response response = LoginSteps.sendFirstLogin(token);
        CoreAssertions.verifySuccessResponseStructure(response);
    }

    @Test
    @DisplayName("Структура ответа с ошибкой")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что ответ об ошибке содержит поля result и message")
    @Severity(SeverityLevel.NORMAL)
    public void testErrorResponseStructure() {
        String token = CommonSteps.generateToken("для проверки ошибки");
        Response response = CoreSteps.checkApiKey("wrong-key", token);
        CoreAssertions.verifyErrorResponseStructure(response);
    }

    @Test
    @DisplayName("Время ответа")
    @Story("Позитивные сценарии: Core")
    @Description("Проверяем, что приложение отвечает в разумное время")
    @Severity(SeverityLevel.NORMAL)
    public void testResponseTime() {
        LoginSteps.mockAuthSuccess();
        String token = CommonSteps.generateToken("для проверки времени");
        long maxTime = 5000;
        CoreSteps.checkResponseTime(token, maxTime);
    }

    @Test
    @DisplayName("Пустой запрос")
    @Story("Негативные сценарии: Core")
    @Description("Проверяем, что пустой POST запрос обрабатывается")
    @Severity(SeverityLevel.NORMAL)
    public void testEmptyRequest() {
        Response response = CoreSteps.checkEmptyRequest();
        CoreAssertions.verifyEmptyRequestError(response);
    }
}