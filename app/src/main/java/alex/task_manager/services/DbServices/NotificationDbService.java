package alex.task_manager.services.DbServices;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.security.Timestamp;
import java.util.Calendar;

import alex.task_manager.R;
import alex.task_manager.models.TaskModel;

public class NotificationDbService {
    public static final String
        NOTIFICATIONS_TABLE_NAME = "Notifications",
        ID_COLUMN = "_id",
        TITLE_COLUMN = "title",
        TEXT_COLUMN = "text",
        TASK_ID_COLUMN = "task_id",
        TIME_COLUMN = "time",
        CHANNEL_ID = "alex.task_manager.channel";

    Context context;
    DatabaseManager dbManager;
    private static NotificationDbService mInstance = new NotificationDbService();
    private NotificationManager mManager;

    public static NotificationDbService getInstance(Context context) {
        mInstance.context = context;
        mInstance.dbManager = DatabaseManager.getInstance(context);
        return mInstance;
    }

    private void createChannel() {
        NotificationChannel androidChannel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);

        getManager().createNotificationChannel(androidChannel);
    }

    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            createChannel();
        }
        return mManager;
    }

    public static void  upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+ NOTIFICATIONS_TABLE_NAME +";");
            createTable(db);
        }
    }

    public static void createTable(SQLiteDatabase db) {
        Log.d("Notifications DB Service", "createDatabase");

        db.execSQL("CREATE TABLE " + NOTIFICATIONS_TABLE_NAME + "(" +
                    ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TITLE_COLUMN + " TEXT NOT NULL, " +
                    TEXT_COLUMN + " TEXT DEFAULT NULL, " +
                    TASK_ID_COLUMN + " INTEGER DEFAULT NULL, " +
                    TIME_COLUMN + " TIMESTAMP " +
                ");"
        );
    }

    public void updateTaskNotification(TaskModel task) {
        if (task.isDeleted()) {
            // TODO: remove its notification
        } else if (task.getDeadline() != null) {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setContentTitle(task.getName())
                            .setSmallIcon(R.mipmap.ic_launcher);

            if (task.getAbout() != null) {
                builder.setContentText(task.getAbout());
            }

            if (task.getDeadline() != null) {
                Calendar calendarFull = Calendar.getInstance();
                Calendar calendarTime = Calendar.getInstance();

                calendarFull.setTime(task.getDeadline());
                calendarTime.setTime(task.getNotificationTime());

                calendarFull.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
                calendarFull.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));

                builder.setWhen(calendarFull.getTimeInMillis());

            }

            Notification notification = builder.build();

            getManager().notify(task.getId(), notification);
        }
    }
}
