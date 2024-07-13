package ru.yandex.practicum.java.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private static int staticId = 0;
    private int id;
    private String name;
    private String description;
    private Status status;
    protected Duration duration;
    private LocalDateTime startTime;

    public Task(String name, String description) {
        staticId += 1;
        this.id = staticId;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.startTime = null;
        this.duration = null;
    }

    public Task(int id, String name, Status status, String description) {
        // сохраняю id в статике для дальнейшего увеличения если будем использовать конструктов выше
        if (id > staticId) {
            staticId = id;
        }
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = null;
        this.duration = null;
    }

    //создание объекта класса с данными, взятыми не из файла
    public Task(String name, String description, LocalDateTime startTime,
                Duration duration) {
        staticId += 1;
        this.id = staticId;
        this.name = name;
        this.status = Status.NEW;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    //создание объекта класса с данными, взятыми из файла
    public Task(int id, String name, Status status, String description, LocalDateTime startTime, Duration duration) {
        if (id > staticId) {
            staticId = id;
        }
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    // устанавливаем время для задачи
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + getName() + '\'' +
                ", discription='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                "}";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status && Objects.equals(duration, task.duration) && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, duration, startTime);
    }
}
