package ru.yandex.practicum.java.kanban.service;

import ru.yandex.practicum.java.kanban.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    // список истории просмотров
    private static final List<Task> browsingHistory = new ArrayList<>();

    // добавление задачи в список просмотров
    @Override
    public void add(Task task) {
        if (browsingHistory.size() <= 9) {
            browsingHistory.add(task);
        } else {
            browsingHistory.removeFirst();
            browsingHistory.add(task);
        }
    }

    // возвращение копии списка просмотров
    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(browsingHistory);
    }
}
