package alex.task_manager.adapters;

import android.database.Cursor;
import android.util.Pair;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import alex.task_manager.models.TaskViewModel;

public class partOfAdapter {
    public static final int
        TIMESTAMP_TYPE = 0,
        TASK_TYPE = 1;

    private List<TaskViewModel> tasks;
    private ArrayList<Timestamp> dates;
    //first - type, second - index in tasks or dates array
    private ArrayList<Pair<Integer,Integer>> indexes;

    partOfAdapter(Cursor cursor) {

        //ToDo: timestamp are correct? One day tasks has similary timestamp?
        Timestamp lastTimestamp = tasks.get(0).getDeadline();
        dates.add(lastTimestamp);

        indexes.add(new Pair<Integer, Integer>(TIMESTAMP_TYPE, dates.size()-1));
        for (int i = 0; i < tasks.size(); i++) {
            if (lastTimestamp == tasks.get(i).getDeadline()) {
                indexes.add(new Pair<Integer, Integer>(TASK_TYPE, i));
            } else {
                lastTimestamp = tasks.get(i).getDeadline();
                indexes.add(new Pair<Integer, Integer>(TIMESTAMP_TYPE, dates.size()-1));
            }
        }
    }

    public Timestamp getTimestamp(int i) {
        if (indexes.get(i).first == TIMESTAMP_TYPE) {
            return dates.get(indexes.get(i).second);
        }
        return null;
    }

    public TaskViewModel getTask(int i) {
        if (indexes.get(i).first == TASK_TYPE) {
            return tasks.get(indexes.get(i).second);
        }
        return null;
    }

    public boolean isTask(int i) {
        return indexes.get(i).first == TASK_TYPE;
    }

    public boolean isTimestamp(int i) {
        return indexes.get(i).first == TIMESTAMP_TYPE;
    }
}
