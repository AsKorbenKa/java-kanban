package ru.yandex.practicum.java.kanban.exceptions;

public class TaskIntersectionError extends RuntimeException {

    public TaskIntersectionError() {
        super("Время выполнения задачи пересекается с временем выполнения другой задачи");
    }
}
