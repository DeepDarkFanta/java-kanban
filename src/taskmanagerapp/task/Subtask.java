package taskmanagerapp.task;

public class Subtask extends Task{
    private int idOfEpic;

    public void setIdOfEpic(int idOfEpic) {
        this.idOfEpic = idOfEpic;
    }

    public int getIdOfEpic() {
        return idOfEpic;
    }

    public Subtask(String title, String description, Epic epic) {
        super(title, description);
        this.idOfEpic = epic.getId();
    }
}
