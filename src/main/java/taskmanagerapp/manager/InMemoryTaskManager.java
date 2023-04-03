package taskmanagerapp.manager;

import taskmanagerapp.enums.Status;
import taskmanagerapp.enums.TaskType;
import taskmanagerapp.manager.utils.exeptions.ManagerCreateTimeTaskException;
import taskmanagerapp.manager.utils.exeptions.ManagerIdTaskException;
import taskmanagerapp.tasks.*;
import java.time.ZonedDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Epic> epicTasksMap;
    protected final HashMap<Integer, Task> tasksMap;
    protected final HashMap<Integer, Subtask> subtasksMap;
    protected final ArrayList<Task> allTaskList;
    protected final HistoryManager inMemoryHistoryManager;
    protected final Map<ZonedDateTime, Task> sortedDateTasksMap;

    public InMemoryTaskManager() {
        epicTasksMap = new HashMap<>();
        tasksMap = new HashMap<>();
        subtasksMap = new HashMap<>();
        allTaskList = new ArrayList<>();
        inMemoryHistoryManager = Managers.getDefaultHistory();
        sortedDateTasksMap = new TreeMap<>();
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
            if (!tasksMap.containsKey(id)) {
                throw new ManagerIdTaskException();
            }
        Task task = tasksMap.getOrDefault(id, null);
            if (task !=null) inMemoryHistoryManager.linkLast(task);
        return task;
    }

    @Override
    public Epic getByIdEpic(int id) {
        if (!epicTasksMap.containsKey(id)) {
            throw new ManagerIdTaskException();
        }
        Epic epic = epicTasksMap.getOrDefault(id, null);
        if (epic !=null) inMemoryHistoryManager.linkLast(epic);
        return epic;
    }

    @Override
    public Subtask getByIdSubtask(int id) {
        if (!subtasksMap.containsKey(id)) {
            throw new ManagerIdTaskException();
        }
        Subtask subtask = subtasksMap.get(id);
        if (subtask != null) inMemoryHistoryManager.linkLast(subtask);
        return subtask;
    }

    /*public int getIdTask() {
        return id++;
    }*/

    @Override
    public List<Subtask> getAllEpicSubtasks(int idEpic) {
        List<Subtask> subtasks = new ArrayList<>();
        if (!epicTasksMap.containsKey(idEpic)) {
            throw new ManagerIdTaskException();
        } else {
            for (Integer idSub : epicTasksMap.get(idEpic).getIdOfSubtasksList()) {
                subtasks.add(subtasksMap.get(idSub));
            }
        }
        return subtasks;
    }

    @Override
    public void deleteTask(int id) {
        if (!tasksMap.containsKey(id)) {
            throw new ManagerIdTaskException();
        } else {
            Task task = tasksMap.get(id);
            inMemoryHistoryManager.taskDeleteInHistory(task);
            allTaskList.remove(task);
            tasksMap.remove(id);
        }
    }

    @Override
    public void deleteEpic(int id) {
        if (!epicTasksMap.containsKey(id)) {
            throw new ManagerIdTaskException();
        } else {
            Epic epic = epicTasksMap.get(id);
            for (Integer idSubtask : epic.getIdOfSubtasksList()) {
                inMemoryHistoryManager.taskDeleteInHistory(subtasksMap.get(idSubtask));
                allTaskList.remove(subtasksMap.remove(idSubtask));
            }
            inMemoryHistoryManager.taskDeleteInHistory(epic);
            allTaskList.remove(epic);
            epicTasksMap.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        if (!subtasksMap.containsKey(id)) {
            throw new ManagerIdTaskException();
        } else {
            if (!subtasksMap.containsKey(id)) {
                throw new ManagerIdTaskException();
            } else {
                allTaskList.remove(subtasksMap.get(id));
                sortedDateTasksMap.remove(subtasksMap.get(id).getStartTime());
                final Subtask subtask = subtasksMap.remove(id);

                inMemoryHistoryManager.taskDeleteInHistory(subtask);

                Epic epic = epicTasksMap.get(subtask.getIdOfEpic());
                ArrayList<Integer> arrayList = epic.getIdOfSubtasksList();
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i) == subtask.getId()) {
                        arrayList.remove(i);
                    }
                }
                epic.setIdOfSubtasksList(arrayList);
                updateEpic(epic, epic.getStatus());
            }
        }
    }

    @Override
    public void deleteAllTasks(ArrayList<Task> taskList) {
        for (Task task : taskList) {
            inMemoryHistoryManager.taskDeleteInHistory(task);
            allTaskList.remove(task);
            tasksMap.remove(task.getId());
        }
    }

    @Override
    public void deleteAllEpics(ArrayList<Epic> epicList) {
        for (Epic epic : epicList) {
            for (Integer idSub : epic.getIdOfSubtasksList()) {
                inMemoryHistoryManager.taskDeleteInHistory(subtasksMap.get(idSub));
                allTaskList.remove(subtasksMap.get(idSub));
                tasksMap.remove(idSub);
                subtasksMap.remove(idSub);
            }
            inMemoryHistoryManager.taskDeleteInHistory(epic);
            allTaskList.remove(epic);
            epicTasksMap.remove(epic.getId());
        }
    }

    @Override
    public void deleteAllSubtasks(ArrayList<Subtask> subtaskList) {
        if (subtaskList != null) {
            for (Subtask subtask : subtaskList) {
                sortedDateTasksMap.remove(subtask.getStartTime());
                ArrayList<Integer> epicList = epicTasksMap.get(subtask.getIdOfEpic()).getIdOfSubtasksList();
                for (int i = 0; i < epicList.size(); i++) {
                    if (epicList.get(i) == subtask.getId()) {
                        epicList.remove(i);
                        break;
                    }
                }
                inMemoryHistoryManager.taskDeleteInHistory(subtask);
                allTaskList.remove(subtask);
                subtasksMap.remove(subtask.getId());
                epicTasksMap.get(subtask.getIdOfEpic()).setStatus(Status.NEW);
            }
        }
    }

    @Override
    public void updateTask(Task task, Status status) {
        allTaskList.remove(tasksMap.get(task.getId()));
        sortedDateTasksMap.remove(task.getStartTime());
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

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedDateTasksMap.values());
    }

    public boolean isTimeCrossing(Task task) {

            if (sortedDateTasksMap.containsKey(task.getStartTime())) {
                return true;
            }
            sortedDateTasksMap.put(task.getStartTime(), task);
            List<Task> tasks = getPrioritizedTasks();
            int taskIdBetween = tasks.indexOf(task);
            int tasksSize = tasks.size() - 1;

            if (taskIdBetween == 0 && tasksSize == 0) {
                sortedDateTasksMap.put(task.getStartTime(), task);
            } else if (taskIdBetween == 0) {
                Task taskNext = tasks.get(1);
                if (task.getStartTime().plus(task.getDuration()).isAfter(taskNext.getStartTime())) {
                    sortedDateTasksMap.remove(task.getStartTime());
                    return true;
                }
            } else if (taskIdBetween == tasksSize) {
                Task taskPrev = tasks.get(tasksSize - 1);
                if (taskPrev.getStartTime().plus(taskPrev.getDuration()).isAfter(task.getStartTime())) {
                    sortedDateTasksMap.remove(task.getStartTime());
                    return true;
                }
            } else {
                Task taskPrev = tasks.get(taskIdBetween - 1);
                Task taskNext = tasks.get(taskIdBetween + 1);
                if (taskPrev.getStartTime().plus(taskPrev.getDuration()).isAfter(task.getStartTime())
                        || task.getStartTime().plus(task.getDuration()).isAfter(taskNext.getStartTime())) {
                    sortedDateTasksMap.remove(task.getStartTime());
                   return true;
                }
            }
        return false;
    }
    @Override
    public void setTask(Task task) {
        //проверка на пересечения по времени
        if ((task.getTaskType() == TaskType.TASK || task.getTaskType() == TaskType.SUBTASK ) && isTimeCrossing(task)) {
            throw new ManagerCreateTimeTaskException(task);
        }
        allTaskList.add(task);
        switch (task.getTaskType()) {
            case TASK:
                tasksMap.put(task.getId(), task);
                break;
            case EPIC:
                Epic newEpic = (Epic) task;
                epicTasksMap.put(newEpic.getId(), newEpic);
                break;
            case SUBTASK:
                Subtask newSubtask = (Subtask) task;
                Epic epic = epicTasksMap.get(newSubtask.getIdOfEpic());
                if (epic.getIdOfSubtasksList().isEmpty()) {
                    epic.setStartTime(newSubtask.getStartTime());
                    epic.setEndTime(newSubtask.getStartTime().plus(newSubtask.getDuration()));
                } else if (epic.getStartTime().isAfter(newSubtask.getStartTime())){
                    epic.setStartTime(newSubtask.getStartTime());
                } else if (newSubtask.getStartTime().isAfter(epic.getEndTime())) {
                        epic.setEndTime(newSubtask.getStartTime().plus(newSubtask.getDuration()));
                    }
                epic.setDuration(epic.getDuration().plus(task.getDuration()));
                ArrayList<Integer> idSubInEpicList = epicTasksMap.get(newSubtask.getIdOfEpic()).getIdOfSubtasksList();
                idSubInEpicList.add(newSubtask.getId());
                subtasksMap.put(newSubtask.getId(), newSubtask);
                updateSubtask(newSubtask, newSubtask.getStatus());
                break;
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return allTaskList;
    }

    @Override
    public List<Task> getHistory(){
         return inMemoryHistoryManager.getHistory();
    }

    public HashMap<Integer, Epic> getEpicTasksMap() {
        return epicTasksMap;
    }

    public HashMap<Integer, Task> getTasksMap() {
        return tasksMap;
    }

    public HashMap<Integer, Subtask> getSubtasksMap() {
        return subtasksMap;
    }
}
