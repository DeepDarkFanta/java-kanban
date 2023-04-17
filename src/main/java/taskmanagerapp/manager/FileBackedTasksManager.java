package taskmanagerapp.manager;

import taskmanagerapp.enums.Status;
import taskmanagerapp.manager.utils.exeptions.ManagerSaveException;
import taskmanagerapp.tasks.Epic;
import taskmanagerapp.tasks.Subtask;
import taskmanagerapp.tasks.Task;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File fileOfTasksAndHistory;
    private final String SEPARATOR = System.lineSeparator();


    public FileBackedTasksManager(String path) {
        fileOfTasksAndHistory = new File(path);
        if (fileOfTasksAndHistory.length() != 0) {
            Deque<String> strings = loadFromFile(fileOfTasksAndHistory);
            dataRecovery(fromString(strings), Objects.requireNonNull(strings.pollLast()));
        }
    }

    private Map<Integer, Task> fromString(Deque<String> strings){
        Map<Integer, Task> tasksMap = new TreeMap<>();
        while (!Objects.equals(strings.peek(), "")) {
            String[] line = strings.pop().split(",");
            switch (line[1]) {
                case "TASK":
                    Task task = new Task(line[2], line[4], Integer.parseInt(line[5]), (line[6])); // !!!!!!!!!!
                    task.setId(Integer.parseInt(line[0]));
                    task.setStatus(Status.valueOf(line[3]));
                    tasksMap.put(task.getId(), task);
                    break;
                case "EPIC":
                    Epic epic = new Epic(line[2], line[4]);
                    epic.setId(Integer.parseInt(line[0]));
                    epic.setStatus(Status.valueOf(line[3]));
                    tasksMap.put(epic.getId(), epic);
                    break;
                case "SUBTASK":
                    Subtask subtask = new Subtask(line[2], line[4],
                            (Epic) tasksMap.get(Integer.parseInt(line[7])), Integer.parseInt(line[5]),  line[6]);
                    subtask.setId(Integer.parseInt(line[0]));
                    subtask.setStatus(Status.valueOf(line[3]));
                    tasksMap.put(subtask.getId(), subtask);
                    break;
            }
        }
            return tasksMap;
    }

    private void dataRecovery(Map<Integer, Task> tasksMap, String history) {
        Status status;
        String[] lineHistory = history.split(",");
        for (Map.Entry<Integer, Task> integerTaskEntry : tasksMap.entrySet()) {
            switch (integerTaskEntry.getValue().getTaskType()) {
                case TASK:
                    status = integerTaskEntry.getValue().getStatus();
                    super.setTask(integerTaskEntry.getValue());
                    super.updateTask(integerTaskEntry.getValue(), status);
                    break;
                case EPIC:
                    status = integerTaskEntry.getValue().getStatus();
                    super.setTask(integerTaskEntry.getValue());
                    super.updateEpic((Epic) integerTaskEntry.getValue(), status);
                    break;
                case SUBTASK:
                    status = integerTaskEntry.getValue().getStatus();
                    super.setTask(integerTaskEntry.getValue());
                    super.updateSubtask((Subtask) integerTaskEntry.getValue(),status);
                    break;
            }
        }
        if (!lineHistory[0].equals("")) {
            for (String s : lineHistory) {
                Task task = tasksMap.get(Integer.parseInt(s));
                int id = Integer.parseInt(s);
                switch (task.getTaskType()) {
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

    protected void save() {
       allTaskList.sort(Comparator.comparingInt(Task::getId));

        StringBuilder line = new StringBuilder("id,type,name,status,description,duration,startTime,epic" + SEPARATOR);
        for (Task task : allTaskList) {
            line.append(toString(task));
        }
        line.append(SEPARATOR)
                .append(toString(getHistory()));
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileOfTasksAndHistory))){
            fileWriter.write(line.toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new ManagerSaveException();
        }
    }

    private static Deque<String> loadFromFile(File file)  {
        Deque<String> strings = new ArrayDeque<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                strings.addLast(bufferedReader.readLine());
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
        return strings;
    }

    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId())
                .append(',')
                .append(task.getTaskType())
                .append(',')
                .append(task.getTitle())
                .append(',')
                .append(task.getStatus().toString())
                .append(',')
                .append(task.getDescription())
                .append(',')
                .append(task.getDuration().toMinutes())
                .append(',')
                .append(task.getStartTime().format( DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        if (task instanceof Subtask) {
            sb.append(',')
                    .append(((Subtask) task).getIdOfEpic());
        }
        return sb.append(SEPARATOR).toString();
    }

    private String toString(List<Task> historyTaskArray) {
        StringBuilder sb = new StringBuilder();
        if (historyTaskArray != null) {
            sb.append(historyTaskArray.stream()
                    .map(x -> String.valueOf(x.getId()))
                    .collect(Collectors.joining(",")));
        }
        return sb.toString();
    }

    @Override
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
        Epic task = super.getByIdEpic(id);
        save();
        return task;
    }

    @Override
    public Subtask getByIdSubtask(int id) {
        Subtask task = super.getByIdSubtask(id);
        save();
        return task;
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
    public ArrayList<Task> getAllTasks() {
        return allTaskList;
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }
}
