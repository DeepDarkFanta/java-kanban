package taskmanagerapp.manager.utils.jsonclasses;

import taskmanagerapp.tasks.Task;

import java.util.ArrayList;

public class JsonObjectClass {
    public ArrayList<Task> tasks;
    public ArrayList<Task>  tasksHistory;

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public ArrayList<Task> getTasksHistory() {
        return tasksHistory;
    }

    public void setTasksHistory(ArrayList<Task> tasksHistory) {
        this.tasksHistory = tasksHistory;
    }
}
