package com.test.tests;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.test.utils.TokenGenerator;
import com.test.parameterized.ActionErrorCodesProvider;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Epic("Действие")
@Feature("ACTION")
@DisplayName("Тесты для действия ACTION")
public class ActionTests extends BaseTest {

    @Test
    @DisplayName("Действие после успешного входа")
    @Story("Позитивные сценарии: ACTION")
    @Description("Проверяем, что после успешного LOGIN действие /action возвращает 200 OK с пустым message")
    @Severity(SeverityLevel.CRITICAL)
    public void testActionAfterLogin() {
        mockAuthSuccess();
        mockActionSuccess();
        String token = TokenGenerator.generateValidToken();

        Response loginResponse = sendRequest(token, ActionType.LOGIN);
        assertEquals(200, loginResponse.statusCode());

        Response actionResponse = sendRequest(token, ActionType.ACTION);

        assertEquals(200, actionResponse.statusCode(), "Статус должен быть 200");
        assertEquals("OK", actionResponse.jsonPath().getString("result"), "result должен быть OK");
        assertNull(actionResponse.jsonPath().getString("message"), "message должен отсутствовать");
    }

    @Test
    @DisplayName("Действие с лишними параметрами")
    @Story("Позитивные сценарии: ACTION")
    @Description("Проверяем, что лишние параметры игнорируются и запрос успешно обрабатывается")
    @Severity(SeverityLevel.MINOR)
    public void testActionWithExtraParameters() {
        mockAuthSuccess();
        mockActionSuccess();
        String token = TokenGenerator.generateValidToken();

        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());

        Response response = RestAssured
                .given()
                .header("X-Api-Key", ConnectionConfig.API_KEY)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .formParam("action", "ACTION")
                .formParam("extra1", "value1")
                .formParam("extra2", "value2")
                .post(ConnectionConfig.BASE_URL + ConnectionConfig.ENDPOINT);

