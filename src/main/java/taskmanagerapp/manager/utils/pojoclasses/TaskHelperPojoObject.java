package taskmanagerapp.manager.utils.pojoclasses;

import org.mockito.Mockito;
import taskmanagerapp.enums.Status;
import taskmanagerapp.enums.TaskType;
import taskmanagerapp.tasks.Epic;
import taskmanagerapp.tasks.Subtask;
import taskmanagerapp.tasks.Task;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class TaskHelperPojoObject {
    public String title;
    public String description;
    public int id;
    public ZonedDateTime startTime;
    public Duration duration;
    public ArrayList<Integer> idOfSubtasksList = new ArrayList<>();
    public ZonedDateTime endTime;
    public int idOfEpic;
    public Status status;
    public TaskType taskType;

    public Task getTaskObject() {
        Task task = new Task(
                title,
                description,
                duration.toMinutesPart(),
                startTime.toString()
        );
        task.setStatus(status);
        return task;
    }

    public Epic getEpicObject() {
        Epic epic = new Epic(
                title,
                description
        );
        epic.setStatus(status);
        return epic;
    }

    public Subtask getSubtaskObject(Epic epic) {
        Subtask subtask = new Subtask(
                title,
                description,
                epic,
                duration.toMinutesPart(),
                startTime.toString()
        );
        subtask.setIdOfEpic(idOfEpic);
        subtask.setStatus(status);
        return subtask;
    }


}
