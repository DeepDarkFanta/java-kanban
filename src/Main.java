import taskmanagerapp.manager.HistoryManager;
import taskmanagerapp.manager.InMemoryHistoryManager;
import taskmanagerapp.manager.TaskManager;
import taskmanagerapp.manager.Managers;
import taskmanagerapp.tasks.Epic;
import taskmanagerapp.tasks.Subtask;
import taskmanagerapp.tasks.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        Task task1 = new Task("убрать комнату", "Нужно убраться до 16:00");
        Task task2 = new Task("убрать комнату", "Нужно убраться до 16:00");
        Epic epic1 = new Epic("Надо сделать утром", "Лучше управиться до 13:00");
        Subtask subtask1 = new Subtask("Убрать кухню", "Лучше управиться до 13:00", epic1);
        Subtask subtask2 = new Subtask("Убрать кухню", "Лучше управиться до 13:00", epic1);
        Subtask subtask3 = new Subtask("Пылесосить", "Лучше управиться до 13:00", epic1);
        Epic epic2 = new Epic("Надо сделать утром", "Лучше управиться до 13:00");
        Task task3 = new Task("убрать комнату", "Нужно убраться до 16:00");

        //Добавление задач
        inMemoryTaskManager.setTask(task1);
        inMemoryTaskManager.setTask(task2);
        inMemoryTaskManager.setTask(epic1);
        inMemoryTaskManager.setTask(subtask1);
        inMemoryTaskManager.setTask(subtask2);
        inMemoryTaskManager.setTask(subtask3);
        inMemoryTaskManager.setTask(epic2);
        inMemoryTaskManager.setTask(task3);

        // получение задач + проверка истории
        inMemoryTaskManager.getByIdTask(0);
        inMemoryTaskManager.getByIdTask(1);
        inMemoryTaskManager.getByIdEpic(2);
        inMemoryTaskManager.getByIdSubtask(3);
        inMemoryTaskManager.getByIdSubtask(4);
        inMemoryTaskManager.getByIdSubtask(5);
        inMemoryTaskManager.getByIdEpic(6);
        inMemoryTaskManager.getHistory();
        inMemoryTaskManager.getByIdSubtask(4);
        inMemoryTaskManager.getHistory();
        inMemoryTaskManager.getByIdTask(0);
        inMemoryTaskManager.getHistory();
        inMemoryTaskManager.getByIdTask(1);
        inMemoryTaskManager.getByIdTask(1);
        inMemoryTaskManager.getByIdTask(1);
        inMemoryTaskManager.getByIdTask(7);
        inMemoryTaskManager.getByIdTask(1);
        inMemoryTaskManager.getHistory();

        //удаление задач + проверка истории на удаление задач
        inMemoryTaskManager.deleteTask(7);
        inMemoryTaskManager.getHistory();
        inMemoryTaskManager.deleteSubtask(4);
        //inMemoryTaskManager.deleteEpic(2);
        inMemoryTaskManager.getHistory();
        inMemoryTaskManager.deleteEpic(6);
        inMemoryTaskManager.getHistory();
        inMemoryTaskManager.deleteAllTasks(inMemoryTaskManager.getTasksList());
        System.out.println("-----");
        inMemoryTaskManager.getHistory();
        System.out.println("-----");
        inMemoryTaskManager.deleteAllSubtasks(inMemoryTaskManager.getSubtasksList());
        inMemoryTaskManager.getHistory();
        inMemoryTaskManager.deleteAllEpics(inMemoryTaskManager.getEpicTasksList());
        inMemoryTaskManager.getHistory();
    }
}
