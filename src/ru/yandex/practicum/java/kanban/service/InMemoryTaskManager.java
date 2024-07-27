package ru.yandex.practicum.java.kanban.service;

import ru.yandex.practicum.java.kanban.exceptions.TaskIntersectionError;
import ru.yandex.practicum.java.kanban.exceptions.TaskNotFoundException;
import ru.yandex.practicum.java.kanban.model.Epic;
import ru.yandex.practicum.java.kanban.model.Subtask;
import ru.yandex.practicum.java.kanban.model.Task;
import ru.yandex.practicum.java.kanban.model.Status;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private static final HashMap<Integer, Task> tasksMap = new HashMap<>();
    private static final HashMap<Integer, Epic> epicsMap = new HashMap<>();
    private static final HashMap<Integer, Subtask> subtasksMap = new HashMap<>();
    private static final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    public static final Logger logger = Logger.getLogger(InMemoryTaskManager.class.getName());

    // Добавляем объект класса Task в HashMap<> taskMap
    @Override
    public void createTask(Task task) {
        if (!tasksMap.containsKey(task.getId())) {
            tasksMap.put(task.getId(), task);
            //если время выполнения задачи не пересекается с временем любой другой задачи, то добавляем в TreeSet
            //иначе выводим предупреждение
            if (task.getStartTime() != null) {
                if (checkIntersectionOfTasks(task)) {
                    addTaskToTreeSet(task);
                } else {
                    // если время выполнения задачи пересекается с временем другой задачи, то выбрасываем ошибку
                    logger.warning("Время задачи (" + task.getName() + ") пересекается с временем другой задачи. " +
                            "Необходимо изменить время начала выполнения и/или продолжительность выполнения");
                    throw new TaskIntersectionError();
                }
            }
        } else {
            // если задача уже есть в списке задач, то перенаправляю ее для обновления данных в самой задаче
            logger.warning("Такая задача в списке Task уже существует. Обновляем данные задачи.");
            replaceTask(task);
        }
    }

    // Добавляем объект класса Epic в HashMap<> epicTasksMap
    @Override
    public void createEpic(Epic epic) {
        if (!epicsMap.containsKey(epic.getId())) {
            epicsMap.put(epic.getId(), epic);
        } else {
            logger.warning("Такая задача в списке Epic уже существует. Обновляем данные задачи.");
            replaceEpic(epic);
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
            setEpicDateTime(subtask, epic);
        } else {
            logger.warning("Такая подзадача в списке уже существует. Обновляем данные задачи.");
            replaceSubtask(subtask);
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
        tasksMap.values()
                .forEach(task -> Managers.getDefaultHistory().remove(task.getId()));

        tasksMap.clear();
    }

    // Удаление всех объектов класса Epic. По логике, если удаляются все эпики, то удаляются и их подзадачи
    @Override
    public void clearEpicsMap() {
        // удаление объектов класса Epic из истории просмотров
        epicsMap.values()
                .forEach(epic -> Managers.getDefaultHistory().remove(epic.getId()));

        epicsMap.clear();
        clearSubtasksMap();
    }

    // Удаление всех объектов класса Subtask. Изменяю статус всех объектов класса Epic на NEW
    @Override
    public void clearSubtasksMap() {
        // удаление объектов класса Subtask из истории просмотров
        subtasksMap.values()
                .forEach(subtask -> Managers.getDefaultHistory().remove(subtask.getId()));

        subtasksMap.clear();

        epicsMap.values()
                .forEach(epic -> {
                    epic.getSubtasksId().clear();
                    epic.setStatus(Status.NEW);
                });

    }

    // Получение задачи типа Task по идентификатору
    @Override
    public Task getTaskById(int id) {
        try {
            if (tasksMap.get(id) != null) {
                Managers.getDefaultHistory().add(tasksMap.get(id));
            } else {
                // Чтобы реализовать throwable в эксепшенах, выбрасываю ошибку и тут же ее отлавливаю с более подробным
                // описанием и стектрейсом
                throw new IOException();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "В tasksMap задача с id (" + id + ") не найдена", new TaskNotFoundException(e));
        }
        return tasksMap.get(id);
    }

    // Получение задачи типа Epic по идентификатору
    @Override
    public Epic getEpicById(int id) {
        try {
            if (epicsMap.get(id) != null) {
                Managers.getDefaultHistory().add(epicsMap.get(id));
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "В epicsMap задача с id (" + id + ") не найдена",
                    new TaskNotFoundException(e));
        }
        return epicsMap.get(id);
    }

    // Получение задачи типа Subtask по идентификатору
    @Override
    public Subtask getSubtaskById(int id) {
        try {
            if (subtasksMap.get(id) != null) {
                Managers.getDefaultHistory().add(subtasksMap.get(id));
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "В subtasksMap задача с id (" + id + ") не найдена",
                    new TaskNotFoundException(e));
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
        try {
            if (tasksMap.containsKey(integer)) {
                tasksMap.remove(integer);
            } else if (epicsMap.containsKey(integer)) {
                List<Integer> subtasks = epicsMap.get(integer).getSubtasksId();

                if (!subtasks.isEmpty()) {
                    subtasks.forEach(subtask -> {
                        Managers.getDefaultHistory().remove(subtask);
                        subtasksMap.remove(subtask);
                    });
                }
                epicsMap.remove(integer);
            } else if (subtasksMap.containsKey(integer)) {
                int epicId = subtasksMap.get(integer).getEpicId();

                epicsMap.get(epicId).getSubtasksId().remove(integer);
                subtasksMap.remove(integer);

                updateEpicStatus(epicId);
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Задача с id (" + integer + ") не была найдена ни в одной из HashMap",
                    new TaskNotFoundException(e));
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
            subtasks.forEach(subtaskId -> {
                if (subtasksMap.get(subtaskId).getStatus().equals(Status.IN_PROGRESS)) {
                    epic.setStatus(Status.IN_PROGRESS);
                } else if (subtasksMap.get(subtaskId).getStatus().equals(Status.DONE)) {
                    statuses.add(Status.DONE);
                }
            });
        } else {
            epic.setStatus(Status.NEW);
        }

        if (statuses.size() == subtasks.size()) {
            epic.setStatus(Status.DONE);
        } else if (!statuses.isEmpty() && statuses.size() < subtasks.size()) {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    // собираем все задачи и подзадачи в один список и сортируем
    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> allTasksForMapping = getAllTasksList();
        allTasksForMapping.addAll(getAllSubtasksList());

        return allTasksForMapping.stream()
                .filter(task -> task.getStartTime() != null)
                .sorted(Comparator.comparing(Task::getStartTime))
                .collect(Collectors.toList());
    }

    // удаляем задачу, если она есть в TreeSet, затем добавляем задачу
    @Override
    public void addTaskToTreeSet(Task task) {
        prioritizedTasks.remove(task);
        prioritizedTasks.add(task);
    }

    @Override
    public boolean checkIntersectionOfTasks(Task task) {
        return prioritizedTasks.stream()
                .allMatch(setTask -> {
                    // если в обоих задачах duration != null
                    if (setTask.getDuration() != null && task.getDuration() != null) {
                        return setTask.getStartTime().plus(setTask.getDuration()).isBefore(task.getStartTime()) ||
                                task.getStartTime().plus(task.getDuration()).isBefore(setTask.getStartTime());
                    } else if (setTask.getDuration() != null && task.getDuration() == null) {
                        // если в параметре duration == null
                        return setTask.getStartTime().plus(setTask.getDuration()).isBefore(task.getStartTime()) ||
                                task.getStartTime().isBefore(setTask.getStartTime());
                    } else if (setTask.getDuration() == null && task.getDuration() != null) {
                        // если в задаче из TreeSet duration == null, в задаче из параметра != null
                        return setTask.getStartTime().isBefore(task.getStartTime()) ||
                                task.getStartTime().plus(task.getDuration()).isBefore(setTask.getStartTime());
                    }
                    // если в обоих задачах duration == null
                    return setTask.getStartTime().isBefore(task.getStartTime()) ||
                            task.getStartTime().isBefore(setTask.getStartTime());
                });
    }

    // обновляем поля startTime, duration, endTime в эпике
    private void setEpicDateTime(Subtask subtask, Epic epic) {
        // присваиваем эпику в startTime новое значение если дата или/и время у подзадачи меньше
        if (subtask.getStartTime() != null) {
            if (epic.getStartTime() == null || subtask.getStartTime().isBefore(epic.getStartTime())) {
                epic.setStartTime(subtask.getStartTime());
            }
            // присваиваем endTime эпику значение
            if (subtask.getDuration() != null) {
                if (epic.getEndTime() == null || subtask.getStartTime().isAfter(epic.getEndTime())) {
                    epic.setEndTime(subtask.getStartTime().plus(subtask.getDuration()));
                }
            } else {
                if (epic.getEndTime() == null || subtask.getStartTime().isAfter(epic.getEndTime())) {
                    epic.setEndTime(subtask.getStartTime());
                }
            }
            //если время выполнения подзадачи не пересекается с временем любой другой задачи, то добавляем в TreeSet
            //иначе выводим предупреждение
            if (checkIntersectionOfTasks(subtask) || prioritizedTasks.isEmpty()) {
                prioritizedTasks.add(subtask); //добавляю подзадачу в TreeSet
            } else {
                logger.warning("Время подзадачи (" + subtask.getName() + ") пересекается с временем другой задачи. " +
                        "Необходимо изменить время начала выполнения и/или продолжительность выполнения");
            }
        }

        // перенаправляем значение duration подзадачи в метод setDuration() эпика
        if (subtask.getDuration() != null) {
            epic.setDuration(subtask.getDuration());
        }
    }

    public void clearTreeSet() {
        prioritizedTasks.clear();
    }
}