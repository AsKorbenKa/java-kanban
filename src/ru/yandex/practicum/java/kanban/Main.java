package ru.yandex.practicum.java.kanban;

import ru.yandex.practicum.java.kanban.model.Epic;
import ru.yandex.practicum.java.kanban.model.Subtask;
import ru.yandex.practicum.java.kanban.model.Task;
import ru.yandex.practicum.java.kanban.service.TaskManager;
import ru.yandex.practicum.java.kanban.model.Status;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
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
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);
        taskManager.createSubTask(subtask4);

        System.out.println(taskManager.getAllTasksList());
        System.out.println(taskManager.getAllEpicsList());
        System.out.println(taskManager.getAllSubtasksList());


        taskManager.removeTaskById(3);
        System.out.println(taskManager.getAllEpicsList());
    }
}
