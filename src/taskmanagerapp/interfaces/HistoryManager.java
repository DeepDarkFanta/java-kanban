package taskmanagerapp.interfaces;
import taskmanagerapp.task.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();
}
