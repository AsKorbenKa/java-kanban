package ru.yandex.practicum.java.kanban.service;

import ru.yandex.practicum.java.kanban.model.Epic;
import ru.yandex.practicum.java.kanban.model.Subtask;
import ru.yandex.practicum.java.kanban.model.Task;
import ru.yandex.practicum.java.kanban.model.Status;
import ru.yandex.practicum.java.kanban.service.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private static HashMap<Integer, Task> tasksMap = new HashMap<>();
    private static HashMap<Integer, Epic> epicsMap = new HashMap<>();
    private static HashMap<Integer, Subtask> subtasksMap = new HashMap<>();

    // Добавляем объект класса Task в HashMap<> taskMap
    @Override
    public void createTask(Task task) {
        if (!tasksMap.containsKey(task.getId())) {
            tasksMap.put(task.getId(), task);
        } else {
            System.out.println("Такая задача в списке Task уже существует.");
        }
    }

    // Добавляем объект класса Epic в HashMap<> epicTasksMap
    @Override
    public void createEpic(Epic epic) {
        if (!epicsMap.containsKey(epic.getId())) {
            epicsMap.put(epic.getId(), epic);
        } else {
            System.out.println("Такая задача в списке Epic уже существует.");
        }
    }

    /* Добавляем объект класса Subtask в HashMap<> subtasksMap,
     также добавляем этот объект класса в список сабтасков объекта класса Epic, к которому subtask относится */
    @Override
    public void createSubtask(Subtask subtask) {
        if (!subtasksMap.containsKey(subtask.getId())) {
            subtasksMap.put(subtask.getId(), subtask);
            int epicId = subtask.getEpicId();
            Epic epic = epicsMap.get(epicId);
            if (!epic.getSubtasksId().contains(subtask.getId())) {
                epic.getSubtasksId().add(subtask.getId());
            }
        } else {
            System.out.println("Такая подзадача в списке уже существует.");
        }
    }

    //Получение списка всех задач типа Task
    @Override
    public List<Task> getAllTasksList() {
        return new ArrayList<>(tasksMap.values());
    }

    //Получение списка всех задач типа Epic
    @Override
    public List<Epic> getAllEpicsList() {
        return new ArrayList<>(epicsMap.values());
    }

    //Получение списка всех задач типа Subtask
    @Override
    public List<Subtask> getAllSubtasksList() {
        return new ArrayList<>(subtasksMap.values());
    }

    // Удаление всех объектов класса Task
    @Override
    public void clearTasksMap() {
        tasksMap.clear();
    }

    // Удаление всех объектов класса Epic. По логике, если удаляются все эпики, то удаляются и их подзадачи
    @Override
    public void clearEpicsMap() {
        epicsMap.clear();
        clearSubtasksMap();
    }

    // Удаление всех объектов класса Subtask. Изменяю статус всех объектов класса Epic на NEW
    @Override
    public void clearSubtasksMap() {
        subtasksMap.clear();
        for (Epic epic : epicsMap.values()) {
            epic.getSubtasksId().clear();
            epic.setStatus(Status.NEW);
        }
    }

    /* Получение задачи по идентификатору. Если я правильно понимаю, класс Task является суперклассом,
    потому его можно использовать в качестве типа для себя и своих подклассов. */
    @Override
    public Task getTaskById(int id) {
        Task task;
        if (tasksMap.containsKey(id)) {
            task = tasksMap.get(id);
        } else if (epicsMap.containsKey(id)) {
            task = epicsMap.get(id);
        } else if (subtasksMap.containsKey(id)) {
            task = subtasksMap.get(id);
        } else {
            task = null;
            System.out.println("Задачи по такому id нет");
        }
        // добавляем в историю просмотров задачу
        if (!task.equals(null)) {
            HistoryManager historyManager = Managers.getDefaultHistory();
            historyManager.add(task);
        }
        return task;
    }

    // обновляем старую версию объекта на новую, используя объект класса Task.
    @Override
    public void replaceTask(Task task) {
        final int id = task.getId();
        final Task savedTask = tasksMap.get(id);
        if (savedTask == null) {
            return;
        }
        tasksMap.replace(id, task);
    }

    // обновляем старую версию объекта на новую, используя объект класса Epic.
    @Override
    public void replaceEpic(Epic epic) {
        final int id = epic.getId();
        final Epic savedEpic = epicsMap.get(id);
        if (savedEpic == null) {
            return;
        }
        epicsMap.replace(id, epic);
    }

    // обновляем старую версию объекта на новую, используя объект класса Subtask.
    @Override
    public void replaceSubtask(Subtask subtask) {
        final int id = subtask.getId();
        final Subtask savedSubtask = subtasksMap.get(id);
        if (savedSubtask == null) {
            return;
        }
        subtasksMap.replace(id, subtask);
    }

    /* удаление по идентификатору. При удалении объекта класса Epic, сначала обращаемся к списку подзадач этого эпика,
    используя id каждого объекта класса SubTask, удаляем все эти объекты из общей HasMap класса SubTask,
    после чего удаляем сам Epic */
    @Override
    public void removeTaskById(Integer integer) {
        if (tasksMap.containsKey(integer)) {
            tasksMap.remove(integer);
        } else if (epicsMap.containsKey(integer)) {
            List<Integer> subTasks = epicsMap.get(integer).getSubtasksId();
            if (!subTasks.isEmpty()) {
                for (Integer subTask : subTasks) {
                    subtasksMap.remove(subTask);
                }
            }
            epicsMap.remove(integer);
        } else if (subtasksMap.containsKey(integer)) {
            int epicId = subtasksMap.get(integer).getEpicId();
            epicsMap.get(epicId).getSubtasksId().remove(integer);
            subtasksMap.remove(integer);
            updateEpicStatus(epicId);
        } else {
            System.out.println("По такому идентификатору задачи нет.");
        }
    }

    // Возвращаю список подзадач определенного объекта класса Epic
    @Override
    public List<Integer> getAllEpicSubtasks(Epic epic) {
        return epic.getSubtasksId();
    }

    // Изменяем статус для объектов класса Task
    @Override
    public void updateTaskStatus(Task task, Status status) {
        task.setStatus(status);
    }

    // Изменяем статус для объектов класса Subtask, вызываем метод для проверки и смены статуса для Epic
    @Override
    public void updateSubtaskStatus(Subtask subtask, Status status) {
        subtask.setStatus(status);
        updateEpicStatus(subtask.getEpicId());
    }

    // Изменяем статус для объектов класса SubTask, проверяем и меняем статус Epic
    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = epicsMap.get(epicId);
        List<Integer> subtasks = epic.getSubtasksId();
        List<Status> statuses = new ArrayList<>();
        if (!subtasks.isEmpty()) {
            for (Integer st : subtasks) {
                if (subtasksMap.get(st).getStatus().equals(Status.IN_PROGRESS)) {
                    epic.setStatus(Status.IN_PROGRESS);
                } else {
                    statuses.add(subtasksMap.get(st).getStatus());
                }
            }
        } else {
            epic.setStatus(Status.NEW);
        }

        if (statuses.size() == subtasks.size()) {
            epic.setStatus(Status.DONE);
        }
    }
}