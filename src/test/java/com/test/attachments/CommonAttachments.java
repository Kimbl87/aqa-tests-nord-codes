package com.test.attachments;

import io.qameta.allure.Attachment;
import io.restassured.response.Response;
import com.test.config.ActionType;
import com.test.config.ConnectionConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CommonAttachments {

    @Attachment(value = "Детали запроса/ответа: {description}", type = "text/plain")
    public static String attachRequestResponseDetails(String method, String url,
                                                      String token, ActionType action, String apiKey, Response response,
                                                      String description, String... extraParams) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(description).append(" ===\n\n");
        sb.append("=== ЗАПРОС ===\n");
        sb.append(String.format("Метод: %s\n", method));
        sb.append(String.format("URL: %s\n", url));
        sb.append("Заголовки:\n");
        sb.append(String.format("  X-Api-Key: %s\n", apiKey));
        sb.append(String.format("  Content-Type: application/x-www-form-urlencoded\n"));
        sb.append("Параметры формы:\n");
        sb.append(String.format("  token: %s\n", token));
        sb.append(String.format("  action: %s\n", action));
        if (extraParams != null && extraParams.length > 0) {
            for (int i = 0; i < extraParams.length; i += 2) {
                if (i + 1 < extraParams.length) {
                    sb.append(String.format("  %s: %s\n", extraParams[i], extraParams[i + 1]));
                }
            }
        }
        sb.append("\n=== CURL ===\n");
        sb.append(generateCurl(method, url, apiKey, token, action, extraParams));
        sb.append("\n");
        sb.append("\n=== ОТВЕТ ===\n");
        sb.append(String.format("Статус: %d %s\n", response.statusCode(), response.statusLine()));
        sb.append("Заголовки ответа:\n");
        response.getHeaders().asList().forEach(header ->
                sb.append(String.format("  %s: %s\n", header.getName(), header.getValue()))
        );
        sb.append("\nТело ответа:\n");
        sb.append(formatJson(response.asString()));
        sb.append("\n\nВремя: ").append(new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
        return sb.toString();
    }

    @Attachment(value = "Результат проверки: {checkName}", type = "text/plain")
    public static String attachVerificationResult(String checkName, String result) {
        return String.format("ПРОВЕРКА: %s\nРЕЗУЛЬТАТ: %s\nВРЕМЯ: %s",
                checkName, result, new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
    }

    @Attachment(value = "Информация", type = "text/plain")
    public static String attachInfo(String info) {
        return String.format("[%s] %s", new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()), info);
    }

    @Attachment(value = "Сводка по параллельным запросам", type = "text/plain")
    public static String attachConcurrentSummary(int count, String token, List<String> threadNames,
                                                 List<Integer> statusCodes, List<String> responses) {
        StringBuilder sb = new StringBuilder();
        sb.append("ПАРАЛЛЕЛЬНЫЕ ЗАПРОСЫ\n");
        sb.append("==================\n\n");
        sb.append(String.format("Токен: %s\n", token));
        sb.append(String.format("Количество запросов: %d\n", count));
        sb.append(String.format("Количество потоков: %d\n", threadNames.size()));
        sb.append("\nДетали по запросам:\n");
        for (int i = 0; i < responses.size(); i++) {
            sb.append(String.format("  Запрос #%d: статус %d, поток %s\n",
                    i + 1, statusCodes.get(i), threadNames.get(i)));
        }
        sb.append("\n\nВремя: ").append(new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
        return sb.toString();
    }

    public static String generateCurl(String method, String url, String apiKey,
                                      String token, ActionType action, String... extraParams) {
        StringBuilder curl = new StringBuilder();
        curl.append("curl -X ").append(method).append(" \\\n");
        curl.append("  '").append(url).append("' \\\n");
        curl.append("  -H 'X-Api-Key: ").append(apiKey).append("' \\\n");
        curl.append("  -H 'Content-Type: application/x-www-form-urlencoded' \\\n");
        StringBuilder data = new StringBuilder();
        data.append("token=").append(token);
        data.append("&action=").append(action);
        if (extraParams != null && extraParams.length > 0) {
            for (int i = 0; i < extraParams.length; i += 2) {
                if (i + 1 < extraParams.length) {
                    data.append("&").append(extraParams[i]).append("=").append(extraParams[i + 1]);
                }
            }
        }
        curl.append("  -d '").append(data).append("'");
        return curl.toString();
    }

    private static String formatJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return "<пустое тело ответа>";
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object jsonObject = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        } catch (Exception e) {
            return json;
        }
    }
}