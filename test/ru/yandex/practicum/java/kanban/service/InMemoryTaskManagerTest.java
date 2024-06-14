package ru.yandex.practicum.java.kanban.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.java.kanban.model.Epic;
import ru.yandex.practicum.java.kanban.model.Status;
import ru.yandex.practicum.java.kanban.model.Subtask;
import ru.yandex.practicum.java.kanban.model.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {
    private static Task task;
    private static Epic epic;
    private static Subtask subtask;

    @BeforeAll
    public static void beforeEach() {
        task = new Task("Задача 1", "Описание 1");
        epic = new Epic("Задача 2", "Описание 2");
        subtask = new Subtask("Задача 3", "Описание 3", epic.getId());

        Managers.getDefault().createTask(task);
        Managers.getDefault().createEpic(epic);
        Managers.getDefault().createSubtask(subtask);
    }

    @AfterEach
    public void afterEach() {
        if (Managers.getDefault().getAllTasksList().isEmpty()) {
            Managers.getDefault().createTask(task);
        }

        if (Managers.getDefault().getAllEpicsList().isEmpty()) {
            Managers.getDefault().createEpic(epic);
        }

        if (Managers.getDefault().getAllSubtasksList().isEmpty()) {
            Managers.getDefault().createSubtask(subtask);
        }
    }

    @AfterAll
    public static void afterAll() {
        Managers.getDefault().removeTaskById(task.getId());
        Managers.getDefault().removeTaskById(epic.getId());
    }

    @Test
    void createTask() {
        assertEquals(task, Managers.getDefault().getAllTasksList().getFirst());
    }

    @Test
    void createEpic() {
        assertEquals(epic, Managers.getDefault().getAllEpicsList().getFirst());
    }

    @Test
    void createSubtask() {
        assertEquals(subtask, Managers.getDefault().getAllSubtasksList().getFirst());
    }

    @Test
    void clearTasksMap() {
        Managers.getDefault().clearTasksMap();
        assertEquals(0, Managers.getDefault().getAllTasksList().size());
    }

    @Test
    void clearEpicsMap() {
        Managers.getDefault().clearEpicsMap();
        assertEquals(0, Managers.getDefault().getAllEpicsList().size());
    }

    @Test
    void clearSubtasksMap() {
        Managers.getDefault().clearSubtasksMap();
        assertEquals(0, Managers.getDefault().getAllSubtasksList().size());
    }

    @Test
    void getTaskById() {
        assertEquals(task, Managers.getDefault().getTaskById(task.getId()));
    }

    @Test
    void getEpicById() {
        assertEquals(epic, Managers.getDefault().getEpicById(epic.getId()));
    }

    @Test
    void getSubtaskById() {
        assertEquals(subtask, Managers.getDefault().getSubtaskById(subtask.getId()));
    }

    @Test
    void replaceTask() {
        task.setName("Задача 69");
        task.setDiscription("Описание 69");
        Managers.getDefault().replaceTask(task);
        assertEquals(task, Managers.getDefault().getAllTasksList().getFirst());
    }

    @Test
    void replaceEpic() {
        epic.setName("Задача 228");
        epic.setDiscription("Описание 228");
        Managers.getDefault().replaceEpic(epic);
        assertEquals(epic, Managers.getDefault().getAllEpicsList().getFirst());
    }

    @Test
    void replaceSubtask() {
        subtask.setName("Задача 1111");
        subtask.setDiscription("Описание 1111");
        Managers.getDefault().replaceSubtask(subtask);
        assertEquals(subtask, Managers.getDefault().getAllSubtasksList().getFirst());
    }

    @Test
    void removeTaskById() {
        Managers.getDefault().removeTaskById(subtask.getId());
        assertEquals(0, Managers.getDefault().getAllSubtasksList().size());
    }

    @Test
    void getAllEpicSubtasks() {
        Subtask subtask1 = new Subtask("Задача 10", "Описание 10", epic.getId());
        Managers.getDefault().createSubtask(subtask1);
        assertEquals(2, Managers.getDefault().getAllEpicSubtasks(epic).size());
    }

    @Test
    void updateTaskStatus() {
        Managers.getDefault().updateTaskStatus(task, Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }

    @Test
    void updateSubtaskStatus() {
        Managers.getDefault().updateSubtaskStatus(subtask, Status.DONE);
        assertEquals(Status.DONE, subtask.getStatus());
    }

    @Test
    void updateEpicStatus() {
        Managers.getDefault().updateSubtaskStatus(subtask, Status.DONE);
        Managers.getDefault().updateEpicStatus(epic.getId());
        assertEquals(Status.DONE, epic.getStatus());
    }
}