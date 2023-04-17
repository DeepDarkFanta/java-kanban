package taskmanagerapp.manager.utils.exeptions;

public class ResponseStatusException extends Exception{
    public ResponseStatusException() {
        super("Статус код не 200 с сервера");
    }

}
