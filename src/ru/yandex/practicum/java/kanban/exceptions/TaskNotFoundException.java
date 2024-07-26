package ru.yandex.practicum.java.kanban.exceptions;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(int id) {
        super("Задача со следующим id не найдена: " + id);
    }
}
