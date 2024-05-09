package ru.yandex.practicum.java.kanban.service;

import ru.yandex.practicum.java.kanban.model.Epic;
import ru.yandex.practicum.java.kanban.model.Subtask;
import ru.yandex.practicum.java.kanban.model.Task;
import ru.yandex.practicum.java.kanban.model.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasksMap = new HashMap<>();
    private HashMap<Integer, Epic> epicsMap = new HashMap<>();
    private HashMap<Integer, Subtask> subtasksMap = new HashMap<>();

    // Добавляем объект класса Task в HashMap<> taskMap
    public void createTask(Task task) {
        if (!tasksMap.containsKey(task.getId())) {
            tasksMap.put(task.getId(), task);
        } else {
            System.out.println("Такая задача в списке Task уже существует.");
        }
    }

    // Добавляем объект класса Epic в HashMap<> epicTasksMap
    public void createEpic(Epic epic) {
        if (!epicsMap.containsKey(epic.getId())) {
            epicsMap.put(epic.getId(), epic);
        } else {
            System.out.println("Такая задача в списке Epic уже существует.");
        }
    }

    /* Добавляем объект класса Subtask в HashMap<> subtasksMap,
     также добавляем этот объект класса в список сабтасков объекта класса Epic, к которому subtask относится */
    public void createSubTask(Subtask subtask) {
        if (!subtasksMap.containsKey(subtask.getId())) {
            subtasksMap.put(subtask.getId(), subtask);
            int epicId = subtask.getEpicId();
            Epic epic = epicsMap.get(epicId);
            epic.getSubtasksId().add(subtask.getId());
        } else {
            System.out.println("Такая подзадача в списке уже существует.");
        }
    }

    //Получение списка всех задач типа Task
    public ArrayList<Task> getAllTasksList() {
        return new ArrayList<>(tasksMap.values());
    }

    //Получение списка всех задач типа Epic
    public ArrayList<Epic> getAllEpicsList() {
        return new ArrayList<>(epicsMap.values());
    }

    //Получение списка всех задач типа Subtask
    public ArrayList<Subtask> getAllSubtasksList() {
        return new ArrayList<>(subtasksMap.values());
    }

    // Удаление всех объектов класса Task
    public void clearTasksMap() {
        tasksMap.clear();
    }

    // Удаление всех объектов класса Epic. По логике, если удаляются все эпики, то удаляются и их подзадачи
    public void clearEpicsMap() {
        epicsMap.clear();
        clearSubtasksMap();
    }

    // Удаление всех объектов класса Subtask. Если я правильно понимаю, то удаление подзадач не значит удаление эпика
    public void clearSubtasksMap() {
        subtasksMap.clear();
    }

    /* Получение задачи по идентификатору. Если я правильно понимаю, класс Task является суперклассом,
    потому его можно использовать в качестве типа для себя и своих подклассов. */
    public Task getTaskById(Integer id) {
        Task object;
        if (tasksMap.containsKey(id)) {
            object = tasksMap.get(id);
        } else if (epicsMap.containsKey(id)) {
            object = epicsMap.get(id);
        } else if (subtasksMap.containsKey(id)) {
            object = subtasksMap.get(id);
        } else {
            object = null;
            System.out.println("Задачи по такому id нет");
        }
        return object;
    }

    /* обновляем старую версию объекта на новую, используя объект класса Task.
    Это единственно решение, до которого я додумался */
    public void replaceTask(Task task) {
        int id = -1;
        for (Integer integer : tasksMap.keySet()) {
            if (tasksMap.get(integer).equals(task)) {
                id = integer;
            }
        }
        if (id != -1) {
            tasksMap.remove(id);
            tasksMap.put(task.getId(), task);
        }
    }

    // обновляем старую версию объекта на новую, используя объект класса Epic.
    public void replaceEpic(Epic epic) {
        int id = -1;
        for (Integer integer : epicsMap.keySet()) {
            if (epicsMap.get(integer).equals(epic)) {
                id = integer;
            }
        }
        if (id != -1) {
            epicsMap.remove(id);
            epicsMap.put(epic.getId(), epic);
        }
    }

    // обновляем старую версию объекта на новую, используя объект класса SubTask.
    public void replaceSubtask(Subtask subTask) {
        int id = -1;
        for (Integer integer : subtasksMap.keySet()) {
            if (subtasksMap.get(integer).equals(subTask)) {
                id = integer;
            }
        }
        if (id != -1) {
            subtasksMap.remove(id);
            subtasksMap.put(subTask.getId(), subTask);
        }
    }

    /* удаление по идентификатору. При удалении объекта класса Epic, сначала обращаемся к списку подзадач этого эпика,
    используя id каждого объекта класса SubTask, удаляем все эти объекты из общей HasMap класса SubTask,
    после чего удаляем сам Epic */
    public void removeTaskById(Integer integer) {
        if (tasksMap.containsKey(integer)) {
            tasksMap.remove(integer);
        } else if (epicsMap.containsKey(integer)) {
            ArrayList<Integer> subTasks = epicsMap.get(integer).getSubtasksId();
            if (!subTasks.isEmpty()) {
                for (Integer subTask : subTasks) {
                    subtasksMap.remove(subTask);
                }
            }
            epicsMap.remove(integer);
        } else if (subtasksMap.containsKey(integer)) {
            subtasksMap.remove(integer);
        } else {
            System.out.println("По такому идентификатору задачи нет.");
        }
    }

    // Возвращаю список подзадач определенного объекта класса Epic
    public ArrayList<Integer> getAllEpicSubtasks(Epic epic) {
        return epic.getSubtasksId();
    }

    // Изменяем статус для объектов класса Task
    public void updateTaskStatus(Task task, Status status) {
        task.setStatus(status);
    }

    // Изменяем статус для объектов класса Subtask, вызываем метод для проверки и смены статуса для Epic
    public void updateSubtaskStatus(Subtask subtask, Status status) {
        subtask.setStatus(status);
        updateEpicStatus(subtask.getEpicId());
    }

    // Изменяем статус для объектов класса SubTask, проверяем и меняем статус Epic
    private void updateEpicStatus(int epicId) {
        Epic epic = epicsMap.get(epicId);
        ArrayList<Integer> subtasks = epic.getSubtasksId();
        ArrayList<Status> statuses = new ArrayList<>();
        if (!subtasks.isEmpty()) {
            for (Integer st : subtasks) {
                if (subtasksMap.get(st).getStatus().equals(Status.IN_PROGRESS)) {
                    epic.setStatus(Status.IN_PROGRESS);
                } else {
                    statuses.add(subtasksMap.get(st).getStatus());
                }
            }
        }

        if (statuses.size() == subtasks.size()) {
            epic.setStatus(Status.DONE);
        }
    }
}