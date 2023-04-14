package taskmanagerapp.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagerapp.enums.Status;
import taskmanagerapp.manager.utils.exeptions.ManagerCreateTimeTaskException;
import taskmanagerapp.manager.utils.exeptions.ManagerIdTaskException;
import taskmanagerapp.server.KVServer;
import taskmanagerapp.tasks.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager>{

    private T manager;
    private Task task;
    private Epic epic;
    private Subtask subtask1;
    protected Epic epic1;
    private Subtask subtask2;
    protected Subtask subtask;
    abstract T createManager();
    protected KVServer kvServer;

    @AfterEach
    public void stopServer() {
        kvServer.stop();
    }

    @BeforeEach
    public void setDefaultBehavior() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        try (FileWriter fileWriter = new FileWriter("test/resources/tasksAndHistoryTest.csv")){
            fileWriter.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter fileWriter = new FileWriter("src/main/resources/tasksAndHistory.csv")){
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
    public void getEpicTasksListShouldEpicsOnMapEqualInEpicsListTest() {
        List<Epic> epicsTestList = new ArrayList<>(manager.getEpicTasksMap().values());
        assertThat(epicsTestList).containsExactlyInAnyOrderElementsOf(manager.getEpicTasksList());
    }

    @Test
    public void getEpicsTaskListWhenNoEpicsTest() {
        manager.deleteAllEpics(manager.getEpicTasksList());
        assertThat(Collections.EMPTY_LIST).isEqualTo(manager.getEpicTasksList());
    }

    @Test
    public void getTasksListShouldTasksOnMapEqualInTasksListTest() {
      List<Task> taskTestList = new ArrayList<>(manager.getTasksMap().values());
      assertThat(taskTestList).containsExactlyInAnyOrderElementsOf(manager.getTasksList());
    }

    @Test
    public void getTasksListWhenNoTasksTest() {
        manager.deleteAllTasks(manager.getTasksList());
        assertThat(Collections.EMPTY_LIST).isEqualTo(manager.getTasksList());
    }

    @Test
    public void getSubtasksListShouldTasksOnSubtaskEqualInSubtasksListTest() {
        List<Subtask> subtaskTestList = new ArrayList<>(manager.getSubtasksMap().values());
        assertThat(subtaskTestList).containsExactlyInAnyOrderElementsOf(manager.getSubtasksList());
    }

    @Test
    public void getSubtasksListWhenNoTasksTest() {
        manager.deleteAllTasks(manager.getTasksList());
        assertThat(Collections.EMPTY_LIST).isEqualTo(manager.getTasksList());
    }

    @Test
    public void getByIdTaskTest() {
        int taskIdTest = task.getId();
        assertThat(task).isEqualTo(manager.getByIdTask(taskIdTest));
    }

    @Test
    public void getByIdTaskWhenNoneExistentIdTest() {
        int idTest = -1;
        assertThatThrownBy(() -> manager.getByIdTask(idTest))
                .isInstanceOf(ManagerIdTaskException.class);
    }

    @Test
    public void getByIdEpicTest() {
       int epicIdTest = epic.getId();
       assertThat(epic).isEqualTo(manager.getByIdEpic(epicIdTest));
    }

    @Test
    public void getByIdEpicWhenNoneExistentId() {
        int idTest = -1;
        assertThatThrownBy(() -> manager.getByIdEpic(idTest))
                .isInstanceOf(ManagerIdTaskException.class);
    }

    @Test
    public void getByIdSubtaskTest() {
       int idSubtaskTest = subtask1.getId();
       assertThat(subtask1).isEqualTo(manager.getByIdSubtask(idSubtaskTest));
    }

    @Test
    public void getByIdSubtaskWhenNoneExistentIdTest() {
        int idTest = -1;
        assertThatThrownBy(() -> manager.getByIdSubtask(idTest))
                .isInstanceOf(ManagerIdTaskException.class);
    }

    @Test
    public void getAllEpicSubtasksListTest() {
        int idEpicTest = epic.getId();
        List<Subtask> subtasksList = manager.getAllEpicSubtasks(idEpicTest);
        assertThat(subtasksList).containsAnyElementsOf(manager.getAllEpicSubtasks(idEpicTest));
    }

    @Test
    public void getAllEpicsSubtasksWhenNoneExistentIdTest() {
        int idEpicTest = -1;
        assertThatThrownBy(() -> manager.getAllEpicSubtasks(idEpicTest))
                .isInstanceOf(ManagerIdTaskException.class);
    }

    @Test
    public void deleteTaskTest() {
        int idTaskTest = task.getId();
        manager.deleteTask(idTaskTest);
        assertThat(manager.getTasksMap().containsValue(task)).isFalse();
    }

    @Test
    public void deleteTaskWhenNoneExistentIdTest() {
        int idTaskTest = -1;
        assertThrows(
                ManagerIdTaskException.class,
                () -> manager.deleteTask(idTaskTest)
        );
    }

    @Test
    public void deleteEpicTest() {
        int idEpicTest = epic.getId();
        manager.deleteEpic(idEpicTest);
        assertFalse(manager.getEpicTasksMap().containsValue(epic));
    }

    @Test
    public void deleteEpicShouldThrowExceptionWhenIdIsNotCorrect() {
        Epic epicTest = new Epic("", "");
        int incorrectId = 320000000;
        epicTest.setId(incorrectId);
        manager.setTask(epic);

    }

    @Test
    public void deleteEpicTestWhenExistentIdTest() {
        int idEpicTest = -1;
        assertThrows(
                ManagerIdTaskException.class,
                () -> manager.deleteEpic(idEpicTest)
        );
    }

    @Test
    public void deleteSubtaskTest() {
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
    public void deleteAllTasksInManagerFromMapTest() {
        assertFalse(manager.getTasksList().isEmpty());
        manager.deleteAllTasks(manager.getTasksList());
        assertTrue(manager.getTasksList().isEmpty());
    }

    @Test
    public void deleteAllEpicsInManagerFromMapTest() {
        assertFalse(manager.getEpicTasksMap().isEmpty());
        manager.deleteAllEpics(manager.getEpicTasksList());
        assertTrue(manager.getEpicTasksMap().isEmpty());
    }

    @Test
    public void deleteAllSubtasksInManagerFromMapTest() {
        assertFalse(manager.getSubtasksMap().isEmpty());
        manager.deleteAllSubtasks(manager.getSubtasksList());
        assertTrue(manager.getSubtasksMap().isEmpty());
    }

    @Test
    public void updateTaskStatusTest() {
        manager.updateTask(task, Status.IN_PROGRESS);
        assertThat(task.getStatus()).isEqualTo(Status.IN_PROGRESS);

        manager.updateTask(task, Status.DONE);
        assertThat(task.getStatus()).isEqualTo(Status.DONE);

        manager.updateTask(task, Status.NEW);
        assertThat(task.getStatus()).isEqualTo(Status.NEW);
    }

    @Test
    public void updateEpicStatusTest() {
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
    public void updateSubtaskStatusTest() {
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
    public void setTaskShouldAddTasksOnManagerTest() {
        Task taskTest = manager.getByIdTask(task.getId());
        assertThat(manager.getTasksMap().containsValue(taskTest)).isTrue();

        Epic epicTest = manager.getByIdEpic(epic.getId());
        assertThat(manager.getEpicTasksMap().containsValue(epicTest)).isTrue();

        Subtask subtaskTest = manager.getByIdSubtask((subtask1.getId()));
        assertThat(manager.getSubtasksMap().containsValue(subtaskTest)).isTrue();
    }

    @Test
    public void setTaskShouldThrowExceptionWhenIdIsNotCorrectTest() {
        Task task = new Task("", "", 123, "12.02.1999 12:12:12");
        task.setId(-12);
        assertThatThrownBy(() -> manager.setTask(task))
                .isInstanceOf(ManagerIdTaskException.class);

        Epic epic = new Epic("", "");
        epic.setId(-10);
        assertThatThrownBy(() -> manager.setTask(epic))
                .isInstanceOf(ManagerIdTaskException.class);

        Subtask subtask = new Subtask("", "", epic, 123, "12.02.1999 12:12:12");
        subtask.setId(-9);
        assertThatThrownBy(() -> manager.setTask(subtask))
                .isInstanceOf(ManagerIdTaskException.class);
    }

    @Test
    public void getHistoryListOfTasksTest() {
        manager.getByIdEpic(epic.getId());
        manager.getByIdTask(task.getId());
        manager.getByIdSubtask(subtask2.getId());
        assertThat(List.of(epic, task, subtask2)).isEqualTo(manager.getHistory());
    }

    @Test
    public void getAllTasksShouldGetAllEpicsTasksAndSubtaskInListTest() {
        List<Task> tasksListTest = Stream.of(manager.getTasksList(), manager.getEpicTasksList(), manager.getSubtasksList())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<Task> tasksList = manager.getAllTasks();
        assertThat(tasksListTest).containsExactlyInAnyOrderElementsOf(tasksList);
    }

    @Test
    public void timeTaskThrowCreateTimeExceptionTest() {
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
    public void timeTaskEpicDurationAndStartTimeTest() {
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
    public void getPrioritizedTasksShouldGetListWhenTasksSortedByTimeNotCrossing() {
        List<Task> tasksTimeList = manager.getPrioritizedTasks();
        for (int i = 0; i < tasksTimeList.size() - 1; i++) {
            assertTrue(tasksTimeList.get(i).getEndTime()
                    .isBefore(tasksTimeList.get(i + 1).getStartTime()));
        }

        for (int i = 0; i < tasksTimeList.size() - 1; i++) {
            for (int j = i + 1; j < tasksTimeList.size() - 1; j++) {
                assertThat(tasksTimeList.get(i).getEndTime()
                        .isBefore(tasksTimeList.get(j).getStartTime())).isTrue();
            }
        }
    }

    @Test
    public void shouldReturnEmptySubtasksListInEpic() {
        Epic epic1 = new Epic("","");
        manager.setTask(epic1);
        assertThat(epic1.getIdOfSubtasksList().isEmpty()).isTrue();
        for (Subtask subtask : manager.getSubtasksList()) {
            assertThat(epic1.getId()).isNotEqualTo(subtask.getIdOfEpic());
        }
    }

    @Test
    public void shouldStatusSetNewForSubtasksIfEpicStatusIsNew() {
        assertThat(epic.getId()).isEqualTo(subtask1.getIdOfEpic());
        //при epic = new -> subtask = new
        assertThat(epic.getStatus())
                .as("epic: "+ epic.getStatus() +  ", subtask: " + subtask1.getStatus())
                .isEqualTo(Status.NEW);
    }

    @Test
    public void shouldStatusEpicDoneWhenStatusSubtasksIsDone() {
        manager.updateSubtask(subtask1, Status.DONE);
        manager.updateSubtask(subtask2, Status.DONE);
        assertThat(epic.getStatus()).isEqualTo(Status.DONE);
    }

    @Test
    public void shouldStatusEpicInProgressWhenSubtasksNewAndDone() {
        manager.updateSubtask(subtask1, Status.NEW);
        manager.updateSubtask(subtask2, Status.DONE);
        assertThat(epic.getStatus()).isEqualTo(Status.IN_PROGRESS);
    }

    @Test
    public void shouldEpicStatusInProgressWhenSubtasksInProgress() {
        manager.updateSubtask(subtask1, Status.IN_PROGRESS);
        manager.updateSubtask(subtask2, Status.IN_PROGRESS);
        assertThat(epic.getStatus()).isEqualTo(Status.IN_PROGRESS);
    }
}
