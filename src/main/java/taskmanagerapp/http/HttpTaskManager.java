package taskmanagerapp.http;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import taskmanagerapp.enums.Status;
import taskmanagerapp.enums.TaskType;
import taskmanagerapp.manager.FileBackedTasksManager;
import taskmanagerapp.manager.Managers;
import taskmanagerapp.manager.utils.jsonclasses.JsonObjectClass;
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
        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        JsonArray tasksJsonArray = jsonObject.getAsJsonArray("tasks");
        JsonArray historyJsonArray = jsonObject.getAsJsonArray("tasksHistory");
        Type type = new TypeToken<ArrayList<TaskHelperPojoObject>>() {}.getType();
        ArrayList<TaskHelperPojoObject> tasks = gson.fromJson(tasksJsonArray, type);
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
        loadHistory(historyJsonArray);
    }

    private void loadHistory(JsonArray historyJsonArray) {
        if (!historyJsonArray.isEmpty()) {
            Type type = new TypeToken<ArrayList<TaskHelperPojoObject>>() {}.getType();
            ArrayList<TaskHelperPojoObject> tasks = gson.fromJson(historyJsonArray, type);

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

    @Override
    public void save() {
        ArrayList<Task> tasks = super.allTaskList;
        List<Task> tasksHistory = getHistory();
        tasks.sort(Comparator.comparingInt(Task::getId));

        JsonObjectClass jsonObjectClass = new JsonObjectClass();
        jsonObjectClass.setTasks(tasks);
        jsonObjectClass.setTasksHistory((ArrayList<Task>) tasksHistory);
        String request = gson.toJson(jsonObjectClass);
        client.put(request);
    }

    /*
    лишние удалил. Как правильно понял то вызывая методы интерфейса TaskManager у HttpTaskManger -> которые лично в нем не находятся,
    то вызывутся у его родителя у FileBackendManager. Ну и оставил методы изменения состояние Манеджера для сохранения на сервере
     */

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
}
