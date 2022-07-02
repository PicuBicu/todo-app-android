package pl.piotrb.todoapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class Notification extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 1;
    public static final String CHANNEL_ID = "channel1";
    public static final String DESCRIPTION = "description";
    public static final String TITLE = "title";

    @Override
    public void onReceive(Context context, Intent intent) {
        android.app.Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(intent.getStringExtra(TITLE))
                .setContentText(intent.getStringExtra(DESCRIPTION))
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, notification);
    }
}
