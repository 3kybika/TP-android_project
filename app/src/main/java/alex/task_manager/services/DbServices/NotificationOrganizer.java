package alex.task_manager.services.DbServices;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.Calendar;

import alex.task_manager.broadcast.NotificationBroadcastReciever;
import alex.task_manager.models.TaskModel;

public class NotificationOrganizer {
    public static final String CHANNEL_ID = "alex.task_manager.channel";

    private Context context;
    private static NotificationOrganizer mInstance = new NotificationOrganizer();
    private NotificationManager mManager;

    public static NotificationOrganizer getInstance(Context context) {
        mInstance.context = context;
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

    public void updateTaskNotification(TaskModel task) {
        if (task == null) {
            return;
        }
        createChannel();

        if (task.isDeleted() || task.isChecked() || (task.getDeadline() == null || Calendar.getInstance().getTimeInMillis() >= task.getFullTime())) {
            cancelTaskNotification(task);
        } else {
            cancelTaskNotification(task);
            addTaskNotification(task);
        }
    }

    private void addTaskNotification(@NonNull TaskModel task) {
        long notificationTime = task.getFullTime();

        int requestCode = task.getLocalId();
        int notificationId = task.getLocalId();
        String title = task.getName();
        String text = task.getAbout();

        Intent notifyIntent = new Intent(context, NotificationBroadcastReciever.class);

        notifyIntent.putExtra("id", notificationId)
                .putExtra("title", title)
                .putExtra("text", text)
                .putExtra("time", notificationTime);

        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (context, requestCode, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
    }

    private void cancelTaskNotification(@NonNull TaskModel task) {
        int requestCode = task.getLocalId();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(context.getApplicationContext(),
                NotificationBroadcastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), requestCode, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }
}
