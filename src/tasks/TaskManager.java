package tasks;

import tasks.status.Status;
import tasks.types.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> tasksMap = new HashMap<>();
    HashMap<Integer, Epic> epicTasksMap = new HashMap<>();
    HashMap<Integer, SubTask> subTasksMap = new HashMap<>();

    // Добавляем объект класса Task в HashMap<> taskMap
    public void createTask(Task task) {
        if (!tasksMap.containsKey(task.getId())) {
            tasksMap.put(task.getId(), task);
        } else {
            System.out.println("Такая задача в списке Task уже существует.");
        }
    }

    // Добавляем объект класса Epic в HashMap<> epicTasksMap
    public void createEpic(Epic epic) {
        if (!epicTasksMap.containsKey(epic.getId())) {
            epicTasksMap.put(epic.getId(), epic);
        } else {
            System.out.println("Такая задача в списке Epic уже существует.");
        }
    }

    /* Добавляем объект класса SubTask в HashMap<> subTasksMap,
     также добавляем этот объект класса в список сабтасков объекта класса Epic, к которому subTask относится */
    public void createSubTask(SubTask subTask) {
        if (!subTasksMap.containsKey(subTask.getId())) {
            subTasksMap.put(subTask.getId(), subTask);
            Epic epic = subTask.getEpic();
            if (!epic.getSubTasks().contains(subTask)) {
                epic.getSubTasks().add(subTask);
            }
        } else {
            System.out.println("Такая задача в списке Subtask уже существует.");
        }
    }

    //Получение списка всех текущих задач.
    public ArrayList<Object> allTasksList() {
        ArrayList<Object> allTasks = new ArrayList<>();
        allTasks.addAll(tasksMap.values());
        allTasks.addAll(epicTasksMap.values());
        allTasks.addAll(subTasksMap.values());
        return allTasks;
    }

    // Удаление всех задач
    public void clearAllMaps() {
        tasksMap.clear();
        epicTasksMap.clear();
        subTasksMap.clear();
    }

    // Получение задачи по модификатору.
    public Object getTaskById(Integer id) {
        Object object;
        if (tasksMap.containsKey(id)) {
            object = tasksMap.get(id);
        } else if (epicTasksMap.containsKey(id)) {
            object = epicTasksMap.get(id);
        } else if (subTasksMap.containsKey(id)) {
            object = subTasksMap.get(id);
        } else {
            object = null;
            System.out.println("Задачи по такому id нет");
        }
        return object;
    }

    /* обновляем старую версию объекта на новую, используя объект класса Task.
    Это единственно решение, до которого я додумался */
    public void replaceTask(Task task) {
        int id = -1;
        for (Integer integer : tasksMap.keySet()) {
            if (tasksMap.get(integer).equals(task)) {
                id = integer;
            }
        }
        if (id != -1) {
            tasksMap.remove(id);
            tasksMap.put(task.getId(), task);
        }
    }

    // обновляем старую версию объекта на новую, используя объект класса Epic.
    public void replaceEpic(Epic epic) {
        int id = -1;
        for (Integer integer : epicTasksMap.keySet()) {
            if (epicTasksMap.get(integer).equals(epic)) {
                id = integer;
            }
        }
        if (id != -1) {
            epicTasksMap.remove(id);
            epicTasksMap.put(epic.getId(), epic);
        }
    }

    // обновляем старую версию объекта на новую, используя объект класса SubTask.
    public void replaceSubTask(SubTask subTask) {
        int id = -1;
        for (Integer integer : subTasksMap.keySet()) {
            if (subTasksMap.get(integer).equals(subTask)) {
                id = integer;
            }
        }
        if (id != -1) {
            subTasksMap.remove(id);
            subTasksMap.put(subTask.getId(), subTask);
        }
    }

    /* удаление по идентификатору. При удалении объекта класса Epic, сначала обращаемся к списку подзадач этого эпика,
    используя id каждого объекта класса SubTask, удаляем все эти объекты из общей HasMap класса SubTask,
    после чего удаляем сам Epic */
    public void removeTaskById(Integer integer) {
        if (tasksMap.containsKey(integer)) {
            tasksMap.remove(integer);
        } else if (epicTasksMap.containsKey(integer)) {
            ArrayList<SubTask> subTasks = epicTasksMap.get(integer).getSubTasks();
            if (!subTasks.isEmpty()) {
                for (SubTask subTask : subTasks) {
                    subTasksMap.remove(subTask.getId());
                }
            }
            epicTasksMap.remove(integer);
        } else if (subTasksMap.containsKey(integer)) {
            subTasksMap.remove(integer);
        } else {
            System.out.println("По такому идентификатору задачи нет.");
        }
    }

    // Возвращаю список подзадач определенного объекта класса Epic
    public ArrayList<SubTask> getAllEpicSubtasks(Epic epic) {
        return epic.getSubTasks();
    }

    // Изменяем статус для объектов класса Task
    public void changeTaskStatus(Task task, Status status) {
        task.setStatus(status);
    }

    // Изменяем статус для объектов класса SubTask, проверяем и меняем статус Epic
    public void changeSubTaskAndEpicStatus(SubTask subTask, Status status) {
        subTask.setStatus(status);
        Epic epic = subTask.getEpic();
        ArrayList<SubTask> subTasks = epic.getSubTasks();
        ArrayList<Status> statuses = new ArrayList<>();
        if (!subTasks.isEmpty()) {
            for (SubTask st : subTasks) {
                if (st.getStatus().equals(Status.IN_PROGRESS)) {
                    epic.setStatus(Status.IN_PROGRESS);
                } else {
                    statuses.add(st.getStatus());
                }
            }
        }

        if (statuses.size() == subTasks.size()) {
            epic.setStatus(Status.DONE);
        }
    }

    public HashMap<Integer, Epic> getEpicTasksMap() {
        return epicTasksMap;
    }

    public HashMap<Integer, Task> getTasksMap() {
        return tasksMap;
    }

    public HashMap<Integer, SubTask> getSubTasksMap() {
        return subTasksMap;
    }
}
