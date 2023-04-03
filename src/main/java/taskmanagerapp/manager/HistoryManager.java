package taskmanagerapp.manager;

import taskmanagerapp.tasks.Task;
import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void linkLast(Task task);

    void taskDeleteInHistory(Task task);
}
