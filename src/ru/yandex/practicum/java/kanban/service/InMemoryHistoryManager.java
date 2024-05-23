package ru.yandex.practicum.java.kanban.service;

import ru.yandex.practicum.java.kanban.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static List<Task> browsingHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (browsingHistory.size() <= 9) {
            browsingHistory.add(task);
        } else {
            browsingHistory.removeFirst();
            browsingHistory.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return browsingHistory;
    }
}
