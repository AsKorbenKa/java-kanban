package ru.yandex.practicum.java.kanban.model;

import java.util.Objects;

public class Task {
    private static int staticId = 0;
    private int id;
    private String name;
    private String discription;
    private Status status;

    public Task(String name, String discription) {
        staticId += 1;
        this.id = staticId;
        this.name = name;
        this.discription = discription;
        this.status = Status.NEW;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + name + '\'' +
                ", discription='" + discription + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(discription, task.discription) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, discription, status);
    }
}
