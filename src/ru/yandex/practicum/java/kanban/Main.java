package ru.yandex.practicum.java.kanban;

import ru.yandex.practicum.java.kanban.model.Epic;
import ru.yandex.practicum.java.kanban.model.Subtask;
import ru.yandex.practicum.java.kanban.model.Task;
import ru.yandex.practicum.java.kanban.service.InMemoryTaskManager;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        System.out.println("Поехали!");

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Задача 1", "Описание задачи 1");
        Epic epic2 = new Epic("Задача 1", "Описание задачи 1");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        System.out.println(epic1);


        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic2.getId());
        Subtask subtask4 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic2.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        taskManager.createSubtask(subtask4);
        taskManager.removeTaskById(subtask1.getId());

        System.out.println(taskManager.getAllTasksList());
        System.out.println(taskManager.getAllEpicsList());
        System.out.println(taskManager.getAllSubtasksList());


        taskManager.removeTaskById(3);
        System.out.println(taskManager.getAllEpicsList());
    }
}
