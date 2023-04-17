package taskmanagerapp.http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import taskmanagerapp.enums.Status;
import taskmanagerapp.manager.Managers;
import taskmanagerapp.manager.TaskManager;
import taskmanagerapp.manager.utils.exeptions.ManagerIdTaskException;
import taskmanagerapp.tasks.Epic;
import taskmanagerapp.tasks.Subtask;
import taskmanagerapp.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import static java.net.HttpURLConnection.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static jdk.internal.util.xml.XMLStreamWriter.DEFAULT_CHARSET;

public class HttpTaskServer {
    protected final TaskManager taskManager;
    private final HttpServer httpServer;
    private final int PORT = 8080;
    private final Gson gson;

    public HttpTaskServer() throws IOException {
        this.taskManager = Managers.getDefaultHttpTaskManager();
        this.httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        this.httpServer.createContext("/tasks/", this::TasksHandler);
        this.httpServer.createContext("/tasks/task", this::TaskHandler);
        this.httpServer.createContext("/tasks/epic", this::EpicHandler);
        this.httpServer.createContext("/tasks/subtask", this::SubtaskHandler);
        this.httpServer.createContext("/tasks/history", this::HistoryHandler);
        this.httpServer.createContext("/tasks/prioritized", this::PrioritizedTasks);
        this.gson = Managers.getGsonFormattedZonedDateTime();
    }

    private void PrioritizedTasks(HttpExchange httpExchange) {
        try {
            if (getHttpMethod(httpExchange).equals("GET")) {
                String response = gson.toJson(taskManager.getPrioritizedTasks());
                sendText(httpExchange, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void TasksHandler(HttpExchange httpExchange) {
        try {
            if (getHttpMethod(httpExchange).equals("GET")) {
               String response = gson.toJson(taskManager.getAllTasks());
               sendText(httpExchange, response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void HistoryHandler(HttpExchange httpExchange) {
        try {
            if (getHttpMethod(httpExchange).equals("GET")) {
                String response = gson.toJson(taskManager.getHistory());
                sendText(httpExchange, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void SubtaskHandler(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String query = httpExchange.getRequestURI().getQuery();
            String response = "";
            switch (getHttpMethod(httpExchange)) {
                case "GET":
                    if (query != null && Pattern.matches("^id=\\d+$", query)) {
                        int id = getIdFromUrl(query, httpExchange);
                        response = gson.toJson(taskManager.getByIdSubtask(id));
                    } else if (Pattern.matches("^/tasks/subtask/$", path)) {
                        response = gson.toJson(taskManager.getSubtasksList());
                    }
                    sendText(httpExchange, response);
                    break;
                case "DELETE":
                    if (query != null && Pattern.matches("^id=\\d+$", query)) {
                        int id = getIdFromUrl(query, httpExchange);
                        taskManager.deleteSubtask(id);
                    } else if (Pattern.matches("^/tasks/subtask/$", path) && query == null) {
                        taskManager.deleteAllSubtasks(taskManager.getSubtasksList());
                    } else {
                        httpExchange.sendResponseHeaders(HTTP_NOT_FOUND, 0);
                    }
                    httpExchange.sendResponseHeaders(HTTP_OK, 0);
                    break;
                case "POST":
                    JsonElement jsonElement = JsonParser.parseString(getBodyFromRequest(httpExchange));
                    if (jsonElement.isJsonObject() && query == null) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        Subtask subtask = new Subtask(
                                jsonObject.get("title").getAsString(),
                                jsonObject.get("description").getAsString(),
                                taskManager.getEpicTasksMap().get(jsonObject.get("idEpic").getAsInt()),
                                jsonObject.get("duration").getAsInt(),
                                jsonObject.get("startTime").getAsString()
                        );
                        taskManager.setTask(subtask);
                    } else if (jsonElement.isJsonObject() && query.equals("update")) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        taskManager.updateSubtask(
                                taskManager.getSubtasksMap().get(jsonObject.get("id").getAsInt()),
                                Status.valueOf(jsonObject.get("status").getAsString())
                        );
                    }
                    httpExchange.sendResponseHeaders(HTTP_OK, 0);
                    break;
                default:
                    httpExchange.sendResponseHeaders(HTTP_BAD_METHOD, 0);
            }
        } catch (Exception e) {
            httpExchange.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void EpicHandler(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String query = httpExchange.getRequestURI().getQuery();
            String response = "";
            switch (getHttpMethod(httpExchange)) {
                case "GET":
                    if (query != null && Pattern.matches("^id=\\d+$", query)) {
                        int id = getIdFromUrl(query, httpExchange);
                        Epic epic = taskManager.getByIdEpic(id);
                        response = gson.toJson(epic);
                    } else if (Pattern.matches("^/tasks/epic/$", path) && query == null) {
                        response = gson.toJson(taskManager.getEpicTasksList());
                    }
                    sendText(httpExchange, response);
                    break;
                case "DELETE":
                    if (query != null && Pattern.matches("^id=\\d+$", query)) {
                        int id = getIdFromUrl(query, httpExchange);
                        taskManager.deleteEpic(id);
                    } else if (Pattern.matches("^/tasks/epic/$", path) && query == null) {
                        taskManager.deleteAllEpics(taskManager.getEpicTasksList());
                    } else {
                        httpExchange.sendResponseHeaders(HTTP_NOT_FOUND, 0);
                    }
                    httpExchange.sendResponseHeaders(HTTP_OK, 0);
                    break;
                case "POST":
                    JsonElement jsonElement = JsonParser.parseString(getBodyFromRequest(httpExchange));
                    if (jsonElement.isJsonObject() && query == null) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        Epic epic = new Epic(
                                jsonObject.get("title").getAsString(),
                                jsonObject.get("description").getAsString()
                        );
                        taskManager.setTask(epic);
                    } else if (jsonElement.isJsonObject() && query.equals("update")) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        taskManager.updateEpic(
                                taskManager.getEpicTasksMap().get(jsonObject.get("id").getAsInt()),
                                Status.valueOf(jsonObject.get("status").getAsString())
                        );
                    }
                    httpExchange.sendResponseHeaders(HTTP_OK, 0);
                    break;
                default:
                    httpExchange.sendResponseHeaders(HTTP_NOT_FOUND, 0);
            }
        } catch (Exception e) {
            httpExchange.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void TaskHandler(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String query = httpExchange.getRequestURI().getQuery();
            String response = "";
            switch (getHttpMethod(httpExchange)) {
                case "GET":
                    if (query != null && Pattern.matches("^id=\\d+$", query)) {
                        int id = getIdFromUrl(query, httpExchange);
                        Task task = taskManager.getByIdTask(id);
                        response = gson.toJson(task);
                    } else if (Pattern.matches("^/tasks/task/$", path) && query == null) {
                        response = gson.toJson(taskManager.getTasksList());
                    }
                    sendText(httpExchange, response);
                    break;
                case "DELETE":
                    if (query != null && Pattern.matches("^id=\\d+$", query)) {
                        int id = getIdFromUrl(query, httpExchange);
                        taskManager.deleteTask(id);
                    } else if (Pattern.matches("^/tasks/task/$", path) && query == null) {
                        taskManager.deleteAllTasks(taskManager.getTasksList());
                    } else {
                        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                    }
                    httpExchange.sendResponseHeaders(HTTP_OK, 0);
                    break;
                case "POST":
                    JsonElement jsonElement = JsonParser.parseString(getBodyFromRequest(httpExchange));
                    if (jsonElement.isJsonObject() && query == null) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        Task task = new Task(
                                jsonObject.get("title").getAsString(),
                                jsonObject.get("description").getAsString(),
                                jsonObject.get("duration").getAsInt(),
                                jsonObject.get("startTime").getAsString()
                        );
                        taskManager.setTask(task);
                    } else if (jsonElement.isJsonObject() && query.equals("update")) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        taskManager.updateTask(
                                taskManager.getTasksMap().get(jsonObject.get("id").getAsInt()),
                                Status.valueOf(jsonObject.get("status").getAsString())
                        );
                    }
                    httpExchange.sendResponseHeaders(HTTP_OK, 0);
                    break;
                default:
                    httpExchange.sendResponseHeaders(HTTP_BAD_METHOD, 0);
            }
        } catch (Exception e) {
            httpExchange.sendResponseHeaders(HTTP_BAD_REQUEST, 0);
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private String getBodyFromRequest(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        return new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
    }

    private int fromStringToInt(String idPath) {
        try {
            return Integer.parseInt(idPath);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String getHttpMethod(HttpExchange httpExchange) {
        return httpExchange.getRequestMethod();
    }

    private int getIdFromUrl(String query, HttpExchange httpExchange) throws IOException {
        String pathId = query.replaceFirst("id=", "");
        int id = fromStringToInt(pathId);
        if (id < 0) {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
            throw new ManagerIdTaskException("negative id");
        }
        return id;
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        if (text.isEmpty()) {
            h.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
            return;
        }
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(HTTP_OK, resp.length);
        h.getResponseBody().write(resp);
    }

    public void start() {
        System.out.println("HttpTaskServer started on " + PORT + " port");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Server stoped on " + PORT + "port");
    }
}
