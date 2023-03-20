package taskmanagerapp.manager.utils.exeptions;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException{
    public ManagerSaveException() {
        System.out.println("ошибка IOExption");
    }
}
