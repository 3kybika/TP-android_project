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

import static alex.task_manager.services.DbServices.Mappers.boolToInt;
import static alex.task_manager.services.DbServices.Mappers.getTaskModel;
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
                "deadline TIMESTAMP DEFAULT NULL," +
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
        contentValues.put("complited", task.isChecked() ? 1 : 0);
        contentValues.put("deadline", task.getStringTime());

        Log.d("TaskDbManager", task.getStringTime());

        SQLiteDatabase database = dbManager.getWritableDatabase();
        long rowID = database.insert("Tasks", null, contentValues);
        Log.d("TaskDbManager", String.format("Created task with id %d",rowID));
        database.close();
    }

    public Cursor getTaskCursorByPerformerId(int performerId) {

        String selectQuery = String.format(
                "SELECT T._id, Users.login, T.name, T.about, T.complited, T.deadline " +
                        "FROM Tasks AS T " +
                        "INNER JOIN Users ON Users._id = T.author_id " +
                        "WHERE T.author_id = %d;",
                performerId
        );
        SQLiteDatabase database = dbManager.getReadableDatabase();
        return database.rawQuery(selectQuery, null);
    }

    public TaskModel getTaskById(int taskId) {
        String selectQuery = String.format(
                "SELECT T._id, T.author_id, T.name, T.about, T.complited, T.deadline " +
                        "FROM Tasks AS T " +
                        "WHERE T._id = %d;",
                taskId
        );
        SQLiteDatabase database = dbManager.getReadableDatabase();
        return getTaskModel(database.rawQuery(selectQuery, null));
    }

    public void setComplited(int id, boolean completed) {
        String updateQuery = String.format(
                "UPDATE Tasks(complited) SET VALUES(%d)" +
                        "WHERE _id = %d",
                boolToInt(completed),
                id
        );
        SQLiteDatabase database = dbManager.getWritableDatabase();
        database.execSQL(updateQuery);
    }

    public List<TaskViewModel> getTaskByPerformerId(int performerId) {

        return getTaskViewModelList(getTaskCursorByPerformerId(performerId));
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

    public void updateTask(int id, TaskModel task) {
        // TODO: implement
    }
}
