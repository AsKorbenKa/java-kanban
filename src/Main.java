import tasks.TaskManager;
import tasks.status.Status;
import tasks.types.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        System.out.println("Поехали!");

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Задача 1", "Описание задачи 1");
        Epic epic2 = new Epic("Задача 1", "Описание задачи 1");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        System.out.println(epic1);


        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", epic1);
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", epic1);
        SubTask subTask3 = new SubTask("Подзадача 1", "Описание подзадачи 1", epic2);
        SubTask subTask4 = new SubTask("Подзадача 2", "Описание подзадачи 2", epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        taskManager.createSubTask(subTask4);

        System.out.println(taskManager.getEpicTasksMap());
        System.out.println(taskManager.getTasksMap());
        System.out.println(taskManager.getSubTasksMap());

        taskManager.changeTaskStatus(task2, Status.IN_PROGRESS);
        taskManager.changeSubTaskAndEpicStatus(subTask3, Status.DONE);
        taskManager.changeSubTaskAndEpicStatus(subTask4, Status.IN_PROGRESS);
        System.out.println(task2);
        System.out.println(epic2);

        taskManager.removeTaskById(3);
        System.out.println(taskManager.getEpicTasksMap());
    }
}
