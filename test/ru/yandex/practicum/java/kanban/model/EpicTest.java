package ru.yandex.practicum.java.kanban.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.java.kanban.service.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic;
    Subtask subtask;
    Subtask subtask1;
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @AfterEach
    public void afterEach() {
        taskManager.clearTasksMap();
        taskManager.clearSubtasksMap();
        taskManager.clearEpicsMap();
    }

    @Test
    public void getEndTime() {
        epic = new Epic("Задача 2", "Описание 2");
        subtask = new Subtask("Задача 3", "Описание 3",
                LocalDateTime.of(2024, 7, 13, 12, 20), Duration.ofMinutes(22),
                epic.getId());
        subtask1 = new Subtask("Задача 4", "Описание 4",
                LocalDateTime.of(2020, 7, 28, 13, 15), Duration.ZERO, epic.getId());
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask1);

        Assertions.assertEquals(LocalDateTime.of(2024, 7, 13, 12, 42),
                epic.getEndTime());
    }

}