package taskmanagerapp.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import taskmanagerapp.adapter.ZonedDateTimeTypeAdapter;
import taskmanagerapp.http.HttpTaskManager;

import java.time.ZonedDateTime;

public class Managers {
    private static final int PORT = 8079;
    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static  FileBackedTasksManager getDefaultFileManager(String path) {
        return new FileBackedTasksManager(path);
    }

    public static HttpTaskManager getDefaultHttpTaskManager() {
        return new HttpTaskManager("src/main/resources/tasksAndHistory.csv","http://localhost:" + PORT + "/");
    }

    public static Gson getGsonFormattedZonedDateTime() {
        return new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter()).create();
    }
}
