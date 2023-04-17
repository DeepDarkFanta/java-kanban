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
import static org.junit.jupiter.api.Assertions.assertAll;

public class HttpTaskServerTest {
    private HttpTaskServer httpTaskServer;
    private KVServer kvServer;
    private Gson gson;
    private HttpClient httpClient;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private String requestMessageOfTask;
    private String requestMessageOfEpic;
    private String requestMessageOfSubtask;
    private final String PRIORITIZED_URI = "http://localhost:8080/tasks/prioritized";
    private static final String TASK_URI = "http://localhost:8080/tasks/task/";
    private static final String EPICS_URI = "http://localhost:8080/tasks/epic/";
    private static final String SUBTASKS_URI = "http://localhost:8080/tasks/subtask/";
    private static  final String TASKS_URI = "http://localhost:8080/tasks/";
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
        sendPostMessage(httpClient, URI.create(TASK_URI), requestMessageOfTask);

        requestMessageOfEpic = "{\n" +
                "        \"title\": \"is\",\n" +
                "        \"description\": \"is\"\n" +
                "}";
        sendPostMessage(httpClient, URI.create(EPICS_URI), requestMessageOfEpic);

        requestMessageOfSubtask = "{\n" +
                "        \"title\": \"up\",\n" +
                "        \"description\": \"is\",\n" +
                "        \"duration\": 120,\n" +
                "        \"startTime\": \"20.12.2014 20:12:23\",\n" +
                "        \"idEpic\": 1\n" +
                "}";
        sendPostMessage(httpClient, URI.create(SUBTASKS_URI), requestMessageOfSubtask);
    }

    @AfterEach
    public void stopServersAfterEveryoneTests() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    public void getPrioritizedTasksFromServerTest() {
        String response = sendGetWaitBody(PRIORITIZED_URI);
        Type type = new TypeToken<List<Task>>() {}.getType();
        List<Task> priorityTimeTasksFromServerList = gson.fromJson(response, type);

        List<Task> priorityTimeTaskFromManagerList = httpTaskServer.taskManager.getPrioritizedTasks();

        assertThat(priorityTimeTaskFromManagerList.size()).isEqualTo(priorityTimeTasksFromServerList.size());

        for (int i = 0; i < priorityTimeTaskFromManagerList.size(); i++) {
            Task taskFromManager = priorityTimeTaskFromManagerList.get(i);
            Task taskFromServer = priorityTimeTasksFromServerList.get(i);

            assertThat(taskFromManager.getTaskType()).isEqualTo(taskFromServer.getTaskType());
            assertThat(taskFromManager.getId()).isEqualTo(taskFromServer.getId());
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
        assertThat(jsonObject.get("idEpic").getAsInt()).isEqualTo(subtask.getIdOfEpic());
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
        sendPostMessage(httpClient, URI.create(TASK_URI + "?update"), requestUpdateTask);
        assertThat(httpTaskServer.taskManager.getTasksList().get(0).getStatus()).isEqualTo(Status.IN_PROGRESS);

        String requestMessageEpic = "{\n" +
                "        \"status\": \"IN_PROGRESS\",\n" +
                "        \"id\": 1\n" +
                "        }";
        assertThat(httpTaskServer.taskManager.getEpicTasksList().get(0).getStatus()).isEqualTo(Status.NEW);
        sendPostMessage(httpClient, URI.create(EPICS_URI + "?update"), requestMessageEpic);
        assertThat(httpTaskServer.taskManager.getEpicTasksList().get(0).getStatus()).isEqualTo(Status.IN_PROGRESS);

       String requestMessageSubtask = "{\n" +
               "        \"status\": \"DONE\",\n" +
               "        \"id\": 2\n" +
               "        }";
        assertThat(httpTaskServer.taskManager.getSubtasksList().get(0).getStatus()).isEqualTo(Status.NEW);
        sendPostMessage(httpClient, URI.create(SUBTASKS_URI + "?update"), requestMessageSubtask);
        assertThat(httpTaskServer.taskManager.getSubtasksList().get(0).getStatus()).isEqualTo(Status.DONE);
       assertThat(httpTaskServer.taskManager.getEpicTasksList().get(0).getStatus()).isEqualTo(Status.DONE);
    }

    @Test
    public void deleteTaskEpicSubtaskTest() {
        sendDeleteMessage(TASK_URI + "?id=0");
        sendDeleteMessage(SUBTASKS_URI + "?id=2");
        sendDeleteMessage(EPICS_URI + "?id=1");
        sendDeleteMessage(TASK_URI);
        sendDeleteMessage(SUBTASKS_URI);
        sendDeleteMessage(EPICS_URI);

        assertAll(
                () -> assertThat(httpTaskServer.taskManager.getTasksList().isEmpty()).isTrue(),
                () -> assertThat(httpTaskServer.taskManager.getSubtasksList().isEmpty()).isTrue(),
                () ->  assertThat(httpTaskServer.taskManager.getEpicTasksList().isEmpty()).isTrue(),
                () -> assertThat(httpTaskServer.taskManager.getTasksList().isEmpty()).isTrue(),
                () -> assertThat(httpTaskServer.taskManager.getSubtasksList().isEmpty()).isTrue(),
                () -> assertThat(httpTaskServer.taskManager.getEpicTasksList().isEmpty()).isTrue()
        );
    }

    @Test
    public void getTasksEpicSubtaskTest() {
        String responseTask = sendGetWaitBody(TASK_URI + "?id=0");
        Task task = gson.fromJson(responseTask, Task.class);

        String responseEpic = sendGetWaitBody(EPICS_URI + "?id=1");
        Epic epic = gson.fromJson(responseEpic, Epic.class);

        String responseSubtask = sendGetWaitBody(SUBTASKS_URI + "?id=2");
        Subtask subtask = gson.fromJson(responseSubtask, Subtask.class);

        assertAll(
                () -> assertThat(httpTaskServer.taskManager.getByIdTask(0)).isEqualTo(task),
                () -> assertThat(httpTaskServer.taskManager.getByIdEpic(1)).isEqualTo(epic),
                () -> assertThat(httpTaskServer.taskManager.getByIdSubtask(2)).isEqualTo(subtask)
        );

        String responseWithAllTask = sendGetWaitBody(TASKS_URI);
        Type type = new TypeToken<ArrayList<Task>>() {}.getType();
        ArrayList<Task> tasks = gson.fromJson(responseWithAllTask, type);
        tasks.sort(Comparator.comparingInt(Task::getId));

        ArrayList<Task> inManagerTasks = httpTaskServer.taskManager.getAllTasks();
        inManagerTasks.sort(Comparator.comparingInt(Task::getId));
        assertThat(tasks.size()).isEqualTo(inManagerTasks.size());

        for (int i = 0; i < inManagerTasks.size(); i++) {
            Task tasksInMemory = inManagerTasks.get(i);
            Task tasksFromResponse = tasks.get(i);

            assertThat(tasksFromResponse.getTaskType()).isEqualTo(tasksInMemory.getTaskType());
            assertThat(tasksFromResponse.getId()).isEqualTo(tasksInMemory.getId());
        }

        String tasksListJson = gson.toJson(httpTaskServer.taskManager.getTasksList());
        String tasksFromServer = sendGetWaitBody(TASK_URI);

        String epicsListJson = gson.toJson(httpTaskServer.taskManager.getEpicTasksList());
        String epicsFromServer = sendGetWaitBody(EPICS_URI);

        String subtasksListJson = gson.toJson(httpTaskServer.taskManager.getSubtasksList());
        String subtasksFromServer = sendGetWaitBody(SUBTASKS_URI);

        assertAll(
                () -> assertThat(tasksFromServer).isEqualTo(tasksListJson),
                () -> assertThat(epicsFromServer).isEqualTo(epicsListJson),
                () -> assertThat(subtasksFromServer).isEqualTo(subtasksListJson)
        );
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
            assertThat(response.statusCode()).isEqualTo(200);
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
            assertThat(response.statusCode()).isEqualTo(200);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
