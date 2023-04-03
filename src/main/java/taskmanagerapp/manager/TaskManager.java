package taskmanagerapp.manager;

import taskmanagerapp.enums.Status;
import taskmanagerapp.tasks.Epic;
import taskmanagerapp.tasks.Subtask;
import taskmanagerapp.tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    ArrayList<Epic> getEpicTasksList();

    ArrayList<Task> getTasksList();

    ArrayList<Subtask> getSubtasksList();

    Task getByIdTask(int id);

    Epic getByIdEpic(int id);

    Subtask getByIdSubtask(int id);

    List<Subtask> getAllEpicSubtasks(int idEpic);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    void deleteAllTasks(ArrayList<Task> taskList);

    void deleteAllEpics(ArrayList<Epic> epicList);

    void deleteAllSubtasks(ArrayList<Subtask> subtaskList);

    void updateTask(Task task, Status status);

    void updateEpic(Epic epic, Status status);

    void updateSubtask(Subtask subtask, Status status);

    void setTask(Task task);

    List<Task> getHistory();

    ArrayList<Task> getAllTasks();

     HashMap<Integer, Epic> getEpicTasksMap();

     HashMap<Integer, Task> getTasksMap();

     HashMap<Integer, Subtask> getSubtasksMap();

     List<Task> getPrioritizedTasks();
}
