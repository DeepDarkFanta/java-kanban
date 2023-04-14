package taskmanagerapp.http;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import taskmanagerapp.enums.Status;
import taskmanagerapp.enums.TaskType;
import taskmanagerapp.manager.FileBackedTasksManager;
import taskmanagerapp.manager.Managers;
import taskmanagerapp.manager.utils.pojoclasses.TaskHelperPojoObject;
import taskmanagerapp.tasks.Epic;
import taskmanagerapp.tasks.Subtask;
import taskmanagerapp.tasks.Task;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(String path, String url) {
        super(path);
        client = new KVTaskClient(url);
        gson = Managers.getGsonFormattedZonedDateTime();
        loadFromServer();
    }

    public void loadFromServer() {
        String response = client.load();
        if (response.isEmpty()) {
            return;
        }
        String[] tasksAndHistory = response.split("#separetted#");
        Type type = new TypeToken<ArrayList<TaskHelperPojoObject>>() {}.getType();
        ArrayList<TaskHelperPojoObject> tasks = gson.fromJson(tasksAndHistory[0], type);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        for (TaskHelperPojoObject task : tasks) {
            TaskType taskType = task.taskType;
            switch (taskType) {
                case TASK:
                    Task newTask = new Task(
                           task.title,
                            task.description,
                            (int) task.duration.toMinutes(),
                            task.startTime.format(formatter)
                    );
                    setTask(newTask);
                    updateTask(newTask, task.status);
                    break;
                case EPIC:
                    Epic newEpic = new Epic(
                            task.title,
                            task.description
                    );

                    setTask(newEpic);
                    updateEpic(newEpic, task.status);
                    break;
                case SUBTASK:
                    Subtask subtask = new Subtask(
                            task.title,
                            task.description,
                            epicTasksMap.get(task.idOfEpic),
                            (int) task.duration.toMinutes(),
                            task.startTime.format(formatter)
                    );

                    setTask(subtask);
                    updateSubtask(subtask, task.status);
                    break;
            }
        }
        loadHistory(tasksAndHistory[1]);
    }

    private void loadHistory(String taskHistory) {
        if (!taskHistory.isEmpty()) {
            Type type = new TypeToken<ArrayList<TaskHelperPojoObject>>() {}.getType();
            ArrayList<TaskHelperPojoObject> tasks = gson.fromJson(taskHistory, type);

            for (TaskHelperPojoObject task : tasks) {
                TaskType taskType = task.taskType;
                int id = task.id;
                switch (taskType) {
                    case TASK:
                        getByIdTask(id);
                        break;
                    case EPIC:
                        getByIdEpic(id);
                        break;
                    case SUBTASK:
                        getByIdSubtask(id);
                        break;
                }
            }
        }
    }

    public void save() {
        ArrayList<Task> tasks = super.allTaskList;
        tasks.sort(Comparator.comparingInt(Task::getId));
        String gsonAllTasks = gson.toJson(tasks) + "#separetted#"; //enterprise решение
        List<Task> tasksHistory = getHistory();
        gsonAllTasks = gsonAllTasks + gson.toJson(tasksHistory);
        client.put(gsonAllTasks);
    }

    public ArrayList<Epic> getEpicTasksList() {
        return super.getEpicTasksList();
    }

    @Override
    public ArrayList<Task> getTasksList() {
        return super.getTasksList();
    }

    @Override
    public ArrayList<Subtask> getSubtasksList() {
        return super.getSubtasksList();
    }

    @Override
    public Task getByIdTask(int id) {
        Task task = super.getByIdTask(id);
        save();
        return task;
    }

    @Override
    public Epic getByIdEpic(int id) {
        Epic epic = super.getByIdEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getByIdSubtask(int id) {
        Subtask subtask = super.getByIdSubtask(id);
        save();
        return subtask;
    }

    @Override
    public List<Subtask> getAllEpicSubtasks(int idEpic) {
        return super.getAllEpicSubtasks(idEpic);
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAllTasks(ArrayList<Task> taskList) {
        super.deleteAllTasks(taskList);
        save();
    }

    @Override
    public void deleteAllEpics(ArrayList<Epic> epicList) {
        super.deleteAllEpics(epicList);
        save();
    }

    @Override
    public void deleteAllSubtasks(ArrayList<Subtask> subtaskList) {
        super.deleteAllSubtasks(subtaskList);
        save();
    }

    @Override
    public void updateTask(Task task, Status status) {
        super.updateTask(task, status);
        save();
    }

    @Override
    public void updateEpic(Epic epic, Status status) {
        super.updateEpic(epic, status);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask, Status status) {
        super.updateSubtask(subtask, status);
        save();
    }

    @Override
    public void setTask(Task task) {
        super.setTask(task);
        save();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public HashMap<Integer, Epic> getEpicTasksMap() {
        return super.getEpicTasksMap();
    }

    @Override
    public HashMap<Integer, Task> getTasksMap() {
        return super.getTasksMap();
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasksMap() {
        return super.getSubtasksMap();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }
}
