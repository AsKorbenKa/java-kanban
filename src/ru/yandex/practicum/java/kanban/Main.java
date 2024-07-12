package ru.yandex.practicum.java.kanban;

import ru.yandex.practicum.java.kanban.model.Epic;
import ru.yandex.practicum.java.kanban.model.Subtask;
import ru.yandex.practicum.java.kanban.service.FileBackedTaskManager;
import ru.yandex.practicum.java.kanban.service.InMemoryTaskManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        File file = File.createTempFile("TempFile", null);
        FileBackedTaskManager tm = new FileBackedTaskManager(file);

        Epic epic1 = new Epic("Задача 1", "Описание задачи 1");
        Epic epic2 = new Epic("Задача 2", "Описание задачи 2");
        tm.createEpic(epic1);
        tm.createEpic(epic2);

        Subtask subtask1 = new Subtask("Задача 3", "Описание задачи 3", LocalDateTime.now(),
                Duration.ofMinutes(120), epic1.getId());
        Subtask subtask2 = new Subtask("Задача 4", "Описание задачи 4", LocalDateTime.now(),
                Duration.ofMinutes(80), epic1.getId());
        Subtask subtask3 = new Subtask("Задача 5", "Описание задачи 5", null,
                null, epic1.getId());
        tm.createSubtask(subtask1);
        tm.createSubtask(subtask2);
        tm.createSubtask(subtask3);

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            List<String> lines = br.lines().toList();
            for (String line : lines) {
                System.out.println(line);
            }
        }
        /*
        Результат:
        id,type,name,status,description,epic
        1,EPIC,Задача 1,NEW,Описание задачи 1
        2,EPIC,Задача 2,NEW,Описание задачи 2
        3,SUBTASK,Задача 3,NEW,Описание задачи 3,1
        4,SUBTASK,Задача 4,NEW,Описание задачи 4,1
        5,SUBTASK,Задача 5,NEW,Описание задачи 5,1
         */

    }
}
