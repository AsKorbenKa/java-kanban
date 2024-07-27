package ru.yandex.practicum.java.kanban.exceptions;

public class TaskIntersectionError extends RuntimeException {

    public TaskIntersectionError() {
    }

    public TaskIntersectionError(final String message) {
        super(message);
    }

    public TaskIntersectionError(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TaskIntersectionError(final Throwable cause) {
        super(cause);
    }
}
