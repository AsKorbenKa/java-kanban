package tasks.types;

import java.util.Objects;

public class SubTask extends Task {
    Epic epic;

    public SubTask(String taskName, String discription, Epic epic) {
        super(taskName, discription);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "taskName='" + taskName + '\'' +
                ", discription='" + discription + '\'' +
                ", id=" + getId() +
                ", epic.getId()=" + epic.getId() +
                ", status=" + getStatus() +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epic);
    }
}
