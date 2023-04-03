package taskmanagerapp.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagerapp.enums.Status;
import taskmanagerapp.manager.InMemoryTaskManager;
import taskmanagerapp.tasks.Epic;
import taskmanagerapp.tasks.Subtask;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class EpicTest {

    private Epic epic;
    private InMemoryTaskManager inMemoryTaskManager;
    private Subtask subtask1;
    private  Subtask subtask2;

    @BeforeEach
    public  void createEpicForAllTests() {
        epic = new Epic("", "");
        inMemoryTaskManager = new InMemoryTaskManager();
        subtask1 = new Subtask("", "", epic, 123, "18.12.2022 20:20:20");
        subtask2 = new Subtask("", "", epic, 123, "19.12.2022 14:20:20");
        inMemoryTaskManager.setTask(epic);
        inMemoryTaskManager.setTask(subtask1);
        inMemoryTaskManager.setTask(subtask2);
    }

    @Test
    public void shouldReturnEmptySubtasksListInEpic() {
        Epic epic1 = new Epic("","");
        inMemoryTaskManager.setTask(epic1);
        assertThat(epic1.getIdOfSubtasksList().isEmpty()).isTrue();
        for (Subtask subtask : inMemoryTaskManager.getSubtasksList()) {
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
        inMemoryTaskManager.updateSubtask(subtask1, Status.DONE);
        inMemoryTaskManager.updateSubtask(subtask2, Status.DONE);
        assertThat(epic.getStatus()).isEqualTo(Status.DONE);
    }

    @Test
    public void shouldStatusEpicInProgressWhenSubtasksNewAndDone() {
        inMemoryTaskManager.updateSubtask(subtask1, Status.NEW);
        inMemoryTaskManager.updateSubtask(subtask2, Status.DONE);
        assertThat(epic.getStatus()).isEqualTo(Status.IN_PROGRESS);
    }

    @Test
    public void shouldEpicStatusInProgressWhenSubtasksInProgress() {
        inMemoryTaskManager.updateSubtask(subtask1, Status.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask2, Status.IN_PROGRESS);
        assertThat(epic.getStatus()).isEqualTo(Status.IN_PROGRESS);
    }
}
