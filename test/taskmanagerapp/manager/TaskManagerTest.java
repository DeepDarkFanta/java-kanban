package taskmanagerapp.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagerapp.enums.Status;
import taskmanagerapp.manager.utils.exeptions.ManagerCreateTimeTaskException;
import taskmanagerapp.manager.utils.exeptions.ManagerIdTaskException;
import taskmanagerapp.tasks.Epic;
import taskmanagerapp.tasks.Subtask;
import taskmanagerapp.tasks.Task;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


abstract class TaskManagerTest<T extends TaskManager>{

    private T manager;
    abstract T createManager();
    Task task;
    Epic epic;
    Subtask subtask1;
    Epic epic1;
    Subtask subtask2;
    Subtask subtask;

    @BeforeEach
    public void setDefaultBehavior() {
        try (FileWriter fileWriter = new FileWriter("test/resources/tasksAndHistoryTest.csv")){
            fileWriter.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        manager = createManager();
        task = new Task("1", "", 60, "01.12.2023 13:00:00");
        epic = new Epic("2", "");
        subtask1 = new Subtask("3", "", epic, 60, "12.02.2023 18:00:00");
        subtask2 = new Subtask("3", "", epic, 60, "10.02.2023 18:00:00");
        subtask = new Subtask("3", "", epic, 60, "15.02.2023 18:00:00");
        epic1 = new Epic("4", "");
        manager.setTask(epic);
        manager.setTask(task);
        manager.setTask(subtask1);
        manager.setTask(subtask2);
        manager.setTask(epic1);
    }

    @Test
    public void getEpicTasksList() {
        List<Epic> epicsTestList = new ArrayList<>(manager.getEpicTasksMap().values());
        assertThat(epicsTestList).containsExactlyInAnyOrderElementsOf(manager.getEpicTasksList());
    }

    @Test
    public void getEpicsTaskListWhenNoEpics() {
        manager.deleteAllEpics(manager.getEpicTasksList());
        assertThat(Collections.EMPTY_LIST).isEqualTo(manager.getEpicTasksList());
    }

    @Test
    public void getTasksList() {
      List<Task> taskTestList = new ArrayList<>(manager.getTasksMap().values());
      assertThat(taskTestList).containsExactlyInAnyOrderElementsOf(manager.getTasksList());
    }

    @Test
    public void getTasksListWhenNoTasks() {
        manager.deleteAllTasks(manager.getTasksList());
        assertThat(Collections.EMPTY_LIST).isEqualTo(manager.getTasksList());
    }

    @Test
    public void getSubtasksList() {
        List<Subtask> subtaskTestList = new ArrayList<>(manager.getSubtasksMap().values());
        assertThat(subtaskTestList).containsExactlyInAnyOrderElementsOf(manager.getSubtasksList());
    }

    @Test
    public void getSubtasksListWhenNoTasks() {
        manager.deleteAllTasks(manager.getTasksList());
        assertThat(Collections.EMPTY_LIST).isEqualTo(manager.getTasksList());
    }

    @Test
    public void getByIdTask() {
        int taskIdTest = task.getId();
        assertThat(task).isEqualTo(manager.getByIdTask(taskIdTest));
    }

    @Test
    public void getByIdTaskWhenNoneExistentId() {
        int idTest = -1;
        assertThatThrownBy(() -> manager.getByIdTask(idTest))
                .isInstanceOf(ManagerIdTaskException.class);
    }

    @Test
    public void getByIdEpic() {
       int epicIdTest = epic.getId();
        assertEquals(epic, manager.getByIdEpic(epicIdTest));
    }

    @Test
    public void getByIdEpicWhenNoneExistentId() {
        int idTest = -1;
        assertThrows(
                ManagerIdTaskException.class,
                () -> manager.getByIdEpic(idTest));
    }

    @Test
    public void getByIdSubtask() {
       int idSubtaskTest = subtask1.getId();
       assertEquals(subtask1, manager.getByIdSubtask(idSubtaskTest));
    }

    @Test
    public void getByIdSubtaskWhenNoneExistentId() {
        int idTest = -1;
        assertThrows(
                ManagerIdTaskException.class,
                () -> manager.getByIdSubtask(idTest)
        );
    }

    @Test
    public void getAllEpicSubtasks() {
        int idEpicTest = epic.getId();
        List<Subtask> subtasksList = manager.getAllEpicSubtasks(idEpicTest);
        assertArrayEquals(subtasksList.toArray(), manager.getAllEpicSubtasks(idEpicTest).toArray());
    }

    @Test
    public void getAllEpicsSubtasksWhenNoneExistentId() {
        int idEpicTest = -1;
        assertThrows(
                ManagerIdTaskException.class,
                () -> manager.getAllEpicSubtasks(idEpicTest)
        );
    }

    @Test
    public void deleteTask() {
        int idTaskTest = task.getId();
        manager.deleteTask(idTaskTest);
        assertFalse(manager.getTasksMap().containsValue(task));
    }

    @Test
    public void deleteTaskWhenNoneExistentId() {
        int idTaskTest = -1;
        assertThrows(
                ManagerIdTaskException.class,
                () -> manager.deleteTask(idTaskTest)
        );
    }

    @Test
    public void deleteEpic() {
        int idEpicTest = epic.getId();
        manager.deleteEpic(idEpicTest);
        assertFalse(manager.getEpicTasksMap().containsValue(epic));
    }

    @Test
    public void deleteEpicTestWhenExistentId() {
        int idEpicTest = -1;
        assertThrows(
                ManagerIdTaskException.class,
                () -> manager.deleteEpic(idEpicTest)
        );
    }

    @Test
    public void deleteSubtask() {
        int subtaskIdTest = subtask1.getId();
        manager.deleteSubtask(subtaskIdTest);
        assertFalse(manager.getSubtasksMap().containsValue(subtask1));
    }

    @Test
    public void deleteSubtaskWhenNoneExistentId() {
        int idSubtaskTest = -1;
        assertThrows(
                ManagerIdTaskException.class,
                () -> manager.deleteSubtask(idSubtaskTest)
        );
    }

    @Test
    public void deleteAllTasks() {
        assertFalse(manager.getTasksList().isEmpty());
        manager.deleteAllTasks(manager.getTasksList());
        assertTrue(manager.getTasksList().isEmpty());
    }

    @Test
    public void deleteAllEpics() {
        assertFalse(manager.getEpicTasksMap().isEmpty());
        manager.deleteAllEpics(manager.getEpicTasksList());
        assertTrue(manager.getEpicTasksMap().isEmpty());
    }

    @Test
    public void deleteAllSubtasks() {
        assertFalse(manager.getSubtasksMap().isEmpty());
        manager.deleteAllSubtasks(manager.getSubtasksList());
        assertTrue(manager.getSubtasksMap().isEmpty());
    }

    @Test
    public void updateTask() {
        assertFalse(manager.getSubtasksMap().isEmpty());
        manager.deleteAllTasks(manager.getTasksList());
        assertTrue(manager.getTasksMap().isEmpty());
    }

    @Test
    public void updateEpic() {
        //assertj
        manager.updateEpic(epic, Status.DONE);
        assertEquals(Status.DONE, epic.getStatus());
        assertThat(manager.getAllEpicSubtasks(epic.getId()))
                .extracting("status")
                .contains(Status.DONE);
        //Junit
        manager.updateEpic(epic, Status.NEW);
        assertEquals(Status.NEW, epic.getStatus());
        manager.getAllEpicSubtasks(epic.getId())
                .forEach(subtask -> assertEquals(subtask.getStatus(), Status.NEW));
    }

    @Test
    public void updateSubtask() {
        // sub1 - new -> done
        // sub2 - new
        // epic - new ? in_progress
        manager.updateSubtask(subtask1, Status.DONE);
        assertEquals(Status.DONE, subtask1.getStatus());
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
        // sub1 - done
        // sub2 - new -> done
        // epic - in_progress ? done
        manager.updateSubtask(subtask2, Status.DONE);
        assertEquals(Status.DONE, subtask2.getStatus());
        assertEquals(Status.DONE, epic.getStatus());
        // sub1 - done -> new
        // sub2 - done -> new
        // epic - done ? new
        manager.updateSubtask(subtask1, Status.NEW);
        manager.updateSubtask(subtask2, Status.NEW);
        assertEquals(Status.NEW, subtask2.getStatus());
        assertEquals(Status.NEW, subtask1.getStatus());
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void setTask() {
        Task taskTest = manager.getByIdTask(task.getId());
        assertTrue(manager.getTasksMap().containsValue(taskTest));

        Epic epicTest = manager.getByIdEpic(epic.getId());
        assertTrue(manager.getEpicTasksMap().containsValue(epicTest));

        Subtask subtaskTest = manager.getByIdSubtask((subtask1.getId()));
        assertTrue(manager.getSubtasksMap().containsValue(subtaskTest));
    }

    @Test
    public void getHistory() {
        manager.getByIdEpic(epic.getId());
        manager.getByIdTask(task.getId());
        manager.getByIdSubtask(subtask2.getId());
        assertArrayEquals(List.of(epic, task, subtask2).toArray(),
                manager.getHistory().toArray());
    }

    @Test
    public void getAllTasks() {
        List<Task> tasksListTest = Stream.of(manager.getTasksList(), manager.getEpicTasksList(), manager.getSubtasksList())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<Task> tasksList = manager.getAllTasks();
        assertThat(tasksListTest).containsExactlyInAnyOrderElementsOf(tasksList);
    }

    @Test
    public void timeTaskThrowCreateTimeException() {
        Task taskTest1 = new Task("", "", 213,"12.02.2023 18:00:00");
        assertThrows(
                ManagerCreateTimeTaskException.class,
                () -> manager.setTask(taskTest1)
        );

        Subtask subtaskTest1 = new Subtask("", "", epic, 123,"10.02.2023 18:00:00");
        assertThrows(
                ManagerCreateTimeTaskException.class,
                () -> manager.setTask(subtaskTest1)
        );

        //проверка на исключение при пересечении
        Subtask subtaskTest2 = new Subtask("", "", epic, 123,"10.02.2023 17:00:00");
        assertThrows(
                ManagerCreateTimeTaskException.class,
                () -> manager.setTask(subtaskTest2)
        );

        Task taskTest2 = new Task("", "", 213,"10.02.2023 17:00:00");
        assertThrows(
                ManagerCreateTimeTaskException.class,
                () -> manager.setTask(taskTest2)
        );
    }

    @Test
    public void timeTaskEpicDurationAndStartTime() {
        Epic newEpic = new Epic("", "");
        manager.setTask(newEpic);

        Subtask subtask = new Subtask("","", newEpic, 60, "10.02.2023 17:00:00");
        manager.setTask(subtask);

        Subtask subtask1 = new Subtask("","", newEpic, 60, "09.02.2023 17:00:00");
        manager.setTask(subtask1);

        assertEquals(120, newEpic.getDuration().toMinutes());

        assertEquals(subtask1.getStartTime(), newEpic.getStartTime());

        assertEquals(subtask.getStartTime().plusMinutes(60), newEpic.getEndTime());

        assertEquals(subtask.getStartTime().plusMinutes(60), subtask.getEndTime());
    }

    @Test
    public void getPrioritizedTasks() {
        List<Task> tasksTimeList = manager.getPrioritizedTasks();
        for (int i = 0; i < tasksTimeList.size() - 1; i++) {
            assertTrue(tasksTimeList.get(i).getEndTime().isBefore(tasksTimeList.get(i + 1).getStartTime()));
        }
    }
}
