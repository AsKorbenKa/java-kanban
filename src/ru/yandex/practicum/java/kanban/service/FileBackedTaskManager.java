package ru.yandex.practicum.java.kanban.service;

import ru.yandex.practicum.java.kanban.exceptions.ManagerSaveException;
import ru.yandex.practicum.java.kanban.model.Epic;
import ru.yandex.practicum.java.kanban.model.Status;
import ru.yandex.practicum.java.kanban.model.Subtask;
import ru.yandex.practicum.java.kanban.model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File allTasksHistory;

    public FileBackedTaskManager(File allTasksHistory) {
        this.allTasksHistory = allTasksHistory;
    }

    // достаем данные из файла, добавляем объекты классов в их HashMap
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileTaskManager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {
                if (lines.get(i).isEmpty()) {
                    break;
                }
                Task task = fromString(lines.get(i));
                switch (task.getClass().getName()) {
                    case "Task":
                        fileTaskManager.createTask(task);
                    case "Epic":
                        fileTaskManager.createEpic((Epic) task);
                    case "Subtask":
                        fileTaskManager.createSubtask((Subtask) task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось получить задачи из файла", e);
        }
        return fileTaskManager;
    }

    // создаем из строки объект или наследника класса Task
    private static Task fromString(String value) {
        String[] taskPieces = value.split(",");
        int id = Integer.parseInt(taskPieces[0]);
        String name = taskPieces[2];
        Status status = Status.valueOf(taskPieces[3]);
        String description = taskPieces[4];
        LocalDateTime startTime;
        if (taskPieces[5].equals("null")) {
            startTime = null;
        } else {
            startTime = LocalDateTime.parse(taskPieces[5]);
        }
        Duration duration;
        if (taskPieces[6].equals("null")) {
            duration = null;
        } else {
            duration = Duration.parse(taskPieces[6]);
        }
        return switch (taskPieces[1]) {
            case "TASK" -> new Task(id, name, status, description, startTime, duration);
            case "SUBTASK" -> new Subtask(id, name, status, description, startTime, duration, Integer.parseInt(taskPieces[7]));
            case "EPIC" -> new Epic(id, name, status, description, startTime, duration);
            default -> throw new IllegalArgumentException("Unknown task type: " + taskPieces[1]);
        };
    }

    // сохранение всех задач в файл
    private void save() throws ManagerSaveException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(allTasksHistory, StandardCharsets.UTF_8))) {
            bw.write("id,type,name,status,description,startTime,duration,epic\n");

            for (Task task : getAllTasksList()) {
                bw.write(task.getId() + ",TASK," + task.getName() + "," + task.getStatus() + ","
                        + task.getDescription() + "," + task.getStartTime() + "," + task.getDuration()
                        + "\n");
            }

            for (Epic epic : getAllEpicsList()) {
                bw.write(epic.getId() + ",EPIC," + epic.getName() + "," + epic.getStatus() + ","
                        + epic.getDescription() + "," + epic.getStartTime() + "," + epic.getDuration()
                        + "\n");
            }

            for (Subtask subtask : getAllSubtasksList()) {
                bw.write(subtask.getId() + ",SUBTASK," + subtask.getName() + "," + subtask.getStatus() + ","
                        + subtask.getDescription() + "," + subtask.getStartTime() + ","
                        + subtask.getDuration() + "," + subtask.getEpicId() + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить изменения в файл", e);
        }
    }

    // переопределены те методы, которые каким-либо образом влияют на количество или состояние задач
    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void clearTasksMap() {
        super.clearTasksMap();
        save();
    }

    @Override
    public void clearEpicsMap() {
        super.clearEpicsMap();
        save();
    }

    @Override
    public void clearSubtasksMap() {
        super.clearSubtasksMap();
        save();
    }

    @Override
    public void replaceTask(Task task) {
        super.replaceTask(task);
        save();
    }

    @Override
    public void replaceEpic(Epic epic) {
        super.replaceEpic(epic);
        save();
    }

    @Override
    public void replaceSubtask(Subtask subtask) {
        super.replaceSubtask(subtask);
        save();
    }

    @Override
    public void removeTaskById(Integer integer) {
        super.removeTaskById(integer);
        save();
    }

    @Override
    public void updateTaskStatus(Task task, Status status) {
        super.updateTaskStatus(task, status);
        save();
    }

    @Override
    public void updateSubtaskStatus(Subtask subtask, Status status) {
        super.updateSubtaskStatus(subtask, status);
        save();
    }

    @Override
    public void updateEpicStatus(int epicId) {
        super.updateEpicStatus(epicId);
        save();
    }
}
