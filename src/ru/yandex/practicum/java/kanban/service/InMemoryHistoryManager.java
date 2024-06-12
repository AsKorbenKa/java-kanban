package ru.yandex.practicum.java.kanban.service;

import ru.yandex.practicum.java.kanban.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    // список истории просмотров
    final OwnLinkedList browsingHistory = new OwnLinkedList();

    // добавление задачи в список просмотров
    @Override
    public void add(Task task) {
        browsingHistory.linkLast(task);
    }

    // удаление задачи из истории просмотров
    @Override
    public void remove(int id) {
        browsingHistory.removeNode(id);
    }

    // возвращение копии списка просмотров
    @Override
    public List<Task> getHistory() {
        return browsingHistory.getTasks();
    }

    private static class OwnLinkedList {

        class Node <T> {

            private T data;
            private Node<T> next;
            private Node<T> prev;

            public Node(T data) {
                this.data = data;
                this.next = null;
                this.prev = null;
            }
        }

        private static Map<Integer, Node<Task>> innerMap = new LinkedHashMap<>();

        public void linkLast(Task task) {
            if (innerMap.containsKey(task.getId())) {
                innerMap.remove(task.getId());
            }

            innerMap.put(task.getId(), new Node<>(task));
        }

        public void removeNode(int id) {
            innerMap.remove(id);
        }

        public List<Task> getTasks() {
            List<Task> histories = new ArrayList<>();
            for(Node<Task> history : innerMap.values()) {
                histories.add(history.data);
            }

            return histories;
        }
    }
}
