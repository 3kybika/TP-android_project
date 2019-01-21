package alex.task_manager.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import alex.task_manager.services.IntentServices.NotificationIntentService;

public class NotificationBroadcastReciever extends BroadcastReceiver {

    public NotificationBroadcastReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, NotificationIntentService.class);

        intent1.putExtra("id", intent.getIntExtra("id", -1));
        intent1.putExtra("title", intent.getStringExtra("title"));
        intent1.putExtra("text", intent.getStringExtra("text"));
        intent1.putExtra("time", intent.getLongExtra("time", -1));

        context.startService(intent1);
    }
}
