package alex.task_manager.services.IntentServices;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

import java.util.concurrent.ThreadLocalRandom;

import alex.task_manager.R;
import alex.task_manager.activities.SignUpActivity;
import alex.task_manager.services.DbServices.NotificationDbService;

public class NotificationIntentService extends IntentService {

    public NotificationIntentService() {
        super("NotificationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        int id = intent.getIntExtra("id", -1);
        long time = intent.getLongExtra("time", -1);

        Notification.Builder builder = new Notification.Builder(this, NotificationDbService.CHANNEL_ID);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setSmallIcon(R.drawable.ic_calendar);
        builder.setAutoCancel(true);

        if (time != -1) {
            builder.setWhen(time);
            builder.setShowWhen(true);
        }

        Intent notifyIntent = new Intent(this, SignUpActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //to be able to launch your activity from the notification
        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(id, notificationCompat);
    }
}
