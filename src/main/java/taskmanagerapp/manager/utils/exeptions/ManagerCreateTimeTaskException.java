package taskmanagerapp.manager.utils.exeptions;

import taskmanagerapp.tasks.Task;

public class ManagerCreateTimeTaskException extends RuntimeException{

    public ManagerCreateTimeTaskException(Task task) {
        super("Таска не подходит по времени: " + task.toString());
    }
}
