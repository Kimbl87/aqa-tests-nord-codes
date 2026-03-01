package com.test.tests;

import com.test.utils.TokenGenerator;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;
import com.test.config.ActionType;

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
}