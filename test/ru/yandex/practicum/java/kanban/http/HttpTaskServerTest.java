package ru.yandex.practicum.java.kanban.http;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.java.kanban.http.adapters.DurationAdapter;
import ru.yandex.practicum.java.kanban.http.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.java.kanban.model.Epic;
import ru.yandex.practicum.java.kanban.model.Subtask;
import ru.yandex.practicum.java.kanban.model.Task;
import ru.yandex.practicum.java.kanban.service.HistoryManager;
import ru.yandex.practicum.java.kanban.service.Managers;
import ru.yandex.practicum.java.kanban.service.TaskManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {
    static HttpTaskServer httpTaskServer;
    static Gson gson;
    static TaskManager taskManager;
    static HistoryManager historyManager;
    private static Task task;
    private static Epic epic;
    private static Subtask subtask;

    @BeforeAll
    public static void beforeAll() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();

        task = new Task("Задача 1", "Описание задачи 1",
                LocalDateTime.of(2024, 7, 26, 22, 7, 0),
                Duration.ofMinutes(20));
        epic = new Epic("Эпик 1", "Описание эпика 1");
        subtask = new Subtask("Подзадача 1", "Описание подзадачи 1",
                LocalDateTime.of(2024, 7, 30, 12, 0, 0),
                Duration.ofMinutes(20), epic.getId());

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @BeforeEach
    public void setUp() throws IOException {
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();

        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());
    }

    @AfterEach
    void tearDown() {
        taskManager.clearTasksMap();
        taskManager.clearEpicsMap();
        taskManager.clearSubtasksMap();
        taskManager.clearTreeSet();

        httpTaskServer.stop();
    }

    // тесты на все методы из новых handler
    @Test
    public void getTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasksList = gson.fromJson(response.body(), taskType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(tasksList, "Список задач не получен");
        assertEquals(taskManager.getAllTasksList(), tasksList, "Получен неверный список задач");
    }

    @Test
    public void getTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task newTask = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode(), "Код ответа не равен 200");
        assertEquals(newTask, task, "Задача отлична от task");
    }

    @Test
    public void createOrUpdateTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        Task task2 = new Task("Задача 2", "Описание задачи 2",
                LocalDateTime.of(2023, 7, 2, 10, 0, 0), Duration.ofMinutes(30));
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(task2));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код ответа не равен 201");
        assertEquals(2, taskManager.getAllTasksList().size(), "Количество задач в списке не равно 2");
    }

    @Test
    public void deleteTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не равен 201");
        assertEquals(0, taskManager.getAllTasksList().size(), "Задача не была удалена");
    }

    @Test
    public void getAllSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> subtasksList = gson.fromJson(response.body(), taskType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(subtasksList, "Список задач не получен");
        assertEquals(taskManager.getAllSubtasksList(), subtasksList, "Получен неверный список задач");
    }

    @Test
    public void getSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task newSubtask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode(), "Код ответа не равен 200");
        assertEquals(newSubtask, subtask, "Задача отлична от subtask");
    }

    @Test
    public void createOrUpdateSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        Subtask subtask2 = new Subtask("Задача 2", "Описание задачи 2",
                LocalDateTime.of(2023, 7, 2, 10, 0, 0),
                Duration.ofMinutes(30), epic.getId());
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код ответа не равен 201");
        assertEquals(2, taskManager.getAllSubtasksList().size(), "Количество задач в списке не равно 2");
    }

    @Test
    public void deleteSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не равен 201");
        assertEquals(0, taskManager.getAllSubtasksList().size(), "Задача не была удалена");
    }

    @Test
    public void getAllEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> epicsList = gson.fromJson(response.body(), taskType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(epicsList, "Список задач не получен");
        assertEquals(taskManager.getAllEpicsList(), epicsList, "Получен неверный список эпиков");
    }

    @Test
    public void getEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic newEpic = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode(), "Код ответа не равен 200");
        assertEquals(newEpic, epic, "Задача отлична от epic");
    }

    @Test
    public void getEpicSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 200");
    }

    @Test
    public void createOrUpdateEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        epic.setName("новое имя эпика");
        epic.setDescription("новое описание эпика");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(epic));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код ответа не равен 201");
        assertEquals(epic, taskManager.getAllEpicsList().getFirst(), "Эпики не равны");
    }

    @Test
    public void deleteEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не равен 201");
        assertEquals(0, taskManager.getAllSubtasksList().size(), "Подзадачи не были удалены");
        assertEquals(0, taskManager.getAllEpicsList().size(), "Эпики не были удалены");
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasksFromHistory = gson.fromJson(response.body(), taskType);
        System.out.println(tasksFromHistory);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
    }

    @Test
    public void getPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        Type taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> prioritizedTasks = gson.fromJson(response.body(), taskType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        // из-за приравнивания всех задач к типу Task, тип класса каждой задачи поменялся на Task соответсвенно
//        assertEquals(taskManager.getPrioritizedTasks(), prioritizedTasks, "Списки приоритетных задач не совпадают");
    }
}