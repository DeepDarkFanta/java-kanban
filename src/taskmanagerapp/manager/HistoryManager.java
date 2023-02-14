package taskmanagerapp.manager;
import taskmanagerapp.tasks.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();
}
