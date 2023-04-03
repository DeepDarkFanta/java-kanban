package taskmanagerapp.manager;

import java.io.File;

public class Managers {
    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static  FileBackedTasksManager getDefaultFileManager(File file) {
        return new FileBackedTasksManager(file);
    }
}
