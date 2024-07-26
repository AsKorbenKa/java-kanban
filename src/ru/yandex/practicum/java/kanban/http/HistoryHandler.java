package ru.yandex.practicum.java.kanban.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.java.kanban.http.adapters.DurationAdapter;
import ru.yandex.practicum.java.kanban.http.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.java.kanban.service.HistoryManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HistoryHandler extends BaseHttpHandler {
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    HistoryManager historyManager;

    public HistoryHandler(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        if (method.equals("GET") && path.equals("/history")) {
            // Обработка запроса на получение истории просмотров задач
            String response = gson.toJson(historyManager.getHistory());
            sendText(exchange, response);
        } else {
            sendNotFound(exchange);
        }
    }
}
