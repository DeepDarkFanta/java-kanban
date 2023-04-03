package taskmanagerapp.manager;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagerapp.tasks.Epic;
import taskmanagerapp.tasks.Subtask;
import taskmanagerapp.tasks.Task;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    Epic epic;
    Task task;
    Subtask subtask;
    HistoryManager inMemoryHistoryManager;
    @BeforeEach
    public void createHistoryAndTasks() {
        inMemoryHistoryManager = Managers.getDefaultHistory();
        epic = new Epic("1", "2");
        subtask = new Subtask("1", "2", epic, 123, "20.12.2023 10:10:00");
        task = new Task("", "", 123,"11.11.2023 20:20:20");
        inMemoryHistoryManager.linkLast(epic);
        inMemoryHistoryManager.linkLast(task);
        inMemoryHistoryManager.linkLast(subtask);
    }

    @Test
    public void getHistory() {
        assertArrayEquals(List.of(epic, task, subtask).toArray(),
                inMemoryHistoryManager.getHistory().toArray());
    }

    @Test
    public void getHistoryEmpty() {
        inMemoryHistoryManager.taskDeleteInHistory(epic);
        inMemoryHistoryManager.taskDeleteInHistory(task);
        inMemoryHistoryManager.taskDeleteInHistory(subtask);
        assertTrue(inMemoryHistoryManager.getHistory().isEmpty());
    }

    @Test
    public void linkLastAndAddIdentical() {
        assertArrayEquals(List.of(epic, task, subtask).toArray(),
                inMemoryHistoryManager.getHistory().toArray());

        inMemoryHistoryManager.linkLast(epic);
        assertArrayEquals(List.of(task, subtask, epic).toArray(),
                inMemoryHistoryManager.getHistory().toArray());

        inMemoryHistoryManager.linkLast(subtask);
        assertArrayEquals(List.of(task, epic, subtask).toArray(),
                inMemoryHistoryManager.getHistory().toArray());
    }

    @Test
    public void taskDeleteInHistoryOnStart() {
        inMemoryHistoryManager.taskDeleteInHistory(epic);
        assertArrayEquals(List.of(task, subtask).toArray(),
                inMemoryHistoryManager.getHistory().toArray());
    }

    @Test
    public void taskDeleteInHistoryInEnd() {
        inMemoryHistoryManager.taskDeleteInHistory(subtask);
        assertArrayEquals(Arrays.array(epic, task), inMemoryHistoryManager.getHistory().toArray());
    }

    @Test
    public void taskDeleteInHistoryBetween() {
        inMemoryHistoryManager.taskDeleteInHistory(task);
        assertArrayEquals(Arrays.array(epic, subtask), inMemoryHistoryManager.getHistory().toArray());
    }
}