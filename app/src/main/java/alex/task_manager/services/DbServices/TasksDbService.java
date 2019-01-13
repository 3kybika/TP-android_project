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

public class TasksDbService {

    Context context;
    DatabaseManager dbManager;
    private static TasksDbService mInstance = new TasksDbService();

    public static TasksDbService getInstance(Context context) {
        mInstance.context = context;
        mInstance.dbManager = DatabaseManager.getInstance(context);
        return mInstance;
    }

    public static void  createDatabase(SQLiteDatabase db) {
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

    public static void  upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS Tasks;");
            createDatabase(db);
        }
    }

    public void createTask(TaskModel task) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("author_id", task.getAuthorId());
        contentValues.put("name", task.getCaption());
        contentValues.put("about", task.getAbout());
        contentValues.put("complited", task.isChecked());

        SQLiteDatabase database = dbManager.getWritableDatabase();
        long rowID = database.insert("Tasks", null, contentValues);
        Log.d("TaskDbManager", String.format("Created task with id %d",rowID));
        database.close();
    }

    public Cursor getTaskCursorByPerformerId(int performerId) {

        //createTask(new TaskModel(performerId, "Test task caption", "Test task description"));

        Log.d("TaskDbManager", String.format("Get user with %d",performerId));
        String selectQuery = "SELECT author_id FROM Tasks;";
        SQLiteDatabase database = dbManager.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        Log.d("TaskDbManager", String.format("Tasks have author %d ",count));

        selectQuery = String.format("SELECT COUNT(*) " +
                "FROM Tasks AS T " +
                "INNER JOIN Users ON Users._id = T.author_id " +
                "WHERE T.author_id = %d;",performerId);
        cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getInt(0);
        Log.d("TaskDbManager", String.format("Tasks have %d tasks user_id  ",count));

        selectQuery = String.format(
                "SELECT T._id, Users.login, T.name, T.about, T.complited " +
                        "FROM Tasks AS T " +
                        "INNER JOIN Users ON Users._id = T.author_id " +
                        "WHERE T.author_id = %d;",
                performerId
        );
        database = dbManager.getReadableDatabase();
        return database.rawQuery(selectQuery, null);
    }

    public List<TaskViewModel> getTaskByPerformerId(int performerId) {
        ArrayList<TaskViewModel> taskList = new ArrayList<TaskViewModel>();
        String selectQuery = String.format(
                "SELECT T._id, Users.login, T.name, T.about, T.complited " +
                        "FROM Tasks AS T " +
                        "INNER JOIN Users ON Users._id = T.author_id " +
                        "WHERE T.author_id = %d;",
                performerId
        );
        SQLiteDatabase database = dbManager.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        return getTaskViewModelList(cursor);
    }

    public List<TaskViewModel> getTaskByPerformerId(int performerId, Timestamp timePeriodBegin, Timestamp timePeriodEnd){
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
        SQLiteDatabase database = dbManager.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        return getTaskViewModelList(cursor);
    }

}
