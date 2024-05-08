package tasks.types;

import tasks.status.Status;

import java.util.Objects;

public class Task {
    private static int staticId = 0;
    private int id;
    public String taskName;
    public String discription;
    private Status status;

    public Task(String taskName, String discription) {
        staticId += 1;
        this.id = staticId;
        this.taskName = taskName;
        this.discription = discription;
        this.status = Status.NEW;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() { return status; }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
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
        return Objects.equals(taskName, task.taskName) && Objects.equals(discription, task.discription) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, discription, status);
    }
}
