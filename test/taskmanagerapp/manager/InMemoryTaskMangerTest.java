package taskmanagerapp.manager;

public class InMemoryTaskMangerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public InMemoryTaskManager createManager() {
        return Managers.getDefault();
    }
}
