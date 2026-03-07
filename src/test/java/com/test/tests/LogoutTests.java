package com.test.tests;

import com.test.base.BaseTest;
import com.test.assertions.LoginAssertions;
import com.test.assertions.LogoutAssertions;
import com.test.steps.CommonSteps;
import com.test.steps.LoginSteps;
import com.test.steps.LogoutSteps;
import com.test.utils.TokenGenerator;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;

@Epic("Выход из системы")
@Feature("LOGOUT")
@DisplayName("Тесты для выхода из системы")
public class LogoutTests extends BaseTest {

    @Test
    @DisplayName("Выход после успешного входа")
    @Story("Позитивные сценарии: LOGOUT")
    @Description("Проверяем, что после успешного LOGIN выход возвращает 200 OK с пустым message")
    @Severity(SeverityLevel.CRITICAL)
    public void testLogoutAfterLogin() {
        LoginSteps.mockAuthSuccess();
        String token = CommonSteps.generateToken("для выхода");

        Response loginResponse = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginSuccess(loginResponse);

        Response logoutResponse = LogoutSteps.sendLogout(token);
        LogoutAssertions.verifyLogoutSuccess(logoutResponse);
    }

    @Test
    @DisplayName("Выход с лишними параметрами")
    @Story("Позитивные сценарии: LOGOUT")
    @Description("Проверяем, как приложение обрабатывает лишние параметры в запросе LOGOUT")
    @Severity(SeverityLevel.MINOR)
    public void testLogoutWithExtraParameters() {
        LoginSteps.mockAuthSuccess();
        String token = CommonSteps.generateToken("для выхода с параметрами");

        Response loginResponse = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginSuccess(loginResponse);

        Response logoutResponse = LogoutSteps.sendLogoutWithExtraParams(token);
        LogoutAssertions.verifyLogoutSuccess(logoutResponse);
    }

    @Test
    @DisplayName("Повторный выход после успешного выхода")
    @Story("Позитивные сценарии: LOGOUT")
    @Description("Проверяем, что повторный выход с тем же токеном после удаления возвращает 403")
    @Severity(SeverityLevel.MINOR)
    public void testRepeatedLogoutAfterLogout() {
        LoginSteps.mockAuthSuccess();
        String token = CommonSteps.generateToken("для повторного выхода");

        Response loginResponse = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginSuccess(loginResponse);

        Response firstLogout = LogoutSteps.sendLogout(token);
        LogoutAssertions.verifyLogoutSuccess(firstLogout);

        Response secondLogout = LogoutSteps.sendLogout(token);
        LogoutAssertions.verifyLogoutWithoutLogin(secondLogout, token);
    }

    @Test
    @DisplayName("Выход с токеном, залогиненным дважды")
    @Story("Позитивные сценарии: LOGOUT")
    @Description("Проверяем, что выход работает корректно при множественных логинах")
    @Severity(SeverityLevel.MINOR)
    public void testLogoutWithTokenLoggedTwice() {
        LoginSteps.mockAuthSuccess();
        String token = CommonSteps.generateToken("для двойного логина");

        Response firstLogin = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginSuccess(firstLogin);

        Response secondLogin = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginConflict(secondLogin, token);

        Response logoutResponse = LogoutSteps.sendLogout(token);
        LogoutAssertions.verifyLogoutSuccess(logoutResponse);
    }

    @Test
    @DisplayName("Цикл вход-выход с одним токеном")
    @Story("Позитивные сценарии: LOGOUT")
    @Description("Проверяем, что после выхода можно снова войти и снова выйти")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginLogoutCycleWithSameToken() {
        LoginSteps.mockAuthSuccess();
        String token = CommonSteps.generateToken("для цикла");

        Response login1 = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginSuccess(login1);

        Response logout1 = LogoutSteps.sendLogout(token);
        LogoutAssertions.verifyLogoutSuccess(logout1);

        Response login2 = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginSuccess(login2);

        Response logout2 = LogoutSteps.sendLogout(token);
        LogoutAssertions.verifyLogoutSuccess(logout2);
    }

    @Test
    @DisplayName("Выход без предварительного входа")
    @Story("Негативные сценарии: LOGOUT")
    @Description("Проверяем, что без предварительного LOGIN выход возвращает 403 ERROR")
    @Severity(SeverityLevel.NORMAL)
    public void testLogoutWithoutLogin() {
        String token = CommonSteps.generateToken("без входа");
        Response response = LogoutSteps.sendLogout(token);
        LogoutAssertions.verifyLogoutWithoutLogin(response, token);
    }
}