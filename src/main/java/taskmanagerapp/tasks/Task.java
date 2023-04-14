package taskmanagerapp.tasks;

import taskmanagerapp.enums.Status;
import taskmanagerapp.enums.TaskType;
import taskmanagerapp.manager.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private String title;
    private String description;
    private static int counter;
    private int id;
    private Status status;
    private TaskType taskType;
    private ZonedDateTime startTime;
    private Duration duration;

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public Task(String title, String description, int duration, String startTime) {
        this.id = InMemoryTaskManager.getId();
        this.duration = Duration.ofMinutes(duration);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        LocalDateTime localDateTime = LocalDateTime.parse(startTime, dateTimeFormatter);
        this.startTime = ZonedDateTime.of(localDateTime, ZoneId.of(ZoneId.systemDefault().getId()));
        this.title = title;
        this.description = description;
        status = Status.NEW;
        setTaskType(TaskType.TASK);
    }

    public int getId() {
        return id;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }


    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public ZonedDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(this.getClass().getSimpleName() + "={");
        sb.append("title='").append(title).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", id=").append(id);
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (id != task.id) return false;
        if (!Objects.equals(title, task.title)) return false;
        if (!Objects.equals(description, task.description)) return false;
        return status == task.status;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + id;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
