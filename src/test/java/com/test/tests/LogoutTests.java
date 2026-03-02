package com.test.tests;

import com.test.utils.TokenGenerator;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import com.test.config.ActionType;
import com.test.config.ConnectionConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@Epic("Авторизация")
@Feature("LOGOUT")
@DisplayName("Тесты для выхода из системы")
public class LogoutTests extends BaseTest {

    @Test
    @DisplayName("Выход без предварительного входа")
    @Story("Негативные сценарии LOGOUT")
    @Description("Проверяем, что без предварительного LOGIN выход возвращает 403 ERROR с сообщением о ненайденном токене")
    @Severity(SeverityLevel.NORMAL)
    public void testLogoutWithoutLogin() {
        String token = TokenGenerator.generateValidToken();
        Response response = sendRequest(token, ActionType.LOGOUT);

        assertEquals(403, response.statusCode(), "Статус должен быть 403");
        assertEquals("ERROR", response.jsonPath().getString("result"), "result должен быть ERROR");
        assertEquals(String.format("Token '%s' not found", token), response.jsonPath().getString("message"),
                String.format("Token '%s' not found", token));
    }

    @Test
    @DisplayName("Выход после успешного входа")
    @Story("Позитивные сценарии LOGOUT")
    @Description("Проверяем, что после успешного LOGIN выход возвращает 200 OK с пустым message")
    @Severity(SeverityLevel.CRITICAL)
    public void testLogoutAfterLogin() {
        mockAuthSuccess();
        String token = TokenGenerator.generateValidToken();

        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());

        Response logoutResponse = sendRequest(token, ActionType.LOGOUT);

