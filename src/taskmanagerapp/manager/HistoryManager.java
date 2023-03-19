package taskmanagerapp.manager;

import taskmanagerapp.tasks.Task;
import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {

    List<Task> getHistory();

    void removeNode(InMemoryHistoryManager.Node node);

    ArrayList<Task> getTasks();

    void linkLast(Task task);

    void taskDeleteInHistory(Task task);
}
