package alex.task_manager.services.DbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Timestamp;
import java.util.List;

import alex.task_manager.models.TaskForm;
import alex.task_manager.models.TaskModel;
import alex.task_manager.requests.TaskJsonModel;
import alex.task_manager.utils.TimestampUtils;

public class TasksDbService {

    public static final String
            //table name
            TASKS_TABLE_NAME = "Tasks",
            //table columns
            LOCAL_ID_COLUMN = "_tasks_id",  // Local (for this device) identificator(id)
            GLOBAL_ID_COLUMN = "tasks_id",   // Global (for all devices and servers) id
            AUTHOR_ID_COLUMN = "author_id",     //Id of creator user
            CHANGED_BY_COLUMN = "changed_by_id",    //Id of user that make last change
            PRIORITY_COLUMN = "priority",
            TASK_NAME_COLUMN = "name",
            TASK_ABOUT_COLUMN = "about",
            DEADLINE_COLUMN = "deadline",
            NOTIFICATION_TIME_COLUMN = "notification_time",
            LAST_UPDATE_TIME_COLUMN = "last_update_time",
            DELETED_COLUMN = "deleted",
            COMPLITED_COLUMN = "complited";

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
        db.execSQL("CREATE TABLE "+ TASKS_TABLE_NAME +" (" +
                LOCAL_ID_COLUMN         + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GLOBAL_ID_COLUMN + " INTEGER DEFAULT NULL, " +
                AUTHOR_ID_COLUMN        + " INTEGER NOT NULL, " +
                CHANGED_BY_COLUMN       + " INTEGER NOT NULL, " +
                PRIORITY_COLUMN         + " INTEGER DEFAULT NULL, " +
                TASK_NAME_COLUMN        + " TEXT NOT NULL, " +
                TASK_ABOUT_COLUMN       + " TEXT DEFAULT NULL," +
                DEADLINE_COLUMN         + " TIMESTAMP DEFAULT NULL," +
                NOTIFICATION_TIME_COLUMN+ " TIMESTAMP DEFAULT NULL," +
                LAST_UPDATE_TIME_COLUMN + " TIMESTAMP NOT NULL," +
                DELETED_COLUMN          + " INTEGER DEFAULT 0," +
                COMPLITED_COLUMN        + " INTEGER DEFAULT 0" +
                ");"
        );
    }

    public static void  upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+ TASKS_TABLE_NAME +";");
            createDatabase(db);
        }
    }

    public void createTask(TaskForm task) {
        String insertQuery = String.format(
                "INSERT INTO " + TASKS_TABLE_NAME + " (" +
                        AUTHOR_ID_COLUMN + "," +
                        CHANGED_BY_COLUMN + "," +
                        PRIORITY_COLUMN + "," +
                        TASK_NAME_COLUMN + "," +
                        TASK_ABOUT_COLUMN   + "," +
                        DEADLINE_COLUMN + "," +
                        NOTIFICATION_TIME_COLUMN + "," +
                        LAST_UPDATE_TIME_COLUMN +
                        ") SELECT user_id, user_id, %d, \"%s\", \"%s\", \"%s\", \"%s\", \"%s\" " +
                        "FROM " + UserDbService.CURRENT_USER_TABLE_NAME +
                        " WHERE _id = 0;",
                task.getPriority(),
                task.getName(),
                task.getAbout(),
                task.getStringDeadline(),
                task.getStringNotificationTime(),
                TimestampUtils.getNowString(TimestampUtils.FULL_DATE_FORMAT)
        );

        SQLiteDatabase database = dbManager.getWritableDatabase();
        database.execSQL(insertQuery);
    }

    public void createTask(TaskModel task) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("author_id", task.getAuthorId());
        contentValues.put("name", task.getName());
        contentValues.put("about", task.getAbout());
        contentValues.put("complited", task.isChecked() ? 1 : 0);
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
                        "INNER JOIN Users ON Users.user_id = T.author_id " +
                        "WHERE T.author_id = %d;",
                performerId
        );
        SQLiteDatabase database = dbManager.getReadableDatabase();
        return database.rawQuery(selectQuery, null);
    }

    public Cursor getTaskModelCursorById(int taskId) {
        Log.d("taskId", "" + taskId);
        String selectQuery = String.format(
                "SELECT * " +
                " FROM "+ TASKS_TABLE_NAME +
                " WHERE " + LOCAL_ID_COLUMN + " = %d; ",
                taskId
        );
        Log.d("query", selectQuery);
        SQLiteDatabase database = dbManager.getReadableDatabase();
        return database.rawQuery(selectQuery, null);
    }

    public void setCompleted(int id, boolean completed) {
        String updateQuery = String.format(
                "UPDATE " + TASKS_TABLE_NAME +
                " SET " + COMPLITED_COLUMN + " = %d " +
                " WHERE "+ LOCAL_ID_COLUMN +" = %d",
                (completed ? 1:0),
                id
        );
        SQLiteDatabase database = dbManager.getWritableDatabase();
        database.execSQL(updateQuery);
    }

    public void setDeleted(int id) {
        String updateQuery = String.format(
                "UPDATE " + TASKS_TABLE_NAME +
                        " SET " + DELETED_COLUMN +" =%d " +
                        " WHERE "+ LOCAL_ID_COLUMN +" = %d",
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
                "SET "+ TASK_NAME_COLUMN + " = \"%s\", " +
                        TASK_ABOUT_COLUMN+ " = \"%s\", " +
                        COMPLITED_COLUMN + " = %d, " +
                        DEADLINE_COLUMN  + " =\"%s\", " +
                        NOTIFICATION_TIME_COLUMN + " =\"%s\", " +
                        LAST_UPDATE_TIME_COLUMN + " = \"%s\" " +
                "WHERE " + LOCAL_ID_COLUMN + " = %d",
                task.getName(),
                task.getAbout(),
                task.isChecked() ? 1 : 0,
                task.getStringDeadline(),
                task.getStringNotificationTime(),
                TimestampUtils.getNowString(TimestampUtils.FULL_DATE_FORMAT),
                task.getId()
            ));
    }

    public void updateTask(int id, TaskForm task) {
        SQLiteDatabase db = dbManager.getWritableDatabase();

        db.execSQL(String.format(
                "UPDATE " + TASKS_TABLE_NAME +
                        " SET "+ TASK_NAME_COLUMN + " = \"%s\", " +
                        TASK_ABOUT_COLUMN+ " = \"%s\", " +
                        DEADLINE_COLUMN  + " =\"%s\", " +
                        NOTIFICATION_TIME_COLUMN + " =\"%s\", " +
                        LAST_UPDATE_TIME_COLUMN + " = \"%s\" " +
                        " WHERE " + LOCAL_ID_COLUMN + " = %d",
                task.getName(),
                task.getAbout(),
                task.getStringDeadline(),
                task.getStringNotificationTime(),
                TimestampUtils.getNowString(TimestampUtils.FULL_DATE_FORMAT),
                id
        ));
    }

    public void removeTask(int id) {
        SQLiteDatabase db = dbManager.getWritableDatabase();

        db.execSQL(String.format(
                "DELETE *" +
                " FROM " + TASKS_TABLE_NAME +
                " WHERE " + LOCAL_ID_COLUMN + " =%d",
                id
        ));
    }

    public List<TaskJsonModel> getTasksForSync(Timestamp lastSyncTime) {

        String query = String.format(
                "SELECT * FROM " + TASKS_TABLE_NAME +
                        " WHERE " + LAST_UPDATE_TIME_COLUMN + " > %s AND " +
                        CHANGED_BY_COLUMN + " IN (" +
                        "SELECT " + UserDbService.CURRENT_USER_ID_COLUMN +
                        " FROM " + UserDbService.CURRENT_USER_TABLE_NAME +
                        " WHERE " + UserDbService.CURRENT_USER_KEY_COLUMN + " = " + UserDbService.CURRENT_USER_KEY +
                        ");",
                lastSyncTime
        );

        SQLiteDatabase db = dbManager.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return (new TaskJsonModel.Builder()).buildListInstance(cursor);
    }

    private void createTask(TaskJsonModel task) {
        String query = String.format(
                "INSERT INTO " + TASKS_TABLE_NAME + " (" +
                        GLOBAL_ID_COLUMN + "," +
                        AUTHOR_ID_COLUMN + "," +
                        CHANGED_BY_COLUMN + "," +
                        PRIORITY_COLUMN + "," +
                        TASK_NAME_COLUMN  + "," +
                        TASK_ABOUT_COLUMN + "," +
                        DEADLINE_COLUMN + "," +
                        NOTIFICATION_TIME_COLUMN + "," +
                        LAST_UPDATE_TIME_COLUMN + ")" +
                        " VALUES( %d, %d, %d, %d, \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"), ",
                task.getGlobalId(),
                task.getAuthorId(),
                task.getChangedBy(),
                task.getPriority(),
                task.getName(),
                task.getAbout(),
                task.getStringDeadline(),
                task.getStringNotificationTime(),
                task.getStringLastChangeTime()
        );

        SQLiteDatabase db = dbManager.getWritableDatabase();
        db.execSQL(query);
    }

    private void updateTask(TaskJsonModel task) {
        String query = String.format(
                "UPDATE " + TASKS_TABLE_NAME +
                        " SET "+
                        GLOBAL_ID_COLUMN + " = %d, " +
                        AUTHOR_ID_COLUMN + " = %d, " +
                        CHANGED_BY_COLUMN + " = %d, " +
                        PRIORITY_COLUMN + " = %d, " +
                        TASK_NAME_COLUMN + " = \"%s\", " +
                        TASK_ABOUT_COLUMN+ " = \"%s\", " +
                        DEADLINE_COLUMN  + " =\"%s\", " +
                        NOTIFICATION_TIME_COLUMN + " =\"%s\", " +
                        LAST_UPDATE_TIME_COLUMN + " = \"%s\" " +
                        " WHERE " + LOCAL_ID_COLUMN + " = %d:",
                    task.getGlobalId(),
                    task.getAuthorId(),
                    task.getChangedBy(),
                    task.getPriority(),
                    task.getName(),
                    task.getAbout(),
                    task.getStringDeadline(),
                    task.getStringNotificationTime(),
                    task.getStringLastChangeTime(),
                    task.getLocalId()
                );

        SQLiteDatabase db = dbManager.getWritableDatabase();
        db.execSQL(query);
    }

    public void syncTasks (List<TaskJsonModel> updatedTasks) {
        for (TaskJsonModel task : updatedTasks) {
            if (task.getLocalId()!= -1) {
                updateTask(task);
            } else {
                createTask(task);
            }
        }
    }

}
