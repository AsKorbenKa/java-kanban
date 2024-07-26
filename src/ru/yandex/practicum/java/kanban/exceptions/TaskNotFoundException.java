package ru.yandex.practicum.java.kanban.exceptions;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException() {
    }

    public TaskNotFoundException(final String message) {
        super(message);
    }

    public TaskNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TaskNotFoundException(final Throwable cause) {
        super(cause);
    }
}
