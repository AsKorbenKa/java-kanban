package ru.yandex.practicum.java.kanban.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.java.kanban.exceptions.TaskNotFoundException;
import ru.yandex.practicum.java.kanban.http.adapters.DurationAdapter;
import ru.yandex.practicum.java.kanban.http.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.java.kanban.model.Epic;
import ru.yandex.practicum.java.kanban.service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class EpicsHandler extends BaseHttpHandler {
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        if (method.equals("GET") && path.equals("/epics")) {
            // Обработка запроса на получение списка всех эпиков
            String response = gson.toJson(taskManager.getAllEpicsList());
            sendText(exchange, response);
        } else if (method.equals("GET") && path.startsWith("/epics/")) {
            getEpicById(exchange, path);
        } else if (method.equals("GET") && path.endsWith("/subtasks")) {
            getEpicSubtasks(exchange, path);
        } else if (method.equals("POST") && path.equals("/epics")) {
            createOrUpdateEpic(exchange);
        } else if (method.equals("DELETE") && path.startsWith("/epics")) {
            deleteTaskById(exchange, path);
        } else {
            sendNotFound(exchange);
        }
    }

    // Обработка запроса на получение эпика по его id
    private void getEpicById(HttpExchange exchange, String path) throws IOException {
        int epicId = Integer.parseInt(path.split("/")[2]);
        try {
            Epic epic = taskManager.getEpicById(epicId);
            String response = gson.toJson(epic);
            sendText(exchange, response);
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange);
        }
    }

    // Обработка запроса на получение списка всех подзадач эпика
    private void getEpicSubtasks(HttpExchange exchange, String path) throws IOException {
        int epicId = Integer.parseInt(path.split("/")[2]);
        try {
            Epic epic = taskManager.getEpicById(epicId);
            String response = gson.toJson(epic.getSubtasksId());
            sendText(exchange, response);
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange);
        }
    }

    // Обработка запроса на добавление нового эпика или обновление старого
    private void createOrUpdateEpic(HttpExchange exchange) throws IOException {
        sendCreated(exchange);
        Epic epic = gson.fromJson(exchange.getRequestBody().toString(), Epic.class);
        taskManager.createEpic(epic);
    }

    // Обработка запроса на удаление эпика по его id
    private void deleteTaskById(HttpExchange exchange, String path) throws IOException {
        int taskId = Integer.parseInt(path.split("/")[2]);
        try {
            taskManager.removeTaskById(taskId);
            sendCreated(exchange);
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange);
        }
    }
}