        assertEquals(200, response.statusCode(),
                "С лишними параметрами должен быть 200. Получен: " + response.statusCode());
        assertEquals("OK", response.jsonPath().getString("result"),
                "result должен быть OK");
    }

    @Test
    @DisplayName("Несколько ACTION подряд")
    @Story("Позитивные сценарии: ACTION")
    @Description("Проверяем, что можно выполнить несколько ACTION подряд с одним токеном")
    @Severity(SeverityLevel.NORMAL)
    public void testMultipleActionsInRow() {
        mockAuthSuccess();
        mockActionSuccess();
        String token = TokenGenerator.generateValidToken();

        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());

        for (int i = 0; i < 5; i++) {
            Response response = sendRequest(token, ActionType.ACTION);
            assertEquals(200, response.statusCode(),
                    "ACTION #" + (i + 1) + " должен быть успешным");
            assertEquals("OK", response.jsonPath().getString("result"));
        }
    }

    @Test
    @DisplayName("ACTION после повторного входа")
    @Story("Позитивные сценарии: ACTION")
    @Description("Проверяем, что после повторного входа ACTION работает")
    @Severity(SeverityLevel.NORMAL)
    public void testActionAfterReLogin() {
        mockAuthSuccess();
        mockActionSuccess();
        String token = TokenGenerator.generateValidToken();

        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());
        assertEquals(200, sendRequest(token, ActionType.LOGOUT).statusCode());
        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());

        Response response = sendRequest(token, ActionType.ACTION);
        assertEquals(200, response.statusCode());
        assertEquals("OK", response.jsonPath().getString("result"));
    }

    @Test
    @DisplayName("ACTION с токеном, залогиненным дважды")
    @Story("Позитивные сценарии: ACTION")
    @Description("Проверяем, что ACTION работает с токеном, который логинился дважды")
    @Severity(SeverityLevel.MINOR)
    public void testActionWithTokenLoggedTwice() {
        mockAuthSuccess();
        mockActionSuccess();
        String token = TokenGenerator.generateValidToken();

        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());
        assertEquals(409, sendRequest(token, ActionType.LOGIN).statusCode());

        Response response = sendRequest(token, ActionType.ACTION);
        assertEquals(200, response.statusCode());
        assertEquals("OK", response.jsonPath().getString("result"));
    }

    @Test
    @DisplayName("5 параллельных ACTION с разными токенами")
    @Story("Позитивные сценарии: ACTION")
    @Description("Проверяем, что приложение корректно обрабатывает параллельные запросы ACTION с разными токенами")
    @Severity(SeverityLevel.NORMAL)
    public void testConcurrentActionsWithDifferentTokens() throws InterruptedException, ExecutionException {
        mockAuthSuccess();
        mockActionSuccess();

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
            futures.add(executor.submit(() -> sendRequest(token, ActionType.ACTION)));
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
    @DisplayName("Действие без предварительного входа")
    @Story("Негативные сценарии: ACTION/базовые")
    @Description("Проверяем, что без авторизации /action возвращает 403 Error с сообщением о ненайденном токене")
    @Severity(SeverityLevel.NORMAL)
    public void testActionWithoutLogin() {
        String token = TokenGenerator.generateValidToken();
        Response response = sendRequest(token, ActionType.ACTION);

        assertEquals(403, response.statusCode(), "Статус должен быть 403");
        assertEquals("ERROR", response.jsonPath().getString("result"), "result должен быть ERROR");
        assertEquals(String.format("Token '%s' not found", token), response.jsonPath().getString("message"),
                String.format("Token '%s' not found", token));
    }

    @Test
    @DisplayName("Ошибка внешнего сервиса при выполнении действия")
    @Story("Негативные сценарии: ACTION/базовые")
    @Description("Проверяем, что при ошибке от /doAction возвращается 500 ERROR с Internal Server Error")
    @Severity(SeverityLevel.NORMAL)
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
    }

    @Test
    @DisplayName("Действие после выхода из системы")
    @Story("Негативные сценарии: ACTION/базовые")
    @Description("Проверяем, что после LOGOUT действие /action возвращает 403 ERROR с Session not found")
    @Severity(SeverityLevel.NORMAL)
    public void testActionAfterLogout() {
        mockAuthSuccess();
        String token = TokenGenerator.generateValidToken();

        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());
        assertEquals(200, sendRequest(token, ActionType.LOGOUT).statusCode());

        Response response = sendRequest(token, ActionType.ACTION);

        assertEquals(403, response.statusCode(), "Статус должен быть 200");
        assertEquals("ERROR", response.jsonPath().getString("result"), "result должен быть ERROR");
        assertEquals(String.format("Token '%s' not found", token), response.jsonPath().getString("message"),
                String.format("Token '%s' not found", token));
    }

    @ParameterizedTest(name = "Ошибка внешнего сервиса: /doAction отвечает {0}")
    @MethodSource("com.test.parameterized.ActionErrorCodesProvider#provideActionErrorCodes")
    @DisplayName("Разные коды ошибок внешнего сервиса")
    @Story("Негативные сценарии: ACTION/ошибки внешнего сервиса")
    @Description("Проверяем, что при разных ошибках от /doAction возвращается 500 ERROR")
    @Severity(SeverityLevel.NORMAL)
    public void testActionErrorCodes(int statusCode, String errorBody) {
        mockAuthSuccess();

        stubFor(post(urlEqualTo("/doAction"))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withBody(errorBody)));

        String token = TokenGenerator.generateValidToken();
        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());

        Response response = sendRequest(token, ActionType.ACTION);

        assertEquals(500, response.statusCode(),
                "При ошибке внешнего сервиса должен быть 500. Получен: " + response.statusCode());
        assertEquals("ERROR", response.jsonPath().getString("result"),
                "result должен быть ERROR");
        assertEquals("Internal Server Error", response.jsonPath().getString("message"),
                "message должен быть Internal Server Error");
    }

    @Test
    @DisplayName("Пустой ответ от /doAction")
    @Story("Негативные сценарии: ACTION/разные типы ответов")
    @Description("Проверяем, что при пустом ответе от внешнего сервиса возвращается 500 ERROR")
    @Severity(SeverityLevel.NORMAL)
    public void testActionEmptyResponse() {
        mockAuthSuccess();

        stubFor(post(urlEqualTo("/doAction"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("")));

        String token = TokenGenerator.generateValidToken();
        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());

        Response response = sendRequest(token, ActionType.ACTION);

        assertEquals(400, response.statusCode(),
                "При пустом ответе должен быть 400. Получен: " + response.statusCode());

    }

    @Test
    @DisplayName("Некорректный JSON от /doAction")
    @Story("Негативные сценарии: ACTION/разные типы ответов")
    @Description("Проверяем, что при некорректном JSON от внешнего сервиса возвращается 500 ERROR")
    @Severity(SeverityLevel.NORMAL)
    public void testActionInvalidJson() {
        mockAuthSuccess();

        stubFor(post(urlEqualTo("/doAction"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{invalid: json}")));

        String token = TokenGenerator.generateValidToken();
        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());

        Response response = sendRequest(token, ActionType.ACTION);

        assertEquals(400, response.statusCode(),
                "При некорректном JSON должен быть 400. Получен: " + response.statusCode());
    }

    @Test
    @DisplayName("Таймаут внешнего сервиса")
    @Story("Негативные сценарии: ACTION/сетевые проблемы")
    @Description("Проверяем, что при таймауте /doAction возвращается 500 ERROR")
    @Severity(SeverityLevel.NORMAL)
    public void testActionTimeout() {
        mockAuthSuccess();

        stubFor(post(urlEqualTo("/doAction"))
                .willReturn(aResponse()
                        .withFixedDelay(10000)
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"done\"}")));

        String token = TokenGenerator.generateValidToken();
        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());

        Response response = sendRequest(token, ActionType.ACTION);

        assertEquals(500, response.statusCode(),
                "При таймауте должен быть 500. Получен: " + response.statusCode());
        assertEquals("ERROR", response.jsonPath().getString("result"),
                "result должен быть ERROR");
        assertEquals("Internal Server Error", response.jsonPath().getString("message"),
                "message должен быть Internal Server Error");
    }

    @Test
    @DisplayName("Внешний сервис недоступен")
    @Story("Негативные сценарии: ACTION/сетевые проблемы")
    @Description("Проверяем, что при недоступном внешнем сервисе возвращается 500 ERROR")
    @Severity(SeverityLevel.NORMAL)
    public void testActionServiceUnavailable() {
        mockAuthSuccess();
        String token = TokenGenerator.generateValidToken();
        assertEquals(200, sendRequest(token, ActionType.LOGIN).statusCode());

        mockServer.stop();

        try {
            Response response = sendRequest(token, ActionType.ACTION);

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
    @DisplayName("5 параллельных ACTION с одинаковым токеном")
    @Story("Негативные сценарии: ACTION/параллельные запросы")
    @Description("Проверяем поведение при параллельных запросах ACTION с одинаковым токеном")
    @Severity(SeverityLevel.NORMAL)
    public void testConcurrentActionsWithSameToken() throws InterruptedException, ExecutionException {
        mockAuthSuccess();
        mockActionSuccess();

        String sharedToken = TokenGenerator.generateValidToken();
        assertEquals(200, sendRequest(sharedToken, ActionType.LOGIN).statusCode());

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Response>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> sendRequest(sharedToken, ActionType.ACTION)));
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
}