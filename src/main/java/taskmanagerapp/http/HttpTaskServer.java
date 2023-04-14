package taskmanagerapp.http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import taskmanagerapp.enums.Status;
import taskmanagerapp.manager.Managers;
import taskmanagerapp.manager.TaskManager;
import taskmanagerapp.tasks.Epic;
import taskmanagerapp.tasks.Subtask;
import taskmanagerapp.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

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
            String method = httpExchange.getRequestMethod();
            if (method.equals("GET")) {
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
            String method = httpExchange.getRequestMethod();
            if (method.equals("GET")) {
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
            String method = httpExchange.getRequestMethod();
            if (method.equals("GET")) {
                String response = gson.toJson(taskManager.getHistory());
                sendText(httpExchange, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void SubtaskHandler(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String query = httpExchange.getRequestURI().getQuery();
            String method = httpExchange.getRequestMethod();
            switch (method) {
                case "GET":
                    if (query != null && Pattern.matches("^id=\\d+$", query)) {
                        int id = fromStringToInt(
                                query.replaceFirst("id=", "")
                        );
                        if (id != -1) {
                            String response = gson.toJson(taskManager.getByIdSubtask(id));
                            sendText(httpExchange, response);
                        }
                    } else if (Pattern.matches("^/tasks/subtask/$", path)) {
                        String response = gson.toJson(taskManager.getSubtasksList());
                        sendText(httpExchange, response);
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                    break;
                case "DELETE":
                    if (query != null && Pattern.matches("^id=\\d+$", query)) {
                        int id = fromStringToInt(
                            query.replaceFirst("id=", "")
                        );
                        if (id != -1) {
                            taskManager.deleteSubtask(id);
                            httpExchange.sendResponseHeaders(200, 0);
                        } else {
                            httpExchange.sendResponseHeaders(404, 0);
                        }
                    } else if (Pattern.matches("^/tasks/subtask/$", path) && query == null) {
                        taskManager.deleteAllSubtasks(taskManager.getSubtasksList());
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                    break;
                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    JsonElement jsonElement = JsonParser.parseString(body);
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
                        httpExchange.sendResponseHeaders(200, 0);
                    } else if (jsonElement.isJsonObject() && query.equals("update")) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        taskManager.updateSubtask(
                                taskManager.getSubtasksMap().get(jsonObject.get("id").getAsInt()),
                                Status.valueOf(jsonObject.get("status").getAsString())
                        );
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void EpicHandler(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String query = httpExchange.getRequestURI().getQuery();
            String requestMethod = httpExchange.getRequestMethod();

            switch (requestMethod) {
                case "GET":
                    if (query != null && Pattern.matches("^id=\\d+$", query)) {
                        String pathId = query.replaceFirst("id=", "");
                        int id = fromStringToInt(pathId);
                        if (id != -1) {
                            Epic epic = taskManager.getByIdEpic(id);
                            String response = gson.toJson(epic);
                            sendText(httpExchange, response);
                        }
                    } else if (Pattern.matches("^/tasks/epic/$", path) && query == null) {
                        String response = gson.toJson(taskManager.getEpicTasksList());
                        sendText(httpExchange, response);
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                    break;
                case "DELETE":
                    if (query != null && Pattern.matches("^id=\\d+$", query)) {
                        String pathId = query.replaceFirst("id=", "");
                        int id = fromStringToInt(pathId);
                        if (id != -1) {
                            taskManager.deleteEpic(id);
                            httpExchange.sendResponseHeaders(200, 0);
                        } else {
                            httpExchange.sendResponseHeaders(404, 0);
                        }
                    } else if (Pattern.matches("^/tasks/epic/$", path) && query == null) {
                        taskManager.deleteAllEpics(taskManager.getEpicTasksList());
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                    break;
                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    JsonElement jsonElement = JsonParser.parseString(body);
                    if (jsonElement.isJsonObject() && query == null) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        Epic epic = new Epic(
                                jsonObject.get("title").getAsString(),
                                jsonObject.get("description").getAsString()
                        );
                        taskManager.setTask(epic);
                        httpExchange.sendResponseHeaders(200, 0);
                    } else if (jsonElement.isJsonObject() && query.equals("update")) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        taskManager.updateEpic(
                                taskManager.getEpicTasksMap().get(jsonObject.get("id").getAsInt()),
                                Status.valueOf(jsonObject.get("status").getAsString())
                        );
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    break;
                default:
                    httpExchange.sendResponseHeaders(404, 0);
            }
        } catch (Exception e) {
            httpExchange.sendResponseHeaders(404, 0);
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void TaskHandler(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();
            String query = httpExchange.getRequestURI().getQuery();
            switch (requestMethod) {
                case "GET":
                    if (query != null && Pattern.matches("^id=\\d+$", query)) {
                        String pathId = query.replaceFirst("id=", "");
                        int id = fromStringToInt(pathId);
                        if (id != -1) {
                            Task task = taskManager.getByIdTask(id);
                            String response = gson.toJson(task);
                            sendText(httpExchange, response);
                        }
                    } else if (Pattern.matches("^/tasks/task/$", path) && query == null) {
                        String response = gson.toJson(taskManager.getTasksList());
                        sendText(httpExchange, response);
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                    break;
                case "DELETE":
                    if (query != null && Pattern.matches("^id=\\d+$", query)) {
                        String pathId = query.replaceFirst("id=", "");
                        int id = fromStringToInt(pathId);
                        if (id != -1) {
                            taskManager.deleteTask(id);
                            httpExchange.sendResponseHeaders(200, 0);
                        } else {
                            httpExchange.sendResponseHeaders(404, 0);
                        }
                    } else if (Pattern.matches("^/tasks/task/$", path) && query == null) {
                        taskManager.deleteAllTasks(taskManager.getTasksList());
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                    break;
                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    JsonElement jsonElement = JsonParser.parseString(body);
                    if (jsonElement.isJsonObject() && query == null) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        Task task = new Task(
                                jsonObject.get("title").getAsString(),
                                jsonObject.get("description").getAsString(),
                                jsonObject.get("duration").getAsInt(),
                                jsonObject.get("startTime").getAsString()
                        );
                        taskManager.setTask(task);
                        httpExchange.sendResponseHeaders(200, 0);
                    } else if (jsonElement.isJsonObject() && query.equals("update")) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        taskManager.updateTask(
                                taskManager.getTasksMap().get(jsonObject.get("id").getAsInt()),
                                Status.valueOf(jsonObject.get("status").getAsString())
                        );
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    break;
                default:
                    httpExchange.sendResponseHeaders(404, 0);
            }
        } catch (Exception e) {
            httpExchange.sendResponseHeaders(404, 0);
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private int fromStringToInt(String idPath) {
        try {
            return Integer.parseInt(idPath);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void start() {
        System.out.println("HttpTaskServer started on " + PORT + " port");
        httpServer.start();
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Server stoped on " + PORT + "port");
    }
}
