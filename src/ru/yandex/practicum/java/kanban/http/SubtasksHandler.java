package ru.yandex.practicum.java.kanban.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.java.kanban.exceptions.TaskNotFoundException;
import ru.yandex.practicum.java.kanban.http.adapters.DurationAdapter;
import ru.yandex.practicum.java.kanban.http.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.java.kanban.model.Subtask;
import ru.yandex.practicum.java.kanban.service.TaskManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtasksHandler extends BaseHttpHandler {
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        if (method.equals("GET") && path.equals("/subtasks")) {
            // Обработка запроса на получение списка всех подзадач
            String response = gson.toJson(taskManager.getAllSubtasksList());
            sendText(exchange, response);
        } else if (method.equals("GET") && path.startsWith("/subtasks/")) {
            getSubtaskById(exchange, path);
        } else if (method.equals("POST") && path.equals("/subtasks")) {
            createOrUpdateSubtask(exchange);
        } else if (method.equals("DELETE") && path.startsWith("/subtasks")) {
            deleteTaskById(exchange, path);
        } else {
            sendNotFound(exchange);
        }
    }

    // Обработка запроса на получение подзадачи по ее id
    private void getSubtaskById(HttpExchange exchange, String path) throws IOException {
        int taskId = Integer.parseInt(path.split("/")[2]);
        try {
            Subtask subtask = taskManager.getSubtaskById(taskId);
            String response = gson.toJson(subtask);
            sendText(exchange, response);
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange);
        }
    }

    // Обработка запроса на добавление подзадачи или ее обновление, если такая уже есть
    private void createOrUpdateSubtask(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();
        Subtask subtask;

        try {
            subtask = gson.fromJson(requestBody, Subtask.class);
        } catch (JsonSyntaxException e) {
            sendNotFound(exchange);
            return;
        }

        if (subtask == null) {
            sendNotFound(exchange);
            return;
        }

        taskManager.createSubtask(subtask);
        sendCreated(exchange);
    }

    // Обработка запроса на удаление подзадачи по ее id
    private void deleteTaskById(HttpExchange exchange, String path) throws IOException {
        int taskId = Integer.parseInt(path.split("/")[2]);
        try {
            sendCreated(exchange);
            taskManager.removeTaskById(taskId);
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange);
        }
    }
}
