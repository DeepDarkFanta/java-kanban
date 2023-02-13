import Interfaces.TaskManager;
import enums.Status;
import inMemoryTaskManager.Managers;
import task.Epic;
import task.Subtask;
import task.Task;


public class Main {
    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        Task task1 = new Task("убрать комнату", "Нужно убраться до 16:00");
        Task task2 = new Task("убрать комнату", "Нужно убраться до 16:00");
        Epic epic1 = new Epic("Надо сделать утром", "Лучше управиться до 13:00");
        Subtask subtask1 = new Subtask("Убрать кухню", "Лучше управиться до 13:00", epic1);
        Subtask subtask3 = new Subtask("Убрать кухню", "Лучше управиться до 13:00", epic1);
        Subtask subtask2 = new Subtask("Пылесосить", "Лучше управиться до 13:00", epic1);
        Epic epic2 = new Epic("Надо сделать утром", "Лучше управиться до 13:00");
        Subtask subtask21 = new Subtask("Пылесосить", "Лучше управиться до 13:00", epic2);


        //Передача эпиков/задач/подзадач в манагера (Возможность хранить задачи всех типов)
        //Содание объектов задач/эпиков/подзадач
        {
            inMemoryTaskManager.setTask(task1);
            inMemoryTaskManager.setTask(task2);
            inMemoryTaskManager.setTask(epic1);
            inMemoryTaskManager.setTask(subtask1);
            inMemoryTaskManager.setTask(subtask2);
            inMemoryTaskManager.setTask(epic2);
            inMemoryTaskManager.setTask(subtask21);
            inMemoryTaskManager.setTask(subtask3);
        }
        //тест истории
        {
            inMemoryTaskManager.getByIdTask(0);
            inMemoryTaskManager.getByIdEpic(2);
            inMemoryTaskManager.getByIdSubtask(4);
            inMemoryTaskManager.getByIdSubtask(4);
            inMemoryTaskManager.getByIdSubtask(4);
            inMemoryTaskManager.getByIdSubtask(4);
            inMemoryTaskManager.getByIdSubtask(4);
            inMemoryTaskManager.getByIdSubtask(4);
            inMemoryTaskManager.getByIdSubtask(4);
            inMemoryTaskManager.getByIdSubtask(4);
            inMemoryTaskManager.getByIdSubtask(4);
            inMemoryTaskManager.getByIdSubtask(4);
            inMemoryTaskManager.getByIdSubtask(4);
            inMemoryTaskManager.getByIdSubtask(4);
            inMemoryTaskManager.getByIdSubtask(4);
            inMemoryTaskManager.getByIdSubtask(4);
            inMemoryTaskManager.getByIdEpic(2);
            System.out.println(inMemoryTaskManager.getHistory());
        }
    }
}
