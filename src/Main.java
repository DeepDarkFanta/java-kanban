import manager.Manager;
import task.Epic;
import task.Subtask;
import task.Task;


public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task1 = new Task("убрать комнату", "Нужно убраться до 16:00", manager.getIdTask());
        int idTask1 = task1.getId();
        Task task2 = new Task("убрать комнату", "Нужно убраться до 16:00", manager.getIdTask());
        int idTask2 = task2.getId();
        Epic epic1 = new Epic("Надо сделать утром", "Лучше управиться до 13:00", manager.getIdTask());
        int idEpic1 = epic1.getId();
        Subtask subtask1 = new Subtask("Убрать кухню", "Лучше управиться до 13:00", epic1.getId(), manager.getIdTask());
        int idSubtask1 = subtask1.getId();
        int idSubEpic1 = subtask1.getIdOfEpic();

        Subtask subtask2 = new Subtask("Пылесосить", "Лучше управиться до 13:00", epic1.getId(), manager.getIdTask());
        int idSubtask2 = subtask2.getId();

        Epic epic2 = new Epic("Надо сделать утром", "Лучше управиться до 13:00", manager.getIdTask());
        int idEpic2 = epic2.getId();
        Subtask subtask21 = new Subtask("Пылесосить", "Лучше управиться до 13:00", epic2.getId(), manager.getIdTask());
        int idSubtask21 = subtask21.getId();

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
        }

        //Получение списка всех задач
        {
            manager.getTasksList();
            manager.getEpicTasksList();
            manager.getSubtasksList();
        }


        //Удаление всех задач
      /*  {
            manager.deleteAllSubtask(manager.getSubtasksList());
            manager.deleteAllEpics(manager.getEpicTasksList());
            manager.deleteAllTasks(manager.getTasksList());
        }*/

        //Получение по идентификатору
        {
            manager.getByIdTask(0);
            manager.getByIdEpic(0);
            manager.getByIdSubtask(0);
        }

        //обновление статусов
        task1 = new Task("убрать комнату", "Нужно убраться до 16:00", idTask1);
        manager.updateTask(task1, Task.Status.DONE);
        epic1 = new Epic("Надо сделать утром", "Лучше управиться до 13:00", idEpic1);
        manager.updateEpic(epic1, Task.Status.DONE);
        epic2 = new Epic("Надо сделать утром", "Лучше управиться до 13:00", idEpic2);
        manager.updateEpic(epic2, Task.Status.DONE);
        subtask1 = new Subtask("Убрать кухню", "Лучше управиться до 13:00", idSubEpic1, idSubtask1);
        manager.updateSubtask(subtask1, Task.Status.NEW);
        manager.updateSubtask(subtask1, Task.Status.DONE);

        //Получение списка всех подзадач определённого эпика
        System.out.println(manager.getAllEpicSubs(idEpic1));
        System.out.println(manager.getAllEpicSubs(idEpic2));

    }
}