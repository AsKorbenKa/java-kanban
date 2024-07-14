package ru.yandex.practicum.java.kanban.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.java.kanban.model.Epic;
import ru.yandex.practicum.java.kanban.model.Subtask;
import ru.yandex.practicum.java.kanban.model.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private static File file;
    Epic epic;
    Task task;
    Subtask subtask;
    Subtask subtask1;
    static FileBackedTaskManager fbtm;

    @AfterEach
    public void afterEach() {
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void loadFromFile() {
        file = new File("Test.txt");
        fbtm = new FileBackedTaskManager(file);

        task = new Task("Задача 1", "Описание 1");
        epic = new Epic("Задача 2", "Описание 2");
        subtask = new Subtask("Задача 3", "Описание 3", LocalDateTime.now(),
                Duration.ofMinutes(22), epic.getId());
        subtask1 = new Subtask("Задача 4", "Описание 4",
                LocalDateTime.of(2020, 7, 28, 13, 15), Duration.ZERO, epic.getId());
        fbtm.createTask(task);
        fbtm.createEpic(epic);
        fbtm.createSubtask(subtask);
        fbtm.createSubtask(subtask1);

        FileBackedTaskManager.loadFromFile(new File("Test.txt"));

        assertEquals(1, Managers.getDefault().getAllTasksList().size(), "Неверное количество task");

        assertEquals(1, Managers.getDefault().getAllEpicsList().size(), "Неверное количество epic");
        System.out.println(epic);

        assertEquals(2, Managers.getDefault().getAllSubtasksList().size(),
                "Неверное количество subtask");
    }

    // данный тест, по идее, пишется так. Просто я не знаю как саботировать программу,
    // чтобы вылезла ошибка ManagerSaveException
//    @Test
//    public void testException() {
//        task = new Task("Задача 1", "Описание 1");
//        epic = new Epic("Задача 2", "Описание 2");
//        subtask = new Subtask("Задача 3", "Описание 3", LocalDateTime.now(),
//                Duration.ofMinutes(22), epic.getId());
//        subtask1 = new Subtask("Задача 4", "Описание 4",
//
//                LocalDateTime.of(2020, 7, 28, 13, 15), Duration.ZERO, epic.getId());
//        assertThrows(ManagerSaveException.class, () -> {
//            fbtm.createTask(task);
//            fbtm.createEpic(epic);
//            fbtm.createSubtask(subtask);
//            fbtm.createSubtask(subtask1);
//        }, "Ошибка при записи задач в файл");
//    }
}