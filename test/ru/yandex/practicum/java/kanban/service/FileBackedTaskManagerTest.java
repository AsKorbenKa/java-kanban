package ru.yandex.practicum.java.kanban.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.java.kanban.model.Epic;
import ru.yandex.practicum.java.kanban.model.Subtask;
import ru.yandex.practicum.java.kanban.model.Task;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File file;

    @BeforeEach
    public void beforeEach() {
        file = new File("Test.txt");
        FileBackedTaskManager fbtm = new FileBackedTaskManager(file);

        Task task = new Task("Задача 1", "Описание 1");
        fbtm.createTask(task);

        Epic epic = new Epic("Задача 2", "Описание 2");
        fbtm.createEpic(epic);

        Subtask subtask = new Subtask("Задача 3", "Описание 3", epic.getId());
        fbtm.createSubtask(subtask);
    }

    @AfterEach
    public void afterEach() {
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void loadFromFile() {
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(new File("Test.txt"));

        assertEquals(1, Managers.getDefault().getAllTasksList().size(), "Неверное количество task");

        assertEquals(1, Managers.getDefault().getAllEpicsList().size(), "Неверное количество epic");

        assertEquals(1, Managers.getDefault().getAllSubtasksList().size(),
                "Неверное количество subtask");

    }
}