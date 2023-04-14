package taskmanagerapp.http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagerapp.enums.Status;
import taskmanagerapp.manager.Managers;
import taskmanagerapp.server.KVServer;
import taskmanagerapp.tasks.Epic;
import taskmanagerapp.tasks.Subtask;
import taskmanagerapp.tasks.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class HttpTaskServerTest {
    private HttpTaskServer httpTaskServer;
    private KVServer kvServer;
    private Gson gson;
    private HttpClient httpClient;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private String requestMessageOfTask;
    private String requestMessageOfEpic;
    private String requestMessageOfSubtask;

    @BeforeEach
    public void startServersAddTasks() throws IOException {

        try (FileWriter fileWriter = new FileWriter("src/main/resources/tasksAndHistory.csv")){
            fileWriter.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        kvServer = new KVServer();
        kvServer.start();

        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();

        gson = Managers.getGsonFormattedZonedDateTime();
        httpClient = HttpClient.newHttpClient();
        requestMessageOfTask = "{\n" +
                "        \"title\": \"is\",\n" +
                "        \"description\": \"is\",\n" +
                "        \"duration\": 120,\n" +
                "        \"startTime\": \"20.12.2013 20:12:23\"\n" +
                "        }";
        sendPostMessage(httpClient, URI.create("http://localhost:8080/tasks/task/"), requestMessageOfTask);

        requestMessageOfEpic = "{\n" +
                "        \"title\": \"is\",\n" +
                "        \"description\": \"is\"\n" +
                "}";
        sendPostMessage(httpClient, URI.create("http://localhost:8080/tasks/epic/"), requestMessageOfEpic);

        requestMessageOfSubtask = "{\n" +
                "        \"title\": \"up\",\n" +
                "        \"description\": \"is\",\n" +
                "        \"duration\": 120,\n" +
                "        \"startTime\": \"20.12.2014 20:12:23\",\n" +
                "        \"idEpic\": 1\n" +
                "}";
        sendPostMessage(httpClient, URI.create("http://localhost:8080/tasks/subtask/"), requestMessageOfSubtask);
    }

    @AfterEach
    public void stopServersAfterEveryoneTests() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    public void getPrioritizedTasksFromServerTest() {
        String response = sendGetWaitBody("http://localhost:8080/tasks/prioritized");
        Type type = new TypeToken<List<Task>>() {}.getType();
        List<Task> priorityTimeTasksFromServerList = gson.fromJson(response, type);

        List<Task> priorityTimeTaskFromManagerList = httpTaskServer.taskManager.getPrioritizedTasks();

        assertThat(priorityTimeTaskFromManagerList.size() == priorityTimeTasksFromServerList.size()).isTrue();

        for (int i = 0; i < priorityTimeTaskFromManagerList.size(); i++) {
            Task taskFromManager = priorityTimeTaskFromManagerList.get(i);
            Task taskFromServer = priorityTimeTasksFromServerList.get(i);

            assertThat(taskFromManager.getTaskType()).isEqualTo(taskFromServer.getTaskType());
            assertThat(taskFromManager.getId() == taskFromServer.getId()).isTrue();
        }
    }

    @Test
    public void postAddTaskEpicSubtaskFromServerTest() {
        //add task test
        JsonElement jsonElement = JsonParser.parseString(requestMessageOfTask);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        assertThat(httpTaskServer.taskManager.getTasksList().isEmpty()).isFalse();
        Task task = httpTaskServer.taskManager.getTasksList().get(0);

        assertThat(jsonObject.get("title").getAsString()).isEqualTo(task.getTitle());
        assertThat(jsonObject.get("description").getAsString()).isEqualTo(task.getDescription());
        assertThat(jsonObject.get("duration").getAsInt()).isEqualTo(task.getDuration().toMinutes());
        assertThat(jsonObject.get("startTime").getAsString()).isEqualTo(task.getStartTime().format(FORMATTER));

        //add epic test
        jsonElement = JsonParser.parseString(requestMessageOfEpic);
        jsonObject = jsonElement.getAsJsonObject();

        assertThat(httpTaskServer.taskManager.getEpicTasksList().isEmpty()).isFalse();
        Epic epic = httpTaskServer.taskManager.getEpicTasksList().get(0);

        assertThat(jsonObject.get("title").getAsString()).isEqualTo(epic.getTitle());
        assertThat(jsonObject.get("description").getAsString()).isEqualTo(epic.getDescription());

        //add subtask
        assertThat(httpTaskServer.taskManager.getSubtasksList().isEmpty()).isFalse();
        Subtask subtask = httpTaskServer.taskManager.getSubtasksList().get(0);

        jsonElement = JsonParser.parseString(requestMessageOfSubtask);
        jsonObject = jsonElement.getAsJsonObject();

        assertThat(jsonObject.get("title").getAsString()).isEqualTo(subtask.getTitle());
        assertThat(jsonObject.get("idEpic").getAsInt() == subtask.getIdOfEpic()).isTrue();
        assertThat(jsonObject.get("description").getAsString()).isEqualTo(subtask.getDescription());
        assertThat(jsonObject.get("duration").getAsInt()).isEqualTo(subtask.getDuration().toMinutes());
        assertThat(jsonObject.get("startTime").getAsString()).isEqualTo(subtask.getStartTime().format(FORMATTER));
    }

   @Test
    public void PostUpdateTaskEpicSubtaskStatusTest() {
        String requestUpdateTask = "{\n" +
                "        \"status\": \"IN_PROGRESS\",\n" +
                "        \"id\": 0\n" +
                "        }";
        assertThat(httpTaskServer.taskManager.getTasksList().get(0).getStatus()).isEqualTo(Status.NEW);
        sendPostMessage(httpClient, URI.create("http://localhost:8080/tasks/task/?update"), requestUpdateTask);
        assertThat(httpTaskServer.taskManager.getTasksList().get(0).getStatus()).isEqualTo(Status.IN_PROGRESS);

        String requestMessageEpic = "{\n" +
                "        \"status\": \"IN_PROGRESS\",\n" +
                "        \"id\": 1\n" +
                "        }";
        assertThat(httpTaskServer.taskManager.getEpicTasksList().get(0).getStatus()).isEqualTo(Status.NEW);
        sendPostMessage(httpClient, URI.create("http://localhost:8080/tasks/epic/?update"), requestMessageEpic);
        assertThat(httpTaskServer.taskManager.getEpicTasksList().get(0).getStatus()).isEqualTo(Status.IN_PROGRESS);

       String requestMessageSubtask = "{\n" +
               "        \"status\": \"DONE\",\n" +
               "        \"id\": 2\n" +
               "        }";
        assertThat(httpTaskServer.taskManager.getSubtasksList().get(0).getStatus()).isEqualTo(Status.NEW);
        sendPostMessage(httpClient, URI.create("http://localhost:8080/tasks/subtask/?update"), requestMessageSubtask);
        assertThat(httpTaskServer.taskManager.getSubtasksList().get(0).getStatus()).isEqualTo(Status.DONE);
       assertThat(httpTaskServer.taskManager.getEpicTasksList().get(0).getStatus()).isEqualTo(Status.DONE);
    }

    @Test
    public void deleteTaskEpicSubtaskTest() {
        sendDeleteMessage("http://localhost:8080/tasks/task/?id=0");
        assertThat(httpTaskServer.taskManager.getTasksList().isEmpty()).isTrue();

        sendDeleteMessage("http://localhost:8080/tasks/subtask/?id=2");
        assertThat(httpTaskServer.taskManager.getSubtasksList().isEmpty()).isTrue();

        sendDeleteMessage("http://localhost:8080/tasks/epic/?id=1");
        assertThat(httpTaskServer.taskManager.getEpicTasksList().isEmpty()).isTrue();
    }

    @Test
    public void deleteAllTaskEpicSubtaskTest() {
        sendDeleteMessage("http://localhost:8080/tasks/task/");
        assertThat(httpTaskServer.taskManager.getTasksList().isEmpty()).isTrue();

        sendDeleteMessage("http://localhost:8080/tasks/subtask/");
        assertThat(httpTaskServer.taskManager.getSubtasksList().isEmpty()).isTrue();

        sendDeleteMessage("http://localhost:8080/tasks/epic/");
        assertThat(httpTaskServer.taskManager.getEpicTasksList().isEmpty()).isTrue();
    }

    @Test
    public void getTasksEpicSubtaskTest() {
        String responseTask = sendGetWaitBody("http://localhost:8080/tasks/task/?id=0");
        Task task = gson.fromJson(responseTask, Task.class);
        assertThat(httpTaskServer.taskManager.getByIdTask(0).equals(task)).isTrue();

        String responseEpic = sendGetWaitBody("http://localhost:8080/tasks/epic/?id=1");
        Epic epic = gson.fromJson(responseEpic, Epic.class);
        assertThat(httpTaskServer.taskManager.getByIdEpic(1).equals(epic)).isTrue();

        String responseSubtask = sendGetWaitBody("http://localhost:8080/tasks/subtask/?id=2");
        Subtask subtask = gson.fromJson(responseSubtask, Subtask.class);
        assertThat(httpTaskServer.taskManager.getByIdSubtask(2).equals(subtask)).isTrue();

        String responseWithAllTask = sendGetWaitBody("http://localhost:8080/tasks/");
        Type type = new TypeToken<ArrayList<Task>>() {}.getType();
        ArrayList<Task> tasks = gson.fromJson(responseWithAllTask, type);
        tasks.sort(Comparator.comparingInt(Task::getId));

        ArrayList<Task> inManagerTasks = httpTaskServer.taskManager.getAllTasks();
        inManagerTasks.sort(Comparator.comparingInt(Task::getId));

        assertThat(tasks.size() == inManagerTasks.size()).isTrue();
        for (int i = 0; i < inManagerTasks.size(); i++) {
            Task tasksInMemory = inManagerTasks.get(i);
            Task tasksFromResponse = tasks.get(i);

            assertThat(tasksFromResponse.getTaskType()).isEqualTo(tasksInMemory.getTaskType());
            assertThat(tasksFromResponse.getId()).isEqualTo(tasksInMemory.getId());
        }
    }

    @Test
    public void getEpicsTasksOrSubtasksTest() {
        String tasksListJson = gson.toJson(httpTaskServer.taskManager.getTasksList());
        String tasksFromServer = sendGetWaitBody("http://localhost:8080/tasks/task/");
        assertThat(tasksFromServer).isEqualTo(tasksListJson);

        String epicsListJson = gson.toJson(httpTaskServer.taskManager.getEpicTasksList());
        String epicsFromServer = sendGetWaitBody("http://localhost:8080/tasks/epic/");
        assertThat(epicsFromServer).isEqualTo(epicsListJson);

        String subtasksListJson = gson.toJson(httpTaskServer.taskManager.getSubtasksList());
        String subtasksFromServer = sendGetWaitBody("http://localhost:8080/tasks/subtask/");
        assertThat(subtasksFromServer).isEqualTo(subtasksListJson);
    }

    public String sendGetWaitBody(String uri) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .build();

        String res = "";
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            res = response.body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public void sendDeleteMessage(String uri) {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(uri))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode() == 200).isTrue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendPostMessage(HttpClient httpClient, URI uri, String requestMessage) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestMessage))
                .uri(uri)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode() == 200).isTrue();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
