package com.test.tests;

import com.test.assertions.LoginAssertions;
import com.test.base.BaseTest;
import com.test.steps.CommonSteps;
import com.test.steps.LoginSteps;
import com.test.utils.TokenGenerator;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import io.restassured.response.Response;

@Epic("Авторизация")
@Feature("LOGIN")
@DisplayName("Тесты входа в систему")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginTests extends BaseTest {

    @BeforeAll
    void setupTests() {
    }

    @AfterAll
    void cleanupTests() {
    }

    @Test
    @DisplayName("Успешный вход в систему")
    @Story("Позитивные сценарии LOGIN")
    @Description("Проверяем, что при успешном ответе от /auth возвращается 200 OK")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginSuccess() {
        LoginSteps.mockAuthSuccess();
        String token = CommonSteps.generateToken("основной");
        Response response = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginSuccess(response);
    }

    @ParameterizedTest(name = "Ошибка авторизации: внешний сервис отвечает {0}")
    @MethodSource("com.test.parameterized.AuthErrorCodesProvider#provideAuthErrorCodes")
    @DisplayName("Разные коды ошибок внешнего сервиса")
    @Story("Негативные сценарии LOGIN")
    @Description("Проверяем, что при разных ошибках от /auth возвращается 500 ERROR")
    @Severity(SeverityLevel.NORMAL)
    public void testAuthErrorCodes(int statusCode, String errorBody) {
        LoginSteps.mockAuthError(statusCode, errorBody);
        String token = CommonSteps.generateToken("для теста с ошибкой");
        Response response = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyAuthError(response, statusCode, errorBody);
    }

    @Test
    @DisplayName("Таймаут внешнего сервиса")
    @Story("Негативные сценарии LOGIN")
    @Description("Проверяем, что при таймауте от /auth возвращается 500 ERROR")
    @Severity(SeverityLevel.NORMAL)
    public void testAuthTimeout() {
        LoginSteps.mockAuthTimeout();
        String token = CommonSteps.generateToken("для теста с таймаутом");
        Response response = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyAuthTimeout(response);
    }

    @Test
    @DisplayName("Повторный LOGIN с тем же токеном")
    @Story("Негативные сценарии LOGIN")
    @Description("Проверяем, что при повторном логине с тем же токеном возвращается 409 Conflict")
    @Severity(SeverityLevel.NORMAL)
    public void testRepeatedLoginWithSameToken() {
        LoginSteps.mockAuthSuccess();
        String token = CommonSteps.generateToken("для повторного логина");
        Response firstResponse = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginSuccess(firstResponse);
        Response secondResponse = LoginSteps.sendSecondLogin(token);
        LoginAssertions.verifyLoginConflict(secondResponse, token);
    }

    @Test
    @DisplayName("Успешный вход в систему с лишними параметрами")
    @Story("Позитивные сценарии LOGIN")
    @Description("Проверяем, что лишние параметры игнорируются и запрос успешно обрабатывается")
    @Severity(SeverityLevel.MINOR)
    public void testLoginWithExtraParameters() {
        LoginSteps.mockAuthSuccess();
        String token = CommonSteps.generateToken("для теста с лишними параметрами");
        Response response = LoginSteps.sendLoginWithExtraParams(token);
        LoginAssertions.verifyLoginSuccess(response);
    }

    @Test
    @DisplayName("LOGIN с дублирующимися параметрами")
    @Story("Негативные сценарии LOGIN")
    @Description("Проверяем, что дублирующиеся параметры вызывают ошибку 400")
    @Severity(SeverityLevel.MINOR)
    public void testLoginWithDuplicateParameters() {
        LoginSteps.mockAuthSuccess();
        String token = CommonSteps.generateToken("основной");
        String duplicateToken = TokenGenerator.generateValidToken(false);
        Response response = LoginSteps.sendLoginWithDuplicateParams(token, duplicateToken);
        LoginAssertions.verifyDuplicateParamsError(response);
    }

    @Test
    @DisplayName("Логин с токеном из цифр")
    @Story("Позитивные сценарии LOGIN")
    @Description("Проверяем, что токен из 32 цифр принимается")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginWithDigitsOnlyToken() {
        LoginSteps.mockAuthSuccess();
        String digitsToken = CommonSteps.generateDigitsToken("тестовый");
        Response response = LoginSteps.sendFirstLogin(digitsToken);
        LoginAssertions.verifyLoginSuccess(response);
    }

    @Test
    @DisplayName("Логин с токеном из букв A-F")
    @Story("Позитивные сценарии LOGIN")
    @Description("Проверяем, что токен из 32 букв A-F принимается")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginWithAtoFOnlyToken() {
        LoginSteps.mockAuthSuccess();
        String aToFToken = CommonSteps.generateAtoFToken("тестовый");
        Response response = LoginSteps.sendFirstLogin(aToFToken);
        LoginAssertions.verifyLoginSuccess(response);
    }

    @Test
    @DisplayName("Логин с токеном в нижнем регистре")
    @Story("Негативные сценарии LOGIN")
    @Description("Проверяем, что токен со строчными буквами вызывает ошибку 400")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginWithLowercaseToken() {
        LoginSteps.mockAuthSuccess();
        String lowercaseToken = CommonSteps.generateLowercaseToken("тестовый");
        Response response = LoginSteps.sendFirstLogin(lowercaseToken);
        LoginAssertions.verifyInvalidTokenError(response, lowercaseToken);
    }
}