package ru.yandex.practicum.java.kanban.model;

import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String discription, int epicId) {
        super(name, discription);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + getName() + '\'' +
                ", discription='" + getDiscription() + '\'' +
                ", id=" + getId() +
                ", epicId=" + epicId +
                ", status=" + getStatus() +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
