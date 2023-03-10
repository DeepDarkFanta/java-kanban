package taskmanagerapp.manager;

import taskmanagerapp.tasks.Task;
import java.util.ArrayList;

public interface HistoryManager {

    ArrayList<Task> getHistory();

    void removeNode(InMemoryHistoryManager.Node node);

    ArrayList<Task> getTasks();

    void linkLast(Task task);

    void taskDeleteInHistory(Task task);
}
