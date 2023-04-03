package taskmanagerapp.tasks;

import taskmanagerapp.enums.TaskType;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> idOfSubtasksList = new ArrayList<>();
    ZonedDateTime endTime;
    public ArrayList<Integer> getIdOfSubtasksList() {
        return idOfSubtasksList;
    }

    public Epic(String title, String description) {
        super(title, description, 0, "20.12.1984 04:11:11");
        setTaskType(TaskType.EPIC);
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setIdOfSubtasksList(ArrayList<Integer> idOfSubtasksList) {
        this.idOfSubtasksList = idOfSubtasksList;
    }
}
