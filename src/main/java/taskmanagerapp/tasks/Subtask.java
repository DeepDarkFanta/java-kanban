package taskmanagerapp.tasks;

import taskmanagerapp.enums.TaskType;

public class Subtask extends Task{
    private int idOfEpic;

    public void setIdOfEpic(int idOfEpic) {
        this.idOfEpic = idOfEpic;
    }

    public int getIdOfEpic() {
        return idOfEpic;
    }

    public Subtask(String title, String description, Epic epic, int duration, String startTime) {
        super(title, description, duration, startTime);
        this.idOfEpic = epic.getId();
        setTaskType(TaskType.SUBTASK);
    }
}
