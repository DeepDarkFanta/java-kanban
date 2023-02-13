package inMemoryTaskManager;

import Interfaces.HistoryManager;
import task.Task;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private Deque<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayDeque<>();
    }

    @Override
    public void add(Task task){
        if (task != null) {
            if (history.size() == 10) {
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