        assertEquals(200, logoutResponse.statusCode(), "Статус должен быть 200");
        assertEquals("OK", logoutResponse.jsonPath().getString("result"), "result должен быть OK");
        assertNull(logoutResponse.jsonPath().getString("message"), "message должен отсутствовать");
    }

    @Test
    @DisplayName("Повторный выход после успешного выхода")
    @Story("Идемпотентность LOGOUT")
    @Description("Проверяем, что повторный выход с тем же токеном после удаления возвращает 403")
    @Severity(SeverityLevel.MINOR)
    public void testRepeatedLogoutAfterLogout() {
        mockAuthSuccess();
        String token = TokenGenerator.generateValidToken();

        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());

        Response firstLogout = sendRequest(token, ActionType.LOGOUT);
        assertEquals(200, firstLogout.statusCode());
        assertEquals("OK", firstLogout.jsonPath().getString("result"));

        Response secondLogout = sendRequest(token, ActionType.LOGOUT);
        assertEquals(403, secondLogout.statusCode());
        assertEquals("ERROR", secondLogout.jsonPath().getString("result"));
        assertTrue(
                secondLogout.jsonPath().getString("message").contains("Token") &&
                        secondLogout.jsonPath().getString("message").contains("not found")
        );
    }

    @Test
    @DisplayName("Выход с токеном, залогиненным дважды")
    @Story("Идемпотентность LOGOUT")
    @Description("Проверяем, что выход работает корректно при множественных логинах")
    @Severity(SeverityLevel.MINOR)
    public void testLogoutWithTokenLoggedTwice() {
        mockAuthSuccess();
        String token = TokenGenerator.generateValidToken();

        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());
        assertEquals(409, sendRequest(token, ActionType.LOGIN).statusCode());

        Response logoutResponse = sendRequest(token, ActionType.LOGOUT);
        assertEquals(200, logoutResponse.statusCode());
        assertEquals("OK", logoutResponse.jsonPath().getString("result"));
    }

    @Test
    @DisplayName("Цикл вход-выход с одним токеном")
    @Story("Идемпотентность LOGOUT")
    @Description("Проверяем, что после выхода можно снова войти и снова выйти")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginLogoutCycleWithSameToken() {
        mockAuthSuccess();
        String token = TokenGenerator.generateValidToken();

        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());
        assertEquals(200, sendRequest(token, ActionType.LOGOUT).statusCode());

        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());
        assertEquals(200, sendRequest(token, ActionType.LOGOUT).statusCode());
    }

    @Test
    @DisplayName("Выход после выполнения действия")
    @Story("Взаимодействие с ACTION")
    @Description("Проверяем, что после успешного ACTION можно выйти из системы")
    @Severity(SeverityLevel.NORMAL)
    public void testLogoutAfterAction() {
        mockAuthSuccess();
        mockActionSuccess();
        String token = TokenGenerator.generateValidToken();

        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());
        assertEquals(200, sendRequest(token, ActionType.ACTION).statusCode());

        Response logoutResponse = sendRequest(token, ActionType.LOGOUT);
        assertEquals(200, logoutResponse.statusCode());
        assertEquals("OK", logoutResponse.jsonPath().getString("result"));
    }

    @Test
    @DisplayName("LOGOUT с лишними параметрами")
    @Story("Параметры запроса")
    @Description("Проверяем, как приложение обрабатывает лишние параметры в запросе LOGOUT")
    @Severity(SeverityLevel.MINOR)
    public void testLogoutWithExtraParameters() {
        mockAuthSuccess();
        String token = TokenGenerator.generateValidToken();

        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());

        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "LOGOUT")
                .formParam("extra1", "value1")
                .formParam("extra2", "value2")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(200, response.statusCode());
        assertEquals("OK", response.jsonPath().getString("result"));
    }

    @Test
    @DisplayName("LOGOUT с дублирующимися параметрами")
    @Story("Параметры запроса")
    @Description("Проверяем, что дублирующиеся параметры в запросе LOGOUT вызывают ошибку 400")
    @Severity(SeverityLevel.MINOR)
    public void testLogoutWithDuplicateParameters() {
        mockAuthSuccess();
        String token = TokenGenerator.generateValidToken();
        String duplicateToken = TokenGenerator.generateValidToken();

        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());

        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("token", duplicateToken)
                .formParam("action", "LOGOUT")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(400, response.statusCode());
        assertEquals("ERROR", response.jsonPath().getString("result"));
    }

    @Test
    @DisplayName("5 параллельных LOGOUT с разными токенами")
    @Story("Параллельные запросы")
    @Description("Проверяем, что приложение корректно обрабатывает параллельные запросы LOGOUT с разными токенами")
    @Severity(SeverityLevel.NORMAL)
    public void testConcurrentLogoutsWithDifferentTokens() throws InterruptedException, ExecutionException {
        mockAuthSuccess();
        int threadCount = 5;
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            String token = TokenGenerator.generateValidToken();
            tokens.add(token);
            assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());
        }

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Response>> futures = new ArrayList<>();

        for (String token : tokens) {
            futures.add(executor.submit(() -> sendRequest(token, ActionType.LOGOUT)));
        }

        List<Response> responses = new ArrayList<>();
        for (Future<Response> future : futures) {
            responses.add(future.get());
        }

        executor.shutdown();

        for (Response response : responses) {
            assertEquals(200, response.statusCode());
            assertEquals("OK", response.jsonPath().getString("result"));
        }
    }

    @Test
    @DisplayName("5 параллельных LOGOUT с одинаковым токеном")
    @Story("Параллельные запросы")
    @Description("Проверяем поведение при параллельных запросах LOGOUT с одинаковым токеном")
    @Severity(SeverityLevel.NORMAL)
    public void testConcurrentLogoutsWithSameToken() throws InterruptedException, ExecutionException {
        mockAuthSuccess();
        String sharedToken = TokenGenerator.generateValidToken();
        assertEquals(200, sendRequest(sharedToken, ActionType.LOGIN).statusCode());

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Response>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> sendRequest(sharedToken, ActionType.LOGOUT)));
        }

        List<Response> responses = new ArrayList<>();
        for (Future<Response> future : futures) {
            responses.add(future.get());
        }

        executor.shutdown();

        int successCount = 0;
        for (Response response : responses) {
            if (response.statusCode() == 200) {
                successCount++;
                assertEquals("OK", response.jsonPath().getString("result"));
            }
        }

        assertEquals(1, successCount, "Только один запрос должен быть успешным");
    }
}