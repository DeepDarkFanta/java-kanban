import manager.Manager;
import task.Epic;
import task.Subtask;
import task.Task;


public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();

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
            manager.setTask(task1);
            manager.setTask(task2);
            manager.setTask(epic1);
            manager.setTask(subtask1);
            manager.setTask(subtask2);
            manager.setTask(epic2);
            manager.setTask(subtask21);
            manager.setTask(subtask3);
        }

        //Получение списка всех задач
        {
            manager.getTasksList();
            manager.getEpicTasksList();
            manager.getSubtasksList();
        }

        //Получение по идентификатору
        {
            manager.getByIdTask(0);
            manager.getByIdEpic(0);
            manager.getByIdSubtask(0);
        }

        //обновление статусов
        manager.updateTask(task1, Task.Status.DONE);
        manager.updateEpic(epic1, Task.Status.DONE);
        manager.updateEpic(epic2, Task.Status.DONE);
        manager.updateSubtask(subtask1, Task.Status.NEW);
        manager.updateSubtask(subtask1, Task.Status.DONE);
        manager.updateEpic(epic1, Task.Status.NEW);
        manager.updateSubtask(subtask1, Task.Status.DONE);
        manager.updateSubtask(subtask2, Task.Status.DONE);


        //Получение списка всех подзадач определённого эпика
        manager.updateSubtask(subtask1, Task.Status.NEW);

        //удаление по айди
        manager.deleteById(4);
    }
}