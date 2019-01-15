package alex.task_manager.services.DbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import alex.task_manager.models.TaskModel;
import alex.task_manager.models.TaskViewModel;
import alex.task_manager.utils.TimestampUtils;

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
                "notification_time TIMESTAMP DEFAULT NULL," +
                "last_update_time TIMESTAMP NOT NULL," +
                "deleted INTEGER DEFAULT 0," +
                "completed INTEGER" +
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
        contentValues.put("name", task.getName());
        contentValues.put("about", task.getAbout());
        contentValues.put("completed", task.isChecked() ? 1 : 0);
        contentValues.put("deadline", task.getStringDeadline());
        contentValues.put("notification_time", task.getStringNotificationTime());
        contentValues.put("last_update_time", TimestampUtils.getNowString(TimestampUtils.FULL_DATE_FORMAT));

        SQLiteDatabase database = dbManager.getWritableDatabase();
        long rowID = database.insert("Tasks", null, contentValues);
        Log.d("TaskDbManager", String.format("Created task with id %d",rowID));
        database.close();
    }

    public Cursor getTaskModelCursorByPerformerId(int performerId) {

        String selectQuery = String.format(
                "SELECT * " +
                        "FROM Tasks AS T " +
                        "INNER JOIN Users ON Users._id = T.author_id " +
                        "WHERE T.author_id = %d;",
                performerId
        );
        SQLiteDatabase database = dbManager.getReadableDatabase();
        return database.rawQuery(selectQuery, null);
    }

    public Cursor getTaskModelCursorById(int taskId) {
        String selectQuery = String.format(
                "SELECT * " +
                "FROM Tasks AS T " +
                "WHERE T._id = %d; ",
                taskId
        );
        SQLiteDatabase database = dbManager.getReadableDatabase();
        return database.rawQuery(selectQuery, null);
    }

    public void setCompleted(int id, boolean completed) {
        String updateQuery = String.format(
                "UPDATE Tasks " +
                "SET completed=%d " +
                "WHERE _id = %d",
                (completed ? 1:0),
                id
        );
        SQLiteDatabase database = dbManager.getWritableDatabase();
        database.execSQL(updateQuery);
    }

    public void setDeleted(int id) {
        String updateQuery = String.format(
                "UPDATE Tasks " +
                        "SET deleted=%d " +
                        "WHERE _id = %d",
                1,
                id
        );
        SQLiteDatabase database = dbManager.getWritableDatabase();
        database.execSQL(updateQuery);
    }

    public void updateTask(int id, TaskModel task) {
        SQLiteDatabase db = dbManager.getWritableDatabase();

        db.execSQL(String.format(
                "UPDATE Tasks " +
                "SET name = \"%s\", about = \"%s\", complited = %d, deadline =\"%s\", " +
                        "notification_time =\"%s\", last_update_time = \"%s\" " +
                "WHERE _id = %d",
                task.getName(),
                task.getAbout(),
                task.isChecked() ? 1 : 0,
                task.getStringDeadline(),
                task.getStringNotificationTime(),
                TimestampUtils.getNowString(TimestampUtils.FULL_DATE_FORMAT),
                task.getId()
            ));
    }

    public void removeTask(int id) {
        SQLiteDatabase db = dbManager.getWritableDatabase();

        db.execSQL(String.format(
                "DELETE " +
                "FROM Tasks " +
                "WHERE _id=%d",
                id
        ));
    }
}
