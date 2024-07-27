package ru.yandex.practicum.java.kanban.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.java.kanban.http.adapters.DurationAdapter;
import ru.yandex.practicum.java.kanban.http.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.java.kanban.service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class PrioritizedHandler extends BaseHttpHandler {
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        if (method.equals("GET") && path.equals("/prioritized")) {
            // Обработка запроса на получение списка приоритетных задач
            String response = gson.toJson(taskManager.getPrioritizedTasks());
            sendText(exchange, response);
        } else {
            sendNotFound(exchange);
        }
    }
}
