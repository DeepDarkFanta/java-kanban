package manager;

import task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Manager {
    int id;
    private HashMap<Integer, Epic> epicTasksMap = new HashMap<>();
    private HashMap<Integer, Task> tasksMap = new HashMap<>();
    private HashMap<Integer, Subtask> subtasksMap = new HashMap<>();
    ArrayList<Object> allTaskList = new ArrayList<>();

    public ArrayList<Epic> getEpicTasksList() {
        ArrayList<Epic> epicList = new ArrayList<>();
        for (Map.Entry<Integer, Epic> integerEpicEntry : epicTasksMap.entrySet()) {
            epicList.add(integerEpicEntry.getValue());
        }
        return epicList;
    }

    public ArrayList<Task> getTasksList() {
        ArrayList<Task> taskList = new ArrayList<>();
        for (Map.Entry<Integer, Task> integerEpicEntry : tasksMap.entrySet()) {
            taskList.add(integerEpicEntry.getValue());
        }
        return taskList;
    }

    public ArrayList<Subtask> getSubtasksList() {
        ArrayList<Subtask> SubtaskList = new ArrayList<>();
        for (Map.Entry<Integer, Subtask> integerEpicEntry : subtasksMap.entrySet()) {
            SubtaskList.add(integerEpicEntry.getValue());
        }
        return SubtaskList;
    }

    public Task getByIdTask(int idTask) {
        return tasksMap.get(idTask);
    }

    public Object getByIdEpic(int idTask) {
        return epicTasksMap.get(idTask);
    }

    public Object getByIdSubtask(int idTask) {
        return subtasksMap.get(idTask);
    }

    public int getIdTask() {
        return id++;
    }

    public ArrayList<Subtask> getAllEpicSubs(int idEpic){
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Integer idSub : epicTasksMap.get(idEpic).getIdOfSubtasksList()) {
            subtasks.add(subtasksMap.get(idSub));
        }
        return subtasks;
    }
    public void deleteAllTasks(ArrayList<Task> taskList) {
        for (Task task : taskList) {
            allTaskList.remove(task);
            tasksMap.remove(task.getId());
        }
    }

    public void deleteAllEpics(ArrayList<Epic> epicList){
        for (Epic epic : epicList) {
            for (Integer idSub : epic.getIdOfSubtasksList()) {
                allTaskList.remove(subtasksMap.get(idSub));
                tasksMap.remove(idSub);
                subtasksMap.remove(idSub);
            }
            allTaskList.remove(epic);
            epicTasksMap.remove(epic.getId());
        }
    }

    public void deleteAllSubtask(ArrayList<Subtask> subtaskList){
        if (subtaskList != null) {
            for (Subtask subtask : subtaskList) {
                ArrayList<Integer> epicList = epicTasksMap.get(subtask.getIdOfEpic()).getIdOfSubtasksList();
                for (int i = 0; i < epicList.size(); i++) {
                    if (epicList.get(i) == subtask.getId()) {
                        epicList.remove(i);
                        break;
                    }
                }
                allTaskList.remove(subtask);
                subtasksMap.remove(subtask.getId());
                epicTasksMap.get(subtask.getIdOfEpic()).setStatus(Task.Status.NEW);
            }
        }
    }

    public void updateTask(Task task, Task.Status status){
        allTaskList.remove(tasksMap.get(task.getId()));
        task.setStatus(status);
        setTask(task);
    }

    public void updateEpic(Epic epic, Task.Status status) {
        if (status == Task.Status.NEW || status == Task.Status.DONE) {
            for (Integer idSub : epicTasksMap.get(epic.getId()).getIdOfSubtasksList()) {
                if (subtasksMap.get(idSub).getStatus() != status) {
                    subtasksMap.get(idSub).setStatus(status);
                }
            }
            removeSubtask(epic, status);
        } else if (status == Task.Status.IN_PROGRESS) {
            removeSubtask(epic, status);
        }
    }

    public void updateSubtask(Subtask subtask, Task.Status status){

        allTaskList.remove(subtasksMap.get(subtask.getId()));
        subtask.setStatus(status);
        allTaskList.add(subtask);
        subtasksMap.put(subtask.getId(), subtask);
        if (status == Task.Status.NEW || status == Task.Status.DONE){
            for (Integer idSub : epicTasksMap.get(subtask.getIdOfEpic()).getIdOfSubtasksList()) {
                if (subtask.getStatus() != subtasksMap.get(idSub).getStatus()){
                    epicTasksMap.get(subtask.getIdOfEpic()).setStatus(Task.Status.IN_PROGRESS);
                    return;
                }

            }
            epicTasksMap.get(subtask.getIdOfEpic()).setStatus(status);
        }
        /*else if (status == Task.Status.DONE){
            for (Integer idSub : epicTasksMap.get(subtask.getIdOfEpic()).getIdOfSubtasksList()){
                if (subtask.getStatus() != subtasksMap.get(idSub).getStatus()){
                    epicTasksMap.get(subtask.getIdOfEpic()).setStatus(Task.Status.IN_PROGRESS);
                    return;
                }
            }
            epicTasksMap.get(subtask.getIdOfEpic()).setStatus(status);
        }*/ else if (status == Task.Status.IN_PROGRESS){
            epicTasksMap.get(subtask.getIdOfEpic()).setStatus(Task.Status.IN_PROGRESS);
        }
    }


    private void removeSubtask(Epic epic, Task.Status status) {
        epic.setIdOfSubtasksList(epicTasksMap.get(epic.getId()).getIdOfSubtasksList());
        allTaskList.remove(epic);
        epic.setStatus(status);
        setTask(epic);
    }


    public void setTask(Object task) {
        allTaskList.add(task);
        switch (task.getClass().getSimpleName()) {
            case "Task":
                Task newTask = (Task) task;
                tasksMap.put(newTask.getId(), newTask);
                break;
            case "Epic":
                Epic newEpic = (Epic) task;
                epicTasksMap.put(newEpic.getId(), newEpic);
                break;
            case "Subtask":
                Subtask newSubtask = (Subtask) task;
                ArrayList<Integer> idSubInEpicList = epicTasksMap.get(newSubtask.getIdOfEpic()).getIdOfSubtasksList();
                idSubInEpicList.add(newSubtask.getId());
                subtasksMap.put(newSubtask.getId(), newSubtask);
                break;
        }
    }

    public void showList() {
        for (Object o : allTaskList) {
            System.out.println("ArrayListAllTask = " + o);
        }
    }


    public void showMap() {
        System.out.println("tasks:");
        System.out.println("lenght: " + epicTasksMap.size());
        for (Map.Entry<Integer, Epic> integerTaskEntry : epicTasksMap.entrySet()) {
            System.out.println(integerTaskEntry.getValue());
        }
    }
}