package ru.yandex.practicum.java.kanban.model;

import ru.yandex.practicum.java.kanban.service.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Epic extends Task {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();
    private final List<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, Status status, String description) {
        super(id, name, status, description);
    }

    public Epic(int id, String name, Status status, String description, LocalDateTime startTime, Duration duration) {
        super(id, name, status, description, startTime, duration);
    }

    public LocalDateTime getEndTime() {
        Optional<Subtask> lastTask = subtasksId.stream()
                .map(id -> taskManager.getSubtaskById(id))
                .filter(subtask -> subtask.getStartTime() != null)
                .max(Comparator.comparing(Subtask::getStartTime));

        if (lastTask.isPresent()) {
            Subtask subtask = lastTask.get();
            if (subtask.getDuration() != null) {
                return subtask.getStartTime().plus(subtask.getDuration());
            }
        }
        return null;

    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    // прибавляем duration к значению эпика
    @Override
    public void setDuration(Duration newDuration) {
        if (duration == null) {
            duration = newDuration;
        } else {
            duration = duration.plus(newDuration);
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks.size()=" + subtasksId.size() +
                ", name='" + getName() + '\'' +
                ", discription='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                "}";
    }
}
