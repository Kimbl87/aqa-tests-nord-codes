package com.test.tests;

import com.test.utils.TokenGenerator;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;
import com.test.config.ActionType;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Действие")
@Feature("ACTION")
@DisplayName("Тесты для действия ACTION")
public class ActionTests extends BaseTest {

    @Test
    @DisplayName("Действие без предварительного входа")
    @Story("Негативные сценарии ACTION")
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
    @DisplayName("Действие после успешного входа")
    @Story("Позитивные сценарии ACTION")
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
    @DisplayName("Ошибка внешнего сервиса при выполнении действия")
    @Story("Негативные сценарии ACTION")
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
    @Story("Негативные сценарии ACTION")
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
}