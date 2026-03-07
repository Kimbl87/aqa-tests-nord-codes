package com.test.assertions;

import com.test.attachments.CommonAttachments;
import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.*;

public class CoreAssertions {

    public static void verifyHostReachable(Response response) {
        int statusCode = response.statusCode();
        boolean isReachable = statusCode < 500;
        assertTrue(isReachable,
                String.format("Хост недоступен. Код ответа: %d", statusCode));
        CommonAttachments.attachVerificationResult("Доступность хоста",
                isReachable ? "Хост доступен" : "Хост недоступен");
    }

    public static void verifyPortCorrect(int expectedPort, String actualBaseUrl) {
        String expectedBaseUrl = "http://localhost:" + expectedPort;
        assertEquals(expectedBaseUrl, actualBaseUrl,
                String.format("Базовый URL должен быть '%s', но получен '%s'", expectedBaseUrl, actualBaseUrl));
        CommonAttachments.attachVerificationResult("Проверка порта",
                String.format("Порт %d используется корректно", expectedPort));
    }

    public static void verifyEndpointExists(Response response) {
        assertNotEquals(404, response.statusCode(),
                String.format("Эндпоинт не найден (404). Получен код: %d", response.statusCode()));
        CommonAttachments.attachVerificationResult("Существование эндпоинта",
                "Эндпоинт доступен, код: " + response.statusCode());
    }

    public static void verifyMethodAllowed(Response response, String method, int expectedCode) {
        assertEquals(expectedCode, response.statusCode(),
                String.format("Метод %s должен возвращать %d, но получен: %d",
                        method, expectedCode, response.statusCode()));
        CommonAttachments.attachVerificationResult("Метод " + method,
                String.format("Получен код %d", expectedCode));
    }

    public static void verifyContentType(Response response, String expectedContentType) {
        String actualContentType = response.getHeader("Content-Type");
        assertTrue(actualContentType != null && actualContentType.contains(expectedContentType),
                String.format("Content-Type должен содержать '%s', но получен '%s'",
                        expectedContentType, actualContentType));
        CommonAttachments.attachVerificationResult("Проверка Content-Type",
                "Content-Type: " + actualContentType);
    }

    public static void verifyApiKeyValid(Response response) {
        assertEquals(200, response.statusCode(),
                String.format("API ключ должен быть валидным, получен код: %d", response.statusCode()));
        CommonAssertions.verifyResultField(response, "OK");
        CommonAttachments.attachVerificationResult("Проверка API ключа",
                "Ключ валидный, получен код 200");
    }

    public static void verifyApiKeyError(Response response) {
        assertEquals(401, response.statusCode(),
                String.format("Неверный API ключ должен возвращать 401, получен: %d", response.statusCode()));
        CommonAssertions.verifyResultField(response, "ERROR");
        CommonAssertions.verifyMessageContains(response, "Missing or invalid API Key");
        CommonAttachments.attachVerificationResult("Проверка API ключа",
                "Получена ошибка 401 с корректным сообщением");
    }

    public static void verifyContentTypeError(Response response) {
        int statusCode = response.statusCode();
        boolean isValidError = statusCode == 400 || statusCode == 415;
        assertTrue(isValidError,
                String.format("Неверный Content-Type должен возвращать 400 или 415, получен: %d", statusCode));
        CommonAssertions.verifyResultField(response, "ERROR");
        CommonAttachments.attachVerificationResult("Проверка Content-Type",
                String.format("Получен код %d с ошибкой", statusCode));
    }

    public static void verifyDuplicateParameterError(Response response) {
        assertEquals(400, response.statusCode(),
                String.format("Дублирующиеся параметры должны возвращать 400, получен: %d", response.statusCode()));
        CommonAssertions.verifyResultField(response, "ERROR");
        CommonAttachments.attachVerificationResult("Проверка дублирования",
                "Получена ошибка 400");
    }

    public static void verifySuccessResponseStructure(Response response) {
        assertEquals(200, response.statusCode(),
                String.format("Ожидался код 200, получен: %d", response.statusCode()));
        String result = response.jsonPath().getString("result");
        assertNotNull(result, "В ответе должно быть поле result");
        assertEquals("OK", result, String.format("Поле result должно быть 'OK', получено: '%s'", result));
        Object message = response.jsonPath().get("message");
        assertNull(message, "При успехе поле message должно отсутствовать");
        CommonAttachments.attachVerificationResult("Структура успешного ответа",
                "Все поля присутствуют и корректны");
    }

    public static void verifyErrorResponseStructure(Response response) {
        String result = response.jsonPath().getString("result");
        assertNotNull(result, "В ответе должно быть поле result");
        String message = response.jsonPath().getString("message");
        assertNotNull(message, "В ответе должно быть поле message");
        CommonAttachments.attachVerificationResult("Структура ответа с ошибкой",
                String.format("Поля result и message присутствуют (result=%s, message=%s)", result, message));
    }

    public static void verifyEmptyRequestError(Response response) {
        assertEquals(400, response.statusCode(),
                String.format("Пустой запрос должен возвращать 400, получен: %d", response.statusCode()));
        CommonAssertions.verifyResultField(response, "ERROR");
        String message = response.jsonPath().getString("message");
        assertNotNull(message, "Должно быть сообщение об ошибке");
        assertFalse(message.isEmpty(), "Сообщение об ошибке не должно быть пустым");
        CommonAttachments.attachVerificationResult("Пустой запрос",
                String.format("Получена ошибка 400 с сообщением: '%s'", message));
    }
}