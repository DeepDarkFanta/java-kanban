package taskmanagerapp.manager;

import org.junit.jupiter.api.Test;
import taskmanagerapp.http.HttpTaskManager;
import taskmanagerapp.server.KVServer;
import taskmanagerapp.tasks.Epic;
import taskmanagerapp.tasks.Subtask;
import taskmanagerapp.tasks.Task;

import static org.assertj.core.api.Assertions.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager>{
    @Override
    HttpTaskManager createManager() {
        return Managers.getDefaultHttpTaskManager();
    }

    @Test
    public void restoreTasksAndHistoryFromKVServerTest() throws IOException {
        try (FileWriter fileWriter = new FileWriter("src/main/resources/tasksAndHistory.csv")){
            fileWriter.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        kvServer.stop();
        KVServer d = new KVServer();
        d.start();
        HttpTaskManager manager = new HttpTaskManager("src/main/resources/tasksAndHistory.csv","http://localhost:" + 8079 + "/");
        Task task = new Task("asd", "asd", 120, "12.02.2022 20:12:12");
        Epic epic = new Epic("qwew", "qwe");
        Subtask subtask = new Subtask("a", "as", epic, 120, "12.02.2021 17:20:16");
        manager.setTask(task);
        manager.setTask(epic);
        manager.setTask(subtask);
        manager.getByIdTask(task.getId());
        manager.getByIdEpic(epic.getId());

        //очищаю файл чтобы не баговался этот тест, ибо запись в файл работает
        try (FileWriter fileWriter = new FileWriter("src/main/resources/tasksAndHistory.csv")){
            fileWriter.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Проверка восстановления тасков
        HttpTaskManager managerAfter = new HttpTaskManager("src/main/resources/tasksAndHistory.csv","http://localhost:" + 8079 + "/");
        ArrayList<Task> tasksFromSecondManager = managerAfter.getAllTasks();
        ArrayList<Task> tasksFromFirstManager = manager.getAllTasks();

        assertThat(tasksFromFirstManager).containsExactlyInAnyOrderElementsOf(tasksFromSecondManager);

        //проверка восстановления истории
        List<Task> historyFromSecondManager = managerAfter.getHistory();
        List<Task> historyFromFirstManager = manager.getHistory();

        assertThat(historyFromFirstManager).containsExactlyInAnyOrderElementsOf(historyFromSecondManager);
        d.stop();
    }
}
