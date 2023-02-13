package taskmanagerapp.manager;

import taskmanagerapp.interfaces.HistoryManager;
import taskmanagerapp.interfaces.TaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
