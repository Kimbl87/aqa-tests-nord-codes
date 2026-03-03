package com.test.tests;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.test.config.ConnectionConfig;
import com.test.utils.TokenGenerator;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import io.restassured.response.Response;
import com.test.config.ActionType;
import io.qameta.allure.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Epic("Авторизация")
@Feature("LOGIN")
@DisplayName("Тесты входа в систему")
public class LoginTests extends BaseTest {

    @Test
    @DisplayName("Успешный вход в систему")
    @Story("Позитивные сценарии LOGIN")
    @Description("Проверяем, что при успешном ответе от /auth возвращается 200 OK")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginSuccess() {
        mockAuthSuccess();
        Response response = sendRequest(ActionType.LOGIN);

        assertEquals(200, response.statusCode(), "Ожидаем ответ 200 Ок");
        assertEquals("OK", response.jsonPath().getString("result"), "result должен быть OK");
        assertNull(response.jsonPath().getString("message"), "message должен отсутствовать при успехе");
    }

    @ParameterizedTest(name = "Ошибка авторизации: внешний сервис отвечает {0}")
    @MethodSource("com.test.parameterized.AuthErrorCodesProvider#provideAuthErrorCodes")
    @DisplayName("Разные коды ошибок внешнего сервиса")
    @Story("Негативные сценарии LOGIN")
    @Description("Проверяем, что при разных ошибках от /auth возвращается 500 ERROR")
    @Severity(SeverityLevel.NORMAL)
    public void testAuthErrorCodes(int statusCode, String errorBody) {
        stubFor(post(urlEqualTo("/auth"))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withBody(errorBody)));

        Response response = sendRequest(ActionType.LOGIN);

