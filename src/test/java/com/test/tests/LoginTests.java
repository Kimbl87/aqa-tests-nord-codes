package com.test.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;
import com.test.config.ActionType;
import io.qameta.allure.*;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("Ошибка при входе в систему")
    @Story("Негативные сценарии LOGIN")
    @Description("Проверяем, что при ошибке от /auth возвращается 500 ERROR")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginFailure() {
        mockAuthFailure();
        Response response = sendRequest(ActionType.LOGIN);

        assertEquals(500, response.statusCode(), "Ожидаем ответ 500 Internal Server Error");
        assertEquals("ERROR", response.jsonPath().getString("result"), "result должен быть ERROR");
        assertEquals("Internal Server Error", response.jsonPath().getString("message"),
                "message должен быть Internal Server Error");
    }
}