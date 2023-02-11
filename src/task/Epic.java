package task;

import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> idOfSubtasksList = new ArrayList<>();

    public ArrayList<Integer> getIdOfSubtasksList() {
        return idOfSubtasksList;
    }

    public Epic(String title, String description) {
        super(title, description);
    }

    public void setIdOfSubtasksList(ArrayList<Integer> idOfSubtasksList) {
        this.idOfSubtasksList = idOfSubtasksList;
    }
}