        assertEquals(500, response.statusCode(),
                "При ошибке внешнего сервиса должен быть 500. Получен: " + response.statusCode());
        assertEquals("ERROR", response.jsonPath().getString("result"),
                "result должен быть ERROR");
        assertEquals("Internal Server Error", response.jsonPath().getString("message"),
                "message должен быть Internal Server Error");

    }

    @Test
    @DisplayName("Таймаут внешнего сервиса")
    @Story("Негативные сценарии LOGIN")
    @Description("Проверяем, что при таймауте от /auth возвращается 500 ERROR")
    @Severity(SeverityLevel.NORMAL)
    public void testAuthTimeout() {
        stubFor(post(urlEqualTo("/auth"))
                .willReturn(aResponse()
                        .withFixedDelay(10000)
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"success\"}")));

        Response response = sendRequest(ActionType.LOGIN);

        assertEquals(500, response.statusCode(),
                "При таймауте должен быть 500. Получен: " + response.statusCode());
        assertEquals("ERROR", response.jsonPath().getString("result"),
                "result должен быть ERROR");
        assertEquals("Internal Server Error", response.jsonPath().getString("message"),
                "message должен быть Internal Server Error");
    }

    @Test
    @DisplayName("Внешний сервис недоступен")
    @Story("Негативные сценарии LOGIN")
    @Description("Проверяем, что при недоступном внешнем сервисе возвращается 500 ERROR")
    @Severity(SeverityLevel.NORMAL)
    public void testAuthServiceUnavailable() {
        mockServer.stop();

        try {
            Response response = sendRequest(ActionType.LOGIN);

            assertEquals(500, response.statusCode(),
                    "При недоступном сервисе должен быть 500. Получен: " + response.statusCode());
            assertEquals("ERROR", response.jsonPath().getString("result"),
                    "result должен быть ERROR");
            assertEquals("Internal Server Error", response.jsonPath().getString("message"),
                    "message должен быть Internal Server Error");
        } finally {
            mockServer.start();
            WireMock.configureFor(8888);
        }
    }

    @Test
    @DisplayName("Повторный LOGIN с тем же токеном сразу после успешного")
    @Story("Негативные сценарии LOGIN")
    @Description("Проверяем, что при повторном логине с тем же токеном возвращается 409 Conflict")
    @Severity(SeverityLevel.NORMAL)
    public void testRepeatedLoginWithSameToken() {
        mockAuthSuccess();
        String token = TokenGenerator.generateValidToken();

        Response firstResponse = sendRequest(token, ActionType.LOGIN);
        assertEquals(200, firstResponse.statusCode(), "Первый логин должен быть успешным");

        Response secondResponse = sendRequest(token, ActionType.LOGIN);
        assertEquals(409, secondResponse.statusCode(),
                "Повторный логин должен возвращать 409 Conflict. Получен: " + secondResponse.statusCode());
        assertEquals("ERROR", secondResponse.jsonPath().getString("result"),
                "result должен быть ERROR");
        assertTrue(
                secondResponse.jsonPath().getString("message").contains("already exists"),
                "Сообщение должно содержать 'already exists'. Получено: " + secondResponse.jsonPath().getString("message")
        );
    }

    @Test
    @DisplayName("Повторный LOGIN с тем же токеном через некоторое время")
    @Story("Негативные сценарии LOGIN")
    @Description("Проверяем, что при повторном логине с тем же токеном после задержки возвращается 409 Conflict")
    @Severity(SeverityLevel.NORMAL)
    public void testRepeatedLoginWithSameTokenAfterDelay() throws InterruptedException {
        mockAuthSuccess();
        String token = TokenGenerator.generateValidToken();

        Response firstResponse = sendRequest(token, ActionType.LOGIN);
        assertEquals(200, firstResponse.statusCode(), "Первый логин должен быть успешным");

        Thread.sleep(2000);
        Response secondResponse = sendRequest(token, ActionType.LOGIN);

        assertEquals(409, secondResponse.statusCode(),
                "Повторный логин после задержки должен возвращать 409 Conflict. Получен: " + secondResponse.statusCode());
        assertEquals("ERROR", secondResponse.jsonPath().getString("result"),
                "result должен быть ERROR");
        assertTrue(
                secondResponse.jsonPath().getString("message").contains("already exists"),
                "Сообщение должно содержать 'already exists'. Получено: " + secondResponse.jsonPath().getString("message")
        );
    }

    @Test
    @DisplayName("Успешный вход в систему с лишними параметрами")
    @Story("Позитивные сценарии LOGIN")
    @Description("Проверяем, что лишние параметры игнорируются и запрос успешно обрабатывается")
    @Severity(SeverityLevel.MINOR)
    public void testLoginWithExtraParameters() {
        mockAuthSuccess();
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

        assertEquals(200, response.statusCode(),
                "С лишними параметрами должен быть 200. Получен: " + response.statusCode());
        assertEquals("OK", response.jsonPath().getString("result"),
                "result должен быть OK");
    }

    @Test
    @DisplayName("LOGIN с дублирующимися параметрами")
    @Story("Негативные сценарии LOGIN")
    @Description("Проверяем, что дублирующиеся параметры вызывают ошибку 400")
    @Severity(SeverityLevel.MINOR)
    public void testLoginWithDuplicateParameters() {
        mockAuthSuccess();
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

        assertEquals(400, response.statusCode(),
                "С дублирующимися параметрами код ответа должен быть 400. Получен: " + response.statusCode());
        assertEquals("ERROR", response.jsonPath().getString("result"),
                "result должен быть ERROR");
    }

    @Test
    @DisplayName("Логин с токеном, содержащим только цифры")
    @Story("Негативные сценарии LOGIN")
    @Description("Проверяем, что токен из 32 цифр принимается")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginWithDigitsOnlyToken() {
        mockAuthSuccess();
        String digitsToken = TokenGenerator.generateDigitsOnlyToken();

        Response response = sendRequest(digitsToken, ActionType.LOGIN);

        assertEquals(200, response.statusCode(),
                "Токен из цифр должен приниматься. Получен: " + response.statusCode());
        assertEquals("OK", response.jsonPath().getString("result"),
                "result должен быть OK");
    }

    @Test
    @DisplayName("Логин с токеном, содержащим только буквы A-F")
    @Story("Негативные сценарии LOGIN")
    @Description("Проверяем, что токен из 32 букв A-F принимается")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginWithAtoFOnlyToken() {
        mockAuthSuccess();
        String aToFToken = TokenGenerator.generateAtoFToken();

        Response response = sendRequest(aToFToken, ActionType.LOGIN);

        assertEquals(200, response.statusCode(),
                "Токен из букв A-F должен приниматься. Получен: " + response.statusCode());
        assertEquals("OK", response.jsonPath().getString("result"),
                "result должен быть OK");
    }

    @Test
    @DisplayName("Логин с токеном, содержащим только буквы G-Z")
    @Story("Негативные сценарии LOGIN")
    @Description("Проверяем, что токен с буквами G-Z возвращает 200")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginWithGtoZOnlyToken() {
        mockAuthSuccess();
        String gToZToken = TokenGenerator.generateGtoZToken();

        Response response = sendRequest(gToZToken, ActionType.LOGIN);

        assertEquals(200, response.statusCode(),
                "С лишними параметрами должен быть 200. Получен: " + response.statusCode());
        assertEquals("OK", response.jsonPath().getString("result"),
                "result должен быть OK");

    }

    @Test
    @DisplayName("Логин с токеном из 32 символов смешанного регистра")
    @Story("Негативные сценарии LOGIN")
    @Description("Проверяем, что токен со строчными буквами вызывает ошибку 400")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginWithLowercaseToken() {
        mockAuthSuccess();
        String lowercaseToken = TokenGenerator.generateLowercaseToken();

        Response response = sendRequest(lowercaseToken, ActionType.LOGIN);

        assertEquals(400, response.statusCode(),
                "Токен в нижнем регистре должен давать 400. Получен: " + response.statusCode());
        assertEquals("ERROR", response.jsonPath().getString("result"),
                "result должен быть ERROR");
    }

    @Test
    @DisplayName("5 одновременных успешных логинов с разными токенами")
    @Story("Позитивные сценарии LOGIN")
    @Description("Проверяем, что приложение корректно обрабатывает параллельные запросы с разными токенами")
    @Severity(SeverityLevel.NORMAL)
    public void testConcurrentLoginsWithDifferentTokens() throws InterruptedException, ExecutionException {
        mockAuthSuccess();
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Response>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                String token = TokenGenerator.generateValidToken();
                return sendRequest(token, ActionType.LOGIN);
            }));
        }

        List<Response> responses = new ArrayList<>();
        for (Future<Response> future : futures) {
            responses.add(future.get());
        }

        executor.shutdown();

        for (Response response : responses) {
            assertEquals(200, response.statusCode(),
                    "Все параллельные запросы должны возвращать 200. Получен: " + response.statusCode());
            assertEquals("OK", response.jsonPath().getString("result"),
                    "result должен быть OK");
        }
    }

    @Test
    @DisplayName("5 одновременных логинов с одинаковым токеном")
    @Story("Негативные сценарии LOGIN")
    @Description("Проверяем, что только первый запрос с токеном успешен, остальные получают 409")
    @Severity(SeverityLevel.NORMAL)
    public void testConcurrentLoginsWithSameToken() throws InterruptedException, ExecutionException {
        mockAuthSuccess();
        String sharedToken = TokenGenerator.generateValidToken();
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Response>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> sendRequest(sharedToken, ActionType.LOGIN)));
        }

        List<Response> responses = new ArrayList<>();
        for (Future<Response> future : futures) {
            responses.add(future.get());
        }

        executor.shutdown();

        int successCount = 0;
        int conflictCount = 0;

        for (Response response : responses) {
            if (response.statusCode() == 200) {
                successCount++;
                assertEquals("OK", response.jsonPath().getString("result"));
            } else if (response.statusCode() == 409) {
                conflictCount++;
                assertEquals("ERROR", response.jsonPath().getString("result"));
                assertTrue(
                        response.jsonPath().getString("message").contains("already exists"),
                        "Сообщение должно содержать 'already exists'"
                );
            }
        }

        assertEquals(1, successCount, "Должен быть ровно один успешный запрос");
        assertEquals(4, conflictCount, "Должно быть ровно четыре конфликта");
    }

    @Test
    @DisplayName("Последовательные логины с разными токенами")
    @Story("Позитивные сценарии LOGIN")
    @Description("Проверяем, что все логины с разными токенами успешны")
    @Severity(SeverityLevel.NORMAL)
    public void testSequentialLoginsWithDifferentTokens() {
        mockAuthSuccess();
        int count = 5;

        for (int i = 0; i < count; i++) {
            String token = TokenGenerator.generateValidToken();
            Response response = sendRequest(token, ActionType.LOGIN);

            assertEquals(200, response.statusCode(),
                    "Логин #" + (i + 1) + " должен быть успешным. Получен: " + response.statusCode());
            assertEquals("OK", response.jsonPath().getString("result"),
                    "result должен быть OK");
        }
    }
}