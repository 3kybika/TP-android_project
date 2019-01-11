package alex.task_manager.services.DbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import alex.task_manager.models.TaskModel;
import alex.task_manager.models.TaskViewModel;
import alex.task_manager.utils.TimestampUtils;

import static alex.task_manager.services.DbServices.Mappers.getTaskViewModelList;

public class TasksDbService extends BaseDbService {

    private static TasksDbService mInstance = new TasksDbService();

    public static TasksDbService getInstance(Context context) {
        mInstance.context = context;
        return mInstance;
    }

    @Override
    public void createDatabase(SQLiteDatabase db) {
        Log.d("Tasks Service", "onCreate database");
        // создаем таблицу с полями
        db.execSQL("CREATE TABLE Tasks (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "about TEXT," +
                "author_id," +
                "deadline TIMESTAMP," +
                "complited INTEGER" +
            ");"
        );
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS Tasks;");
            createDatabase(db);
        }
    }

    public void createTask(TaskModel task) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("author_id", task.getAuthorId());
        contentValues.put("caption", task.getCaption());
        contentValues.put("about", task.getAbout());
        contentValues.put("checked", task.isChecked());

        SQLiteDatabase database = helper.getWritableDatabase();
        database.insert("Tasks", null, contentValues);
        database.close();
    }

    public Cursor getTaskCursorByPerformerId(int performerId) {
        checkInitialized();
        ArrayList<TaskViewModel> taskList = new ArrayList<TaskViewModel>();
        String selectQuery = String.format(
                "SELECT T._id, U.name, T.caption, T.about, T.checked " +
                        "FROM Tasks AS T " +
                        "INNER JOIN Users AS U ON U._id = T.author_id" +
                        "WHERE Tasks.author_id = %d;",
                performerId
        );
        SQLiteDatabase database = helper.getReadableDatabase();
        return database.rawQuery(selectQuery, null);
    }

    public List<TaskViewModel> getTaskByPerformerId(int performerId) {
        checkInitialized();
        ArrayList<TaskViewModel> taskList = new ArrayList<TaskViewModel>();
        String selectQuery = String.format(
                "SELECT T._id, U.name, T.caption, T.about, T.checked " +
                "FROM Tasks AS T " +
                "INNER JOIN Users AS U ON U._id = T.author_id" +
                "WHERE Tasks.author_id = %d;",
                performerId
        );
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        return getTaskViewModelList(cursor);
    }

    public List<TaskViewModel> getTaskByPerformerId(int performerId, Timestamp timePeriodBegin, Timestamp timePeriodEnd){
        checkInitialized();

        ArrayList<TaskViewModel> taskList = new ArrayList<TaskViewModel>();
        String selectQuery = String.format(
                        "SELECT T.id, U.name, T.caption, T.about, T.checked " +
                        "FROM Tasks AS T " +
                        "INNER JOIN Users AS U ON U.id = T.author_id " +
                        "WHERE T.deadline < '%s' AND T.deadline > '%s' AND author_id = %;",

                        TimestampUtils.timestampToString(timePeriodBegin, TimestampUtils.FULL_DATE_FORMAT),
                        TimestampUtils.timestampToString(timePeriodEnd, TimestampUtils.FULL_DATE_FORMAT),
                        performerId
                );
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        return getTaskViewModelList(cursor);
    }

}
