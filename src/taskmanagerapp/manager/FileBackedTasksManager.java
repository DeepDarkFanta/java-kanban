package taskmanagerapp.manager;

import taskmanagerapp.enums.Status;
import taskmanagerapp.manager.utils.TaskIdComparator;
import taskmanagerapp.manager.utils.exeptions.ManagerSaveException;
import taskmanagerapp.tasks.Epic;
import taskmanagerapp.tasks.Subtask;
import taskmanagerapp.tasks.Task;

import java.io.*;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File fileOfTasksAndHistory;
    private final String SEPARATOR = System.lineSeparator();

    public FileBackedTasksManager() {
        fileOfTasksAndHistory = new File("src/taskmanagerapp/resources/csv/tasksAndHistory.csv");
        if (fileOfTasksAndHistory.length() != 0) {
            Deque<String> strings = loadFromFile(fileOfTasksAndHistory);
            dataRecovery(fromString(strings), Objects.requireNonNull(strings.pollLast()));
        }
    }

    public static void main(String[] args) {
        Task task1 = new Task("убрать комнату", "Нужно убраться до 16:00");
        Task task2 = new Task("убрать комнату", "Нужно убраться до 16:00");
        Epic epic1 = new Epic("Надо сделать утром", "Лучше управиться до 13:00");
        Subtask subtask1 = new Subtask("Убрать кухню", "Лучше управиться до 13:00", epic1);
        Subtask subtask2 = new Subtask("Убрать кухню", "Лучше управиться до 13:00", epic1);
        Subtask subtask3 = new Subtask("Пылесосить", "Лучше управиться до 13:00", epic1);
        Epic epic2 = new Epic("Надо сделать утром", "Лучше управиться до 13:00");
        Task task3 = new Task("убрать комнату", "Нужно убраться до 16:00");

        //предварительно очищаем файл .csv чтобы не было двойной инициализации тасков
        TaskManager fileBackedTAsksManager = Managers.getDefaultFileManager();

        fileBackedTAsksManager.setTask(task1);
        fileBackedTAsksManager.setTask(task2);
        fileBackedTAsksManager.setTask(epic1);
        fileBackedTAsksManager.setTask(subtask1);
        fileBackedTAsksManager.setTask(subtask2);
        fileBackedTAsksManager.setTask(subtask3);
        fileBackedTAsksManager.setTask(epic2);
        fileBackedTAsksManager.setTask(task3);

        fileBackedTAsksManager.getByIdTask(0);
        fileBackedTAsksManager.getByIdEpic(2);
        fileBackedTAsksManager.getByIdSubtask(3);
        fileBackedTAsksManager.getByIdSubtask(5);
        fileBackedTAsksManager.getByIdSubtask(4);
        fileBackedTAsksManager.getByIdEpic(6);

        FileBackedTasksManager fileBackedTAsksManager1 = Managers.getDefaultFileManager();

        //Проверка первого менеджера со вторым на таски
        ArrayList<Task> test = fileBackedTAsksManager.getAllTasks();
        ArrayList<Task> test1 = fileBackedTAsksManager1.getAllTasks();
        System.out.println("совпадение тасков: " + Arrays.deepEquals(test.toArray(), test1.toArray()));

        //проверка историй
        System.out.println("совпадение истории: " + Arrays.deepEquals(fileBackedTAsksManager.getHistory().toArray(),
                fileBackedTAsksManager1.getHistory().toArray()));
    }

    private Map<Integer, Task> fromString(Deque<String> strings){
        Map<Integer, Task> tasksMap = new TreeMap<>();
        while (!Objects.equals(strings.peek(), "")) {
            String[] line = strings.pop().split(",");
            switch (line[1]) {
                case "Task":
                    Task task = new Task(line[2], line[4]);
                    task.setId(Integer.parseInt(line[0]));
                    task.setStatus(Status.valueOf(line[3]));
                    tasksMap.put(task.getId(), task);
                    break;
                case "Epic":
                    Epic epic = new Epic(line[2], line[4]);
                    epic.setId(Integer.parseInt(line[0]));
                    epic.setStatus(Status.valueOf(line[3]));
                    tasksMap.put(epic.getId(), epic);
                    break;
                case "Subtask":
                    Subtask subtask = new Subtask(line[2], line[4], (Epic) tasksMap.get(Integer.parseInt(line[5])));
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
            switch (integerTaskEntry.getValue().getClass().getSimpleName()) {
                case "Task":
                    status = integerTaskEntry.getValue().getStatus();
                    super.setTask(integerTaskEntry.getValue());
                    super.updateTask(integerTaskEntry.getValue(), status);
                    break;
                case "Epic":
                    status = integerTaskEntry.getValue().getStatus();
                    super.setTask(integerTaskEntry.getValue());
                    super.updateEpic((Epic) integerTaskEntry.getValue(), status);
                    break;
                case "Subtask":
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
                switch (task.getClass().getSimpleName()) {
                    case "Task":
                        getByIdTask(id);
                        break;
                    case "Epic":
                        getByIdEpic(id);
                        break;
                    case "Subtask":
                        getByIdSubtask(id);
                        break;
                }
            }
        }
    }

    private void save() {

        allTaskList.sort(new TaskIdComparator());
        StringBuilder line = new StringBuilder("id,type,name,status,description,epic" + SEPARATOR);
        for (Task task : allTaskList) {
            line.append(toString(task));
        }
        line.append(SEPARATOR)
                .append(toString(getHistory()));
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileOfTasksAndHistory))){
            fileWriter.write(line.toString());
        } catch (IOException e) {
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
                .append(task.getClass().getSimpleName())
                .append(',')
                .append(task.getTitle())
                .append(',')
                .append(task.getStatus().toString())
                .append(',')
                .append(task.getDescription());
        if (task instanceof Subtask) {
            sb.append(',')
                    .append(((Subtask) task).getIdOfEpic());
        }
        return sb.append(SEPARATOR).toString();
    }

    private String toString(List<Task> historyTaskArray) {
        StringBuilder sb = new StringBuilder();
        if (!historyTaskArray.isEmpty()) {
            // не очень понимаю как тут использовать String.join(), ибо у меня лист объектов...
            // а в join надо передавать лист стрингов
            for (Task task1 : historyTaskArray) {
                sb.append(task1.getId()).append(',');
            }
            if (sb.length() != 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
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
    public Object getByIdEpic(int id) {
        Task task = (Task) super.getByIdEpic(id);
        save();
        return task;
    }

    @Override
    public Object getByIdSubtask(int id) {
        Task task = (Task) super.getByIdSubtask(id);
        save();
        return task;
    }

    @Override
    public ArrayList<Subtask> getAllEpicSubtasks(int idEpic) {
        return null;
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
