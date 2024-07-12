package ru.yandex.practicum.java.kanban.service;

import ru.yandex.practicum.java.kanban.model.Epic;
import ru.yandex.practicum.java.kanban.model.Status;
import ru.yandex.practicum.java.kanban.model.Subtask;
import ru.yandex.practicum.java.kanban.model.Task;

import java.util.List;

public interface TaskManager {
    // Добавляем объект класса Task в HashMap<> taskMap
    void createTask(Task task);

    // Добавляем объект класса Epic в HashMap<> epicTasksMap
    void createEpic(Epic epic);

    /* Добавляем объект класса Subtask в HashMap<> subtasksMap,
         также добавляем этот объект класса в список сабтасков объекта класса Epic, к которому subtask относится */
    void createSubtask(Subtask subtask);

    //Получение списка всех задач типа Task
    List<Task> getAllTasksList();

    //Получение списка всех задач типа Epic
    List<Epic> getAllEpicsList();

    //Получение списка всех задач типа Subtask
    List<Subtask> getAllSubtasksList();

    // Удаление всех объектов класса Task
    void clearTasksMap();

    // Удаление всех объектов класса Epic. По логике, если удаляются все эпики, то удаляются и их подзадачи
    void clearEpicsMap();

    // Удаление всех объектов класса Subtask. Изменяю статус всех объектов класса Epic на NEW
    void clearSubtasksMap();

    /* Получение задачи по идентификатору. Если я правильно понимаю, класс Task является суперклассом,
        потому его можно использовать в качестве типа для себя и своих подклассов. */
    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    // обновляем старую версию объекта на новую, используя объект класса Task.
    void replaceTask(Task task);

    // обновляем старую версию объекта на новую, используя объект класса Epic.
    void replaceEpic(Epic epic);

    // обновляем старую версию объекта на новую, используя объект класса Subtask.
    void replaceSubtask(Subtask subtask);

    /* удаление по идентификатору. При удалении объекта класса Epic, сначала обращаемся к списку подзадач этого эпика,
        используя id каждого объекта класса SubTask, удаляем все эти объекты из общей HasMap класса SubTask,
        после чего удаляем сам Epic */
    void removeTaskById(Integer integer);

    // Возвращаю список подзадач определенного объекта класса Epic
    List<Integer> getAllEpicSubtasks(Epic epic);

    // Изменяем статус для объектов класса Task
    void updateTaskStatus(Task task, Status status);

    // Изменяем статус для объектов класса Subtask, вызываем метод для проверки и смены статуса для Epic
    void updateSubtaskStatus(Subtask subtask, Status status);

    // Изменяем статус для объектов класса SubTask, проверяем и меняем статус Epic
    void updateEpicStatus(int epicId);

    List<Task> getPrioritizedTasks();

    void addTaskToTreeSet(Task task);

    boolean checkIntersectionOfTasks(Task task);
}
