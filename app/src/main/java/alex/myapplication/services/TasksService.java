package alex.myapplication.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import alex.myapplication.models.TaskModel;

public class TasksService {
    private static TasksService mInstance;

    public static synchronized TasksService getInstance() {
        if (mInstance == null) {
            mInstance = new TasksService();
        }
        return mInstance;
    }

    public static Collection<TaskModel> loadTasks(){
        List<TaskModel> tasksList = new ArrayList<>();
        for (int x = 0; x < 10; x = x + 1) {
            tasksList.add(x, new TaskModel(x,1,"Title", "Description",false));
        }
        return tasksList;
    }
}
