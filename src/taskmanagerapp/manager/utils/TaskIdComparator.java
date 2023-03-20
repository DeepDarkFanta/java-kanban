package taskmanagerapp.manager.utils;

import taskmanagerapp.tasks.Task;
import java.util.Comparator;

public class TaskIdComparator implements Comparator<Task> {

    //этот метод мне нужен чтобы отсортировать лист тасков для правильной записи в файл, ибо чтение
    //идет сверху вниз и сабтаски не могут быть созданы раньше эпика, поэтому сортирую по айди
    //отдельным классом сделал чтобы не имплементировать доп интерфейс в класс менеджера
    //вдруг бы понадоилась бы другая реализация compare() или штатная)
    @Override
    public int compare(Task o1, Task o2) {
        return Integer.compare(o1.getId(), o2.getId());
    }
}
