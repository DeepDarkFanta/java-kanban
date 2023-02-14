package taskmanagerapp.manager;

import taskmanagerapp.enums.Status;
import taskmanagerapp.task.*;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private static int id;
    private final HashMap<Integer, Epic> epicTasksMap;
    private final HashMap<Integer, Task> tasksMap;
    private final HashMap<Integer, Subtask> subtasksMap;
    private final ArrayList<Object> allTaskList;
    private final HistoryManager inMemoryHistoryManager;

    public InMemoryTaskManager() {
        epicTasksMap = new HashMap<>();
        tasksMap = new HashMap<>();
        subtasksMap = new HashMap<>();
        allTaskList = new ArrayList<>();
        inMemoryHistoryManager = Managers.getDefaultHistory();

    }

    @Override
    public ArrayList<Epic> getEpicTasksList() {
        ArrayList<Epic> epicList = new ArrayList<>();
        for (Map.Entry<Integer, Epic> integerEpicEntry : epicTasksMap.entrySet()) {
            epicList.add(integerEpicEntry.getValue());
        }
        return epicList;
    }

    @Override
    public ArrayList<Task> getTasksList() {
        ArrayList<Task> taskList = new ArrayList<>();
        for (Map.Entry<Integer, Task> integerEpicEntry : tasksMap.entrySet()) {
            taskList.add(integerEpicEntry.getValue());
        }
        return taskList;
    }

    @Override
    public ArrayList<Subtask> getSubtasksList() {
        ArrayList<Subtask> SubtaskList = new ArrayList<>();
        for (Map.Entry<Integer, Subtask> integerEpicEntry : subtasksMap.entrySet()) {
            SubtaskList.add(integerEpicEntry.getValue());
        }
        return SubtaskList;
    }

    @Override
    public Task getByIdTask(int id) {
        inMemoryHistoryManager.add(tasksMap.get(id));
        return tasksMap.get(id);
    }

    @Override
    public Object getByIdEpic(int id) {
        inMemoryHistoryManager.add(epicTasksMap.get(id));
        return epicTasksMap.get(id);
    }

    @Override
    public Object getByIdSubtask(int id) {
        inMemoryHistoryManager.add(subtasksMap.get(id));
        return subtasksMap.get(id);
    }

    public static int getIdTask() {
        return id++;
    }

    @Override
    public ArrayList<Subtask> getAllEpicSubtasks(int idEpic) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Integer idSub : epicTasksMap.get(idEpic).getIdOfSubtasksList()) {
            subtasks.add(subtasksMap.get(idSub));
        }
        return subtasks;
    }


    @Override
    public void deleteTask(int id) {
        allTaskList.remove(tasksMap.get(id));
        tasksMap.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        for (Integer idSubtask : epicTasksMap.get(id).getIdOfSubtasksList()) {
            allTaskList.remove(subtasksMap.remove(idSubtask));
        }
        allTaskList.remove(epicTasksMap.get(id));
        epicTasksMap.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        allTaskList.remove(subtasksMap.get(id));
        final Subtask subtask = subtasksMap.remove(id);
        ArrayList<Integer> arrayList = epicTasksMap.get(subtask.getIdOfEpic()).getIdOfSubtasksList();
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i) == subtask.getId()){
                arrayList.remove(i);
            }
        }
        epicTasksMap.get(subtask.getIdOfEpic()).setIdOfSubtasksList(arrayList);
        updateEpic(epicTasksMap.get(subtask.getIdOfEpic()),
                epicTasksMap.get(subtask.getIdOfEpic()).getStatus());

    }

    //оставлю для себя удаление с помощью одного метода
    /*public void deleteById(int id) {
        for (Object o : allTaskList) {
            if (o.getClass().getSimpleName().equals("Epic")) {
                Epic epic = (Epic) o;
                if (id == epic.getId()){
                    for (Integer idSub : epic.getIdOfSubtasksList()) {
                        allTaskList.remove(subtasksMap.get(idSub));
                        subtasksMap.remove(idSub);
                    }
                    epicTasksMap.remove(id);
                    allTaskList.remove(epic);
                    return;
                }
            } else if (o.getClass().getSimpleName().equals("Task")) {
                Task taskmanagerapp.task = (Task) o;
                if (id == taskmanagerapp.task.getId()){
                    tasksMap.remove(id);
                    allTaskList.remove(taskmanagerapp.task);
                    return;
                }
            } else {
                Subtask subtask = (Subtask) o;
                if (id == subtask.getId()){
                    subtasksMap.remove(id);
                    ArrayList<Integer> arrayList = epicTasksMap.get(subtask.getIdOfEpic()).getIdOfSubtasksList();
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (arrayList.get(i) == subtask.getId()){
                            arrayList.remove(i);
                        }
                    }
                    epicTasksMap.get(subtask.getIdOfEpic()).setIdOfSubtasksList(arrayList);

                    allTaskList.remove(subtask);
                    if (epicTasksMap.get(subtask.getIdOfEpic()).getIdOfSubtasksList().isEmpty()){
                        epicTasksMap.get(subtask.getIdOfEpic()).setStatus(Task.Status.NEW);
                    } else if (epicTasksMap.get(subtask.getIdOfEpic()).getIdOfSubtasksList().size() >= 1){
                        updateSubtask(subtasksMap.get(epicTasksMap.get(subtask.getIdOfEpic()).getIdOfSubtasksList().get(0)),
                                subtasksMap.get(epicTasksMap.get(subtask.getIdOfEpic()).getIdOfSubtasksList().get(0)).getStatus());
                                }
                                return;
                }
            }
        }
    }*/

    @Override
    public void deleteAllTasks(ArrayList<Task> taskList) {
        for (Task task : taskList) {
            allTaskList.remove(task);
            tasksMap.remove(task.getId());
        }
    }

    @Override
    public void deleteAllEpics(ArrayList<Epic> epicList) {
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

    @Override
    public void deleteAllSubtasks(ArrayList<Subtask> subtaskList) {
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
                epicTasksMap.get(subtask.getIdOfEpic()).setStatus(Status.NEW);
            }
        }
    }

    @Override
    public void updateTask(Task task, Status status) {
        allTaskList.remove(tasksMap.get(task.getId()));
        task.setStatus(status);
        setTask(task);
    }

    @Override
    public void updateEpic(Epic epic, Status status) {
        if (epic.getIdOfSubtasksList().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            if (status == Status.NEW || status == Status.DONE) {
                for (Integer idSub : epicTasksMap.get(epic.getId()).getIdOfSubtasksList()) {
                    if (subtasksMap.get(idSub).getStatus() != status) {
                        subtasksMap.get(idSub).setStatus(status);
                    }
                }
                removeSubtask(epic, status);
            } else if (status == Status.IN_PROGRESS) {
                removeSubtask(epic, status);
            }
        }
    }

    @Override
    public void updateSubtask(Subtask subtask, Status status) {
        allTaskList.remove(subtasksMap.get(subtask.getId()));
        subtask.setStatus(status);
        allTaskList.add(subtask);
        subtasksMap.put(subtask.getId(), subtask);
        if (status == Status.NEW || status == Status.DONE) {
            for (Integer idSub : epicTasksMap.get(subtask.getIdOfEpic()).getIdOfSubtasksList()) {
                if (subtask.getStatus() != subtasksMap.get(idSub).getStatus()) {
                    epicTasksMap.get(subtask.getIdOfEpic()).setStatus(Status.IN_PROGRESS);
                    return;
                }

            }
            epicTasksMap.get(subtask.getIdOfEpic()).setStatus(status);
        }
        else if (status == Status.IN_PROGRESS) {
            epicTasksMap.get(subtask.getIdOfEpic()).setStatus(Status.IN_PROGRESS);
        }
    }

    private void removeSubtask(Epic epic, Status status) {
        epic.setIdOfSubtasksList(epicTasksMap.get(epic.getId()).getIdOfSubtasksList());
        allTaskList.remove(epic);
        epic.setStatus(status);
        setTask(epic);
    }

    @Override
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
                updateSubtask(newSubtask, newSubtask.getStatus());
                break;
        }
    }

    @Override
    public List<Task> getHistory(){
        return inMemoryHistoryManager.getHistory();
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
