package taskmanagerapp.http;

import taskmanagerapp.manager.utils.exeptions.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String API_TOKEN;
    private final URI urlKVServer;
    private final HttpClient httpClient;
    private final String SAVE_URI = "save/alltasks?API_TOKEN=";
    private final String URI_LOAD = "load/alltasks?API_TOKEN=";


    public KVTaskClient(String path) {
    if (path == null) throw new  NullPointerException();
    urlKVServer = URI.create(path);
    httpClient = HttpClient.newHttpClient();
    API_TOKEN = registrationOnKVServer();

    }

    public void put(String json) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(urlKVServer + SAVE_URI + API_TOKEN))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String load() {
        String responseToManager = "";
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlKVServer + URI_LOAD + API_TOKEN))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            responseToManager = response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return responseToManager;
    }

    private String registrationOnKVServer() {
        String result = "";
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlKVServer + "register"))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ResponseStatusException();
            }
            result = response.body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
