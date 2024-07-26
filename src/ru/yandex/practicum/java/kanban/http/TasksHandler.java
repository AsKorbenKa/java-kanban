package ru.yandex.practicum.java.kanban.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.java.kanban.exceptions.TaskNotFoundException;
import ru.yandex.practicum.java.kanban.http.adapters.DurationAdapter;
import ru.yandex.practicum.java.kanban.http.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.java.kanban.model.Task;
import ru.yandex.practicum.java.kanban.service.TaskManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;

public class TasksHandler extends BaseHttpHandler {
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        if (method.equals("GET") && path.equals("/tasks")) {
            // Обработка запроса на получение списка всех задач
            String response = gson.toJson(taskManager.getAllTasksList());
            sendText(exchange, response);
        } else if (method.equals("GET") && path.startsWith("/tasks/")) {
            getTaskById(exchange, path);
        } else if (method.equals("POST") && path.equals("/tasks")) {
            createOrUpdateTask(exchange);
        } else if (method.equals("DELETE") && path.startsWith("/tasks/")) {
            deleteTaskById(exchange, path);
        } else {
            sendNotFound(exchange);
        }
    }

    // Обработка запроса на получение задачи по ее id
    private void getTaskById(HttpExchange exchange, String path) throws IOException {
        int taskId = Integer.parseInt(path.split("/")[2]);
        try {
            Task task = taskManager.getTaskById(taskId);
            String response = gson.toJson(task);
            sendText(exchange, response);
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange);
        }
    }

    // Обработка запроса на создание новой задачи, обновление задачи, если такая уже есть
    private void createOrUpdateTask(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();
        Task task;

        try {
            task = gson.fromJson(requestBody, Task.class);
        } catch (JsonSyntaxException e) {
            sendNotFound(exchange);
            return;
        }

        if (task == null) {
            sendNotFound(exchange);
            return;
        }

        taskManager.createTask(task);
        sendCreated(exchange);
    }

    // Обработка запроса на удаление задачи по ее id
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
