package tasks.types;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    ArrayList<SubTask> subTasks = new ArrayList<>();
    public Epic(String taskName, String discription) {
        super(taskName, discription);
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks.size()=" + subTasks.size() +
                ", taskName='" + taskName + '\'' +
                ", discription='" + discription + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                '}';
    }
}
