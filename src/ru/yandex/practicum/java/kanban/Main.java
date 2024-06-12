package ru.yandex.practicum.java.kanban;

import ru.yandex.practicum.java.kanban.model.Epic;
import ru.yandex.practicum.java.kanban.model.Subtask;
import ru.yandex.practicum.java.kanban.service.InMemoryTaskManager;
import ru.yandex.practicum.java.kanban.service.Managers;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Задача 1", "Описание задачи 1");
        Epic epic2 = new Epic("Задача 2", "Описание задачи 2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Задача 3", "Описание задачи 3", epic1.getId());
        Subtask subtask2 = new Subtask("Задача 4", "Описание задачи 4", epic1.getId());
        Subtask subtask3 = new Subtask("Задача 5", "Описание задачи 5", epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        // 1-й просмотр задач
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getSubtaskById(subtask1.getId());
        System.out.println(Managers.getDefaultHistory().getHistory());

        // 2-й просмотр задач
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getEpicById(epic1.getId());
        System.out.println(Managers.getDefaultHistory().getHistory());

        // удаление задачи и вывод истории просмотров
        taskManager.removeTaskById(epic2.getId());
        System.out.println(Managers.getDefaultHistory().getHistory());

        // удаление эпика с подзадачами и вывод результата
        taskManager.removeTaskById(epic1.getId());
        System.out.println(Managers.getDefaultHistory().getHistory());
    }
}
