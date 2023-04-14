package taskmanagerapp.manager;

import org.junit.jupiter.api.Test;
import taskmanagerapp.tasks.Epic;
import taskmanagerapp.tasks.Subtask;
import taskmanagerapp.tasks.Task;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private final String SEPARATOR = System.lineSeparator();

    @Override
    FileBackedTasksManager createManager() {
        return Managers.getDefaultFileManager("test/resources/tasksAndHistoryTest.csv");
    }

    @Test
    public void saveInFileWithEmptyHistoryAndNoSubtaskTest() {
        cleanFile("test/resources/saveAndLoadTest.csv");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(
                "test/resources/saveAndLoadTest.csv"
        );
        Epic epic = new Epic("1", "3");
        Task task = new Task("1", "2", 120, "01.01.2023 10:10:10");
        Subtask subtask = new Subtask("2", "4", epic, 120, "02.01.2023 10:10:10");
        fileBackedTasksManager.setTask(epic);
        fileBackedTasksManager.setTask(task);
        fileBackedTasksManager.setTask(subtask);

        //без истории
        String csvString = readCSV();
        String stringTestNoneHistory = "id,type,name,status,description,duration,startTime,epic" + SEPARATOR +
                epic.getId() +",EPIC,1,NEW,3,120,02.01.2023 10:10:10" + SEPARATOR +
                task.getId() + ",TASK,1,NEW,2,120,01.01.2023 10:10:10" + SEPARATOR +
                subtask.getId() + ",SUBTASK,2,NEW,4,120,02.01.2023 10:10:10," + subtask.getIdOfEpic() + SEPARATOR + SEPARATOR;
        assertEquals(stringTestNoneHistory, csvString);

        //без сабов и истории
        fileBackedTasksManager.deleteAllSubtasks(fileBackedTasksManager.getSubtasksList());
        String stringTestNoneSubtask = "id,type,name,status,description,duration,startTime,epic"+ SEPARATOR +
                epic.getId() +",EPIC,1,NEW,3,120,02.01.2023 10:10:10" + SEPARATOR +
                task.getId() + ",TASK,1,NEW,2,120,01.01.2023 10:10:10" + SEPARATOR + SEPARATOR;
        csvString = readCSV();
        assertEquals(stringTestNoneSubtask, csvString);

        //история и сабы
        fileBackedTasksManager.setTask(subtask);
        fileBackedTasksManager.getByIdTask(task.getId());
        fileBackedTasksManager.getByIdSubtask(subtask.getId());
        fileBackedTasksManager.getByIdEpic(epic.getId());
        csvString = readCSV();

        String stringTestWithHistoryAndTask = "id,type,name,status,description,duration,startTime,epic" + SEPARATOR +
                epic.getId() + ",EPIC,1,NEW,3,240,02.01.2023 10:10:10" + SEPARATOR +
                task.getId() + ",TASK,1,NEW,2,120,01.01.2023 10:10:10" + SEPARATOR +
                subtask.getId() + ",SUBTASK,2,NEW,4,120,02.01.2023 10:10:10," + subtask.getIdOfEpic() + SEPARATOR +
                SEPARATOR +
                task.getId() + "," + subtask.getId() + "," + epic.getId() + SEPARATOR;
        assertEquals(stringTestWithHistoryAndTask, csvString);
    }

    @Test
    public void loadFromFileShouldDeserializedFromCSVinObjectTaskAndAddOnManagerTest() {

        cleanFile("test/resources/loadFromFileTest.csv");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(
                "test/resources/loadFromFileTest.csv"
        );
        Task task = new Task("1", "2", 120, "01.01.2023 10:10:10");
        Epic epic = new Epic("1", "3");
        Subtask subtask = new Subtask("2", "4", epic, 120, "02.01.2023 10:10:10");
        fileBackedTasksManager.setTask(epic);
        fileBackedTasksManager.setTask(task);
        fileBackedTasksManager.setTask(subtask);
        FileBackedTasksManager fileBackedTasksManager1 = new FileBackedTasksManager(
                "test/resources/loadFromFileTest.csv"
        );

        //task
        assertTrue(fileBackedTasksManager1.getTasksMap().containsKey(task.getId()));
        assertTrue(fileBackedTasksManager1.getTasksMap().containsValue(task));

        //epic
        assertTrue(fileBackedTasksManager1.getEpicTasksMap().containsKey(epic.getId()));
        assertTrue(fileBackedTasksManager1.getEpicTasksMap().containsValue(epic));
        assertTrue(fileBackedTasksManager1.getEpicTasksMap()
                .get(epic.getId())
                .getIdOfSubtasksList()
                .contains(subtask.getId()));

        //subtask
        assertTrue(fileBackedTasksManager1.getSubtasksMap().containsKey(subtask.getId()));
        assertTrue(fileBackedTasksManager1.getSubtasksMap().containsValue(subtask));
        assertEquals(epic.getId(), fileBackedTasksManager1.getSubtasksMap().get(subtask.getId()).getIdOfEpic());


    }

    private String readCSV() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("test/resources/saveAndLoadTest.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(SEPARATOR);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public void cleanFile(String path) {
        try (FileWriter fileWriter = new FileWriter(path)){
            fileWriter.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
