package ru.yandex.practicum.java.kanban.service;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.java.kanban.model.Epic;
import ru.yandex.practicum.java.kanban.model.Status;
import ru.yandex.practicum.java.kanban.model.Subtask;
import ru.yandex.practicum.java.kanban.model.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskManagerTest {
    static InMemoryTaskManager taskManager;
    private static Task task;
    private static Epic epic;
    private static Subtask subtask;
    private static Subtask subtask1;

    @BeforeAll
    public static void beforeAll() {
        taskManager = new InMemoryTaskManager();
        taskManager.clearTreeSet();
        task = new Task("Задача 1", "Описание 1");
        epic = new Epic("Задача 2", "Описание 2");
        subtask = new Subtask("Задача 3", "Описание 3",
                LocalDateTime.now(), null, epic.getId());
        subtask1 = new Subtask("Задача 4", "Описание 4",
                LocalDateTime.of(2020, 1, 1, 1, 1), null, epic.getId());
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask1);
    }

    @BeforeEach
    public void beforeEach() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask1);
    }

    @AfterEach
    public void afterEach() {
        taskManager.clearTasksMap();
        taskManager.clearSubtasksMap();
        taskManager.clearEpicsMap();
        taskManager.clearTreeSet();
    }

    @Test
    void createTaskAndClearTasksMap() {
        Assertions.assertEquals(task, taskManager.getAllTasksList().getFirst());

        taskManager.clearTasksMap();
        Assertions.assertEquals(0, taskManager.getAllTasksList().size());
    }

    @Test
    void createEpicAndClearEpicsMap() {
        Assertions.assertEquals(epic, taskManager.getAllEpicsList().getFirst());

        taskManager.clearEpicsMap();
        Assertions.assertEquals(0, taskManager.getAllEpicsList().size());
    }

    @Test
    void createSubtaskAndClearSubtasksMap() {
        Assertions.assertEquals(2, taskManager.getAllSubtasksList().size());

        taskManager.clearSubtasksMap();
        Assertions.assertEquals(0, taskManager.getAllSubtasksList().size());
    }

    @Test
    void getTaskById() {
        Assertions.assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
    void getEpicById() {
        Assertions.assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    void getSubtaskById() {
        Assertions.assertEquals(subtask1, taskManager.getSubtaskById(subtask1.getId()));
    }

    @Test
    void replaceTask() {
        task.setName("Задача 69");
        task.setDescription("Описание 69");
        taskManager.replaceTask(task);
        Assertions.assertEquals(task, taskManager.getAllTasksList().getFirst());
    }

    @Test
    void replaceEpic() {
        epic.setName("Задача 228");
        epic.setDescription("Описание 228");
        taskManager.replaceEpic(epic);
        Assertions.assertEquals(epic, taskManager.getAllEpicsList().getFirst());
    }

    @Test
    void replaceSubtask() {
        subtask.setName("Задача 1111");
        subtask.setDescription("Описание 1111");
        taskManager.replaceSubtask(subtask);
        Assertions.assertEquals(subtask, taskManager.getAllSubtasksList().getFirst());
    }

    @Test
    void removeTaskById() {
        taskManager.removeTaskById(subtask.getId());
        Assertions.assertEquals(1, taskManager.getAllSubtasksList().size());
    }

    @Test
    void getAllEpicSubtasks() {
        Assertions.assertEquals(2, taskManager.getAllEpicSubtasks(epic).size());
    }

    @Test
    void updateTaskStatus() {
        taskManager.updateTaskStatus(task, Status.IN_PROGRESS);
        Assertions.assertEquals(Status.IN_PROGRESS, task.getStatus());
    }

    @Test
    void updateSubtaskStatus() {
        taskManager.updateSubtaskStatus(subtask, Status.DONE);
        Assertions.assertEquals(Status.DONE, subtask.getStatus());
    }

    @Test
    void updateEpicStatus() {
        taskManager.updateSubtaskStatus(subtask, Status.NEW);
        taskManager.updateSubtaskStatus(subtask1, Status.NEW);
        taskManager.updateEpicStatus(epic.getId());
        Assertions.assertEquals(Status.NEW, epic.getStatus());

        taskManager.updateSubtaskStatus(subtask, Status.DONE);
        taskManager.updateSubtaskStatus(subtask1, Status.DONE);
        taskManager.updateEpicStatus(epic.getId());
        Assertions.assertEquals(Status.DONE, epic.getStatus());

        taskManager.updateSubtaskStatus(subtask, Status.NEW);
        taskManager.updateSubtaskStatus(subtask1, Status.DONE);
        taskManager.updateEpicStatus(epic.getId());
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());

        taskManager.updateSubtaskStatus(subtask, Status.IN_PROGRESS);
        taskManager.updateSubtaskStatus(subtask1, Status.IN_PROGRESS);
        taskManager.updateEpicStatus(epic.getId());
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void getPrioritizedTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(subtask1);
        tasks.add(subtask);
        Assertions.assertEquals(tasks, taskManager.getPrioritizedTasks());
    }

    @Test
    public void checkIntersectionOfTasks() {
        Assertions.assertFalse(taskManager.checkIntersectionOfTasks(subtask1));
    }
}