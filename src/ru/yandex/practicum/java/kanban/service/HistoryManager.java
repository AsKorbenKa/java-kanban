package ru.yandex.practicum.java.kanban.service;

import ru.yandex.practicum.java.kanban.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();
}
