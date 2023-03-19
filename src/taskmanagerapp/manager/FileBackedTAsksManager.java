package taskmanagerapp.manager;

import taskmanagerapp.enums.Status;
import taskmanagerapp.manager.utils.TaskIdComparator;
import taskmanagerapp.tasks.Epic;
import taskmanagerapp.tasks.Subtask;
import taskmanagerapp.tasks.Task;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileBackedTAsksManager extends InMemoryTaskManager implements TaskManager {
    File fileOfTasksAndHistory;

    public FileBackedTAsksManager() {
        fileOfTasksAndHistory = new File("src/taskmanagerapp/resources/csv/tasksAndHistory.csv");
        if (fileOfTasksAndHistory.length() != 0) {
            dataRecovery(loadFromFile(fileOfTasksAndHistory), historyFromString());
        }
    }

    static List<Integer> historyFromString(String value) {

    }

    private void dataRecovery(Map<Integer, Task> tasksMap, List<Task> taskList) {
        Status status;
        for (Map.Entry<Integer, Task> integerTaskEntry : tasksMap.entrySet()) {
            switch (integerTaskEntry.getValue().getClass().getSimpleName()) {
                case "Task":
                    status = integerTaskEntry.getValue().getStatus();
                    super.setTask(integerTaskEntry.getValue());
                    super.updateTask(integerTaskEntry.getValue(), status);
                    break;
                case "Epic":
                    status = integerTaskEntry.getValue().getStatus();
                    super.setTask((Epic) integerTaskEntry.getValue());
                    super.updateEpic((Epic) integerTaskEntry.getValue(), status);
                    break;
                case "Subtask":
                    status = integerTaskEntry.getValue().getStatus();
                    super.setTask(integerTaskEntry.getValue());
                    super.updateSubtask((Subtask) integerTaskEntry.getValue(),status);
                    break;
            }
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
        FileBackedTAsksManager fileBackedTAsksManager = new FileBackedTAsksManager();
        //Добавление задач
        fileBackedTAsksManager.setTask(task1);
        fileBackedTAsksManager.setTask(task2);
        fileBackedTAsksManager.setTask(epic1);
        fileBackedTAsksManager.setTask(subtask1);
        fileBackedTAsksManager.setTask(subtask2);
        fileBackedTAsksManager.setTask(subtask3);
        fileBackedTAsksManager.setTask(epic2);
        fileBackedTAsksManager.setTask(task3);

        fileBackedTAsksManager.getByIdTask(0);
        fileBackedTAsksManager.getByIdTask(1);
        fileBackedTAsksManager.getByIdEpic(2);
        fileBackedTAsksManager.getByIdSubtask(3);
        fileBackedTAsksManager.getByIdSubtask(4);
        fileBackedTAsksManager.getByIdSubtask(5);
        fileBackedTAsksManager.getByIdEpic(6);
        fileBackedTAsksManager.getHistory();
        FileBackedTAsksManager fileBackedTAsksManager1 = new FileBackedTAsksManager();
        System.out.println("22222");
    }


    @Override
    public ArrayList<Epic> getEpicTasksList() {
        return null;
    }

    @Override
    public ArrayList<Task> getTasksList() {
        return null;
    }

    @Override
    public ArrayList<Subtask> getSubtasksList() {
        return null;
    }

    @Override
    public Task getByIdTask(int id) {
        Task task = (Task) super.getByIdTask(id);
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

    }

    @Override
    public void deleteEpic(int id) {

    }

    @Override
    public void deleteSubtask(int id) {

    }

    @Override
    public void deleteAllTasks(ArrayList<Task> taskList) {

    }

    @Override
    public void deleteAllEpics(ArrayList<Epic> epicList) {

    }

    @Override
    public void deleteAllSubtasks(ArrayList<Subtask> subtaskList) {

    }

    @Override
    public void updateTask(Task task, Status status) {

    }

    @Override
    public void updateEpic(Epic epic, Status status) {

    }

    @Override
    public void updateSubtask(Subtask subtask, Status status) {

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

    private void save() {
        allTaskList.sort(new TaskIdComparator());
        StringBuilder line = new StringBuilder("id,type,name,status,description,epic\n");
        for (Task task : allTaskList) {
            line.append(toString(task));
        }
        line.append('\n')
                .append(toString(getHistory()));
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileOfTasksAndHistory))){
            fileWriter.write(line.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<Integer, Task> loadFromFile(File file)  {
        Map<Integer, Task> tasksMap = new TreeMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                String[] line = bufferedReader.readLine().split(",");
                if (line.length == 1) {
                    String lineHistory = bufferedReader.readLine();
                    break;
                }
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
                    case "":
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tasksMap;
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
        if (task.getClass().getSimpleName().equals("Subtask")) {
            sb.append(',')
                    .append(((Subtask) task).getIdOfEpic());
        }
        return sb.append("\n").toString();
    }

    private String toString(List<Task> historyTaskArray) {
        StringBuilder sb = new StringBuilder();
        if (historyTaskArray.size() == 0){
            sb.append('\n')
                    .append('0');
        } else {
            for (Task task1 : historyTaskArray) {
                sb.append(task1.getId()).append(',');
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
