package task;

import enums.Status;
import inMemoryTaskManager.InMemoryTaskManager;
import java.util.Objects;

public class Task {
    private String title;
    private String description;
    private int id;
    private Status status;

/*
    public enum Status {
        NEW,
        IN_PROGRESS,
        DONE
    }
*/

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        status = Status.NEW;

        this.id = InMemoryTaskManager.getIdTask();
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

    public int getId() {
        return id;
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
