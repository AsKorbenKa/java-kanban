package ru.yandex.practicum.java.kanban.service;

import ru.yandex.practicum.java.kanban.service.InMemoryTaskManager;

public final class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}