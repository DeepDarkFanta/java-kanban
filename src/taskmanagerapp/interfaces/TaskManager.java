package taskmanagerapp.interfaces;

import taskmanagerapp.enums.Status;
import taskmanagerapp.task.Epic;
import taskmanagerapp.task.Subtask;
import taskmanagerapp.task.Task;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    ArrayList<Epic> getEpicTasksList();

    ArrayList<Task> getTasksList();

    ArrayList<Subtask> getSubtasksList();

    Task getByIdTask(int id);

    Object getByIdEpic(int id);

    Object getByIdSubtask(int id);

    static int getIdTask() {
        return 0;
    }

    ArrayList<Subtask> getAllEpicSubtasks(int idEpic);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    void deleteAllTasks(ArrayList<Task> taskList);

    void deleteAllEpics(ArrayList<Epic> epicList);

    void deleteAllSubtasks(ArrayList<Subtask> subtaskList);

    void updateTask(Task task, Status status);

    void updateEpic(Epic epic, Status status);

    void updateSubtask(Subtask subtask, Status status);

    void setTask(Object task);

    List<Task> getHistory();
}
