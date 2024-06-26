package ru.yandex.practicum.java.kanban.service;

import ru.yandex.practicum.java.kanban.model.Epic;
import ru.yandex.practicum.java.kanban.model.Subtask;
import ru.yandex.practicum.java.kanban.model.Task;
import ru.yandex.practicum.java.kanban.model.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private static final HashMap<Integer, Task> tasksMap = new HashMap<>();
    private static final HashMap<Integer, Epic> epicsMap = new HashMap<>();
    private static final HashMap<Integer, Subtask> subtasksMap = new HashMap<>();

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
        // удаление объектов класса Task из истории просмотров
        for (Task task : tasksMap.values()) {
            Managers.getDefaultHistory().remove(task.getId());
        }

        tasksMap.clear();
    }

    // Удаление всех объектов класса Epic. По логике, если удаляются все эпики, то удаляются и их подзадачи
    @Override
    public void clearEpicsMap() {
        // удаление объектов класса Epic из истории просмотров
        for (Epic task : epicsMap.values()) {
            Managers.getDefaultHistory().remove(task.getId());
        }

        epicsMap.clear();
        clearSubtasksMap();
    }

    // Удаление всех объектов класса Subtask. Изменяю статус всех объектов класса Epic на NEW
    @Override
    public void clearSubtasksMap() {
        // удаление объектов класса Subtask из истории просмотров
        for (Subtask task : subtasksMap.values()) {
            Managers.getDefaultHistory().remove(task.getId());
        }

        subtasksMap.clear();
        for (Epic epic : epicsMap.values()) {
            epic.getSubtasksId().clear();
            epic.setStatus(Status.NEW);
        }
    }

    // Получение задачи типа Task по идентификатору
    @Override
    public Task getTaskById(int id) {
        if (tasksMap.get(id) != null) {
            Managers.getDefaultHistory().add(tasksMap.get(id));
        }
        return tasksMap.get(id);
    }

    // Получение задачи типа Epic по идентификатору
    @Override
    public Epic getEpicById(int id) {
        if (epicsMap.get(id) != null) {
            Managers.getDefaultHistory().add(epicsMap.get(id));
        }
        return epicsMap.get(id);
    }

    // Получение задачи типа Subtask по идентификатору
    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasksMap.get(id) != null) {
            Managers.getDefaultHistory().add(subtasksMap.get(id));
        }
        return subtasksMap.get(id);
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

    // удаление по идентификатору.
    @Override
    public void removeTaskById(Integer integer) {
        if (tasksMap.containsKey(integer)) {
            tasksMap.remove(integer);
        } else if (epicsMap.containsKey(integer)) {
            List<Integer> subTasks = epicsMap.get(integer).getSubtasksId();

            if (!subTasks.isEmpty()) {
                for (Integer subTask : subTasks) {
                    // удаление всех сабтасков из истории просмотров при удалении их эпика
                    Managers.getDefaultHistory().remove(subTask);
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
        // удаление задачи из истории промотров
        Managers.getDefaultHistory().remove(integer);
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