package taskmanagerapp.manager.utils.exeptions;

public class ManagerIdTaskException extends RuntimeException{
    public ManagerIdTaskException() {
        super("такого айдишника нет");
    }


}
