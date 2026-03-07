package com.test.tests;

import com.test.base.BaseTest;
import com.test.assertions.ActionAssertions;
import com.test.assertions.LoginAssertions;
import com.test.assertions.LogoutAssertions;
import com.test.steps.ActionSteps;
import com.test.steps.CommonSteps;
import com.test.steps.LoginSteps;
import com.test.steps.LogoutSteps;
import com.test.utils.TokenGenerator;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import io.restassured.response.Response;

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
        LoginSteps.mockAuthSuccess();
        ActionSteps.mockActionSuccess();
        String token = CommonSteps.generateToken("для действия");

        Response loginResponse = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginSuccess(loginResponse);

        Response actionResponse = ActionSteps.sendAction(token);
        ActionAssertions.verifyActionSuccess(actionResponse);
    }

    @Test
    @DisplayName("Действие с лишними параметрами")
    @Story("Позитивные сценарии: ACTION")
    @Description("Проверяем, что лишние параметры игнорируются и запрос успешно обрабатывается")
    @Severity(SeverityLevel.MINOR)
    public void testActionWithExtraParameters() {
        LoginSteps.mockAuthSuccess();
        ActionSteps.mockActionSuccess();
        String token = CommonSteps.generateToken("для действия с параметрами");

        Response loginResponse = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginSuccess(loginResponse);

        Response response = ActionSteps.sendActionWithExtraParams(token);
        ActionAssertions.verifyActionSuccess(response);
    }

    @Test
    @DisplayName("Несколько ACTION подряд")
    @Story("Позитивные сценарии: ACTION")
    @Description("Проверяем, что можно выполнить несколько ACTION подряд с одним токеном")
    @Severity(SeverityLevel.NORMAL)
    public void testMultipleActionsInRow() {
        LoginSteps.mockAuthSuccess();
        ActionSteps.mockActionSuccess();
        String token = CommonSteps.generateToken("для нескольких действий");

        Response loginResponse = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginSuccess(loginResponse);

        for (int i = 0; i < 5; i++) {
            Response actionResponse = ActionSteps.sendAction(token);
            ActionAssertions.verifyActionSuccess(actionResponse);
        }
    }

    @Test
    @DisplayName("ACTION после повторного входа")
    @Story("Позитивные сценарии: ACTION")
    @Description("Проверяем, что после повторного входа ACTION работает")
    @Severity(SeverityLevel.NORMAL)
    public void testActionAfterReLogin() {
        LoginSteps.mockAuthSuccess();
        ActionSteps.mockActionSuccess();
        String token = CommonSteps.generateToken("для повторного входа");

        Response loginResponse1 = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginSuccess(loginResponse1);

        Response logoutResponse = LogoutSteps.sendLogout(token);
        LogoutAssertions.verifyLogoutSuccess(logoutResponse);

        Response loginResponse2 = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginSuccess(loginResponse2);

        Response actionResponse = ActionSteps.sendAction(token);
        ActionAssertions.verifyActionSuccess(actionResponse);
    }

    @Test
    @DisplayName("Действие без предварительного входа")
    @Story("Негативные сценарии: ACTION/базовые")
    @Description("Проверяем, что без авторизации /action возвращает 403 Error")
    @Severity(SeverityLevel.NORMAL)
    public void testActionWithoutLogin() {
        String token = CommonSteps.generateToken("без логина");
        Response response = ActionSteps.sendAction(token);
        ActionAssertions.verifyActionWithoutLogin(response, token);
    }

    @Test
    @DisplayName("Ошибка внешнего сервиса при выполнении действия")
    @Story("Негативные сценарии: ACTION/базовые")
    @Description("Проверяем, что при ошибке от /doAction возвращается 500 ERROR")
    @Severity(SeverityLevel.NORMAL)
    public void testActionFailure() {
        LoginSteps.mockAuthSuccess();
        ActionSteps.mockActionFailure();
        String token = CommonSteps.generateToken("для ошибки действия");

        Response loginResponse = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginSuccess(loginResponse);

        Response actionResponse = ActionSteps.sendAction(token);
        ActionAssertions.verifyActionError(actionResponse);
    }

    @Test
    @DisplayName("Действие после выхода из системы")
    @Story("Негативные сценарии: ACTION/базовые")
    @Description("Проверяем, что после LOGOUT действие /action возвращает 403 ERROR")
    @Severity(SeverityLevel.NORMAL)
    public void testActionAfterLogout() {
        LoginSteps.mockAuthSuccess();
        String token = CommonSteps.generateToken("для действия после выхода");

        Response loginResponse = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginSuccess(loginResponse);

        Response logoutResponse = LogoutSteps.sendLogout(token);
        LogoutAssertions.verifyLogoutSuccess(logoutResponse);

        Response actionResponse = ActionSteps.sendAction(token);
        ActionAssertions.verifyActionWithoutLogin(actionResponse, token);
    }

    @ParameterizedTest(name = "Ошибка внешнего сервиса: /doAction отвечает {0}")
    @MethodSource("com.test.parameterized.ActionErrorCodesProvider#provideActionErrorCodes")
    @DisplayName("Разные коды ошибок внешнего сервиса")
    @Story("Негативные сценарии: ACTION/ошибки внешнего сервиса")
    @Description("Проверяем, что при разных ошибках от /doAction возвращается 500 ERROR")
    @Severity(SeverityLevel.NORMAL)
    public void testActionErrorCodes(int statusCode, String errorBody) {
        LoginSteps.mockAuthSuccess();
        ActionSteps.mockActionError(statusCode, errorBody);
        String token = CommonSteps.generateToken("для кодов ошибок");

        Response loginResponse = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginSuccess(loginResponse);

        Response response = ActionSteps.sendAction(token);
        ActionAssertions.verifyActionError(response);
    }

    @Test
    @DisplayName("Пустой ответ от /doAction")
    @Story("Негативные сценарии: ACTION/разные типы ответов")
    @Description("Проверяем, что при пустом ответе от внешнего сервиса возвращается 400 ERROR")
    @Severity(SeverityLevel.NORMAL)
    public void testActionEmptyResponse() {
        LoginSteps.mockAuthSuccess();
        ActionSteps.mockActionEmptyResponse();
        String token = CommonSteps.generateToken("для пустого ответа");

        Response loginResponse = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginSuccess(loginResponse);

        Response response = ActionSteps.sendAction(token);
        ActionAssertions.verifyActionEmptyResponse(response);
    }

    @Test
    @DisplayName("Некорректный JSON от /doAction")
    @Story("Негативные сценарии: ACTION/разные типы ответов")
    @Description("Проверяем, что при некорректном JSON от внешнего сервиса возвращается 400 ERROR")
    @Severity(SeverityLevel.NORMAL)
    public void testActionInvalidJson() {
        LoginSteps.mockAuthSuccess();
        ActionSteps.mockActionInvalidJson();
        String token = CommonSteps.generateToken("для невалидного JSON");

        Response loginResponse = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginSuccess(loginResponse);

        Response response = ActionSteps.sendAction(token);
        ActionAssertions.verifyActionInvalidJson(response);
    }

    @Test
    @DisplayName("Таймаут внешнего сервиса")
    @Story("Негативные сценарии: ACTION/сетевые проблемы")
    @Description("Проверяем, что при таймауте /doAction возвращается 500 ERROR")
    @Severity(SeverityLevel.NORMAL)
    public void testActionTimeout() {
        LoginSteps.mockAuthSuccess();
        ActionSteps.mockActionTimeout();
        String token = CommonSteps.generateToken("для таймаута");

        Response loginResponse = LoginSteps.sendFirstLogin(token);
        LoginAssertions.verifyLoginSuccess(loginResponse);

        Response response = ActionSteps.sendAction(token);
        ActionAssertions.verifyActionError(response);
    }

    @Test
    @DisplayName("5 параллельных ACTION с одинаковым токеном")
    @Story("Негативные сценарии: ACTION/параллельные запросы")
    @Description("Проверяем поведение при параллельных запросах ACTION с одинаковым токеном")
    @Severity(SeverityLevel.NORMAL)
    public void testConcurrentActionsWithSameToken() throws Exception {
        LoginSteps.mockAuthSuccess();
        ActionSteps.mockActionSuccess();
        String sharedToken = CommonSteps.generateToken("для параллельных действий");

        Response loginResponse = LoginSteps.sendFirstLogin(sharedToken);
        LoginAssertions.verifyLoginSuccess(loginResponse);

        ActionSteps.sendConcurrentActions(sharedToken, 5);
    }
}