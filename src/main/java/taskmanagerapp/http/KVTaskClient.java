package taskmanagerapp.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String API_TOKEN;
    private final URI urlKVServer;
    HttpClient httpClient;

    public KVTaskClient(String path) {
    urlKVServer = URI.create(path);
    httpClient = HttpClient.newHttpClient();
    API_TOKEN = registrationOnKVServer();
    }
    public void put(String json) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(urlKVServer + "save/alltasks?API_TOKEN=" + API_TOKEN))
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
                .uri(URI.create(urlKVServer + "load/alltasks?API_TOKEN=" + API_TOKEN))
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
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlKVServer + "register"))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
