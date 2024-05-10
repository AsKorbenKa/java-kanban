package ru.yandex.practicum.java.kanban.model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String discription) {
        super(name, discription);
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks.size()=" + subtasksId.size() +
                ", name='" + getName() + '\'' +
                ", discription='" + getDiscription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                '}';
    }
}
