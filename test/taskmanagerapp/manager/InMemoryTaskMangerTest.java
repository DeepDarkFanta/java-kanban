package taskmanagerapp.manager;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryTaskMangerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public InMemoryTaskManager createManager() {
        return Managers.getDefaultInMemoryTaskManager();
    }


    @Test
    public void createManagerShouldWithoutEmptyMapsAndListsOfTasksTest() {
        InMemoryTaskManager inMemoryTaskManager = createManager();

        assertThat(inMemoryTaskManager.getSubtasksList()).isEmpty();
        assertThat(inMemoryTaskManager.getSubtasksMap()).isEmpty();

        assertThat(inMemoryTaskManager.getTasksList()).isEmpty();
        assertThat(inMemoryTaskManager.getTasksMap()).isEmpty();

        assertThat(inMemoryTaskManager.getEpicTasksMap()).isEmpty();
        assertThat(inMemoryTaskManager.getEpicTasksMap()).isEmpty();
    }
}
