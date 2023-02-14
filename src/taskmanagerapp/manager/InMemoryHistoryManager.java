package taskmanagerapp.manager;

import taskmanagerapp.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager{

    private final LinkedList<Task> history;

    public InMemoryHistoryManager() {
        history = new LinkedList<>();
    }

    @Override
    public void add(Task task){
        if (task != null) {
            if (history.size() >= 10) {
                history.poll();
            }
            history.offer(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
