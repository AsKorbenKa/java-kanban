package ru.yandex.practicum.java.kanban.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.java.kanban.model.Epic;
import ru.yandex.practicum.java.kanban.model.Subtask;
import ru.yandex.practicum.java.kanban.model.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private static Task task;
    private static Epic epic;
    private static Subtask subtask;


    @BeforeAll
    public static void beforeAll() {
        task = new Task("Задача 1", "Описание 1");
        epic = new Epic("Задача 2", "Описание 2");
        subtask = new Subtask("Задача 3", "Описание 3", epic.getId());

        Managers.getDefault().createTask(task);
        Managers.getDefault().createEpic(epic);
        Managers.getDefault().createSubtask(subtask);
        Managers.getDefault().getTaskById(task.getId());
        Managers.getDefault().getTaskById(epic.getId());
        Managers.getDefault().getTaskById(subtask.getId());
    }

    @Test
    void add() {
        assertEquals(3, Managers.getDefaultHistory().getHistory().size(), "Длина списка равна 0");
    }

    @Test
    void getHistory() {
        assertEquals(task, Managers.getDefaultHistory().getHistory().getFirst(), "Сравниваемые объекты неравны");
    }
}