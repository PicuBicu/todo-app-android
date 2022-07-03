package pl.piotrb.todoapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import pl.piotrb.todoapp.database.models.Todo;

public class Notification extends BroadcastReceiver {

    public static final String CHANNEL_ID = "channel1";
    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Todo todo = (Todo) intent.getSerializableExtra(MainActivity.TODO_DATA);
        Intent addUpdateIntent = new Intent(context, AddUpdateTodoActivity.class);
        addUpdateIntent.putExtra(MainActivity.TODO_DATA, todo);
        addUpdateIntent.putExtra("notification", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                NOTIFICATION_ID,
                addUpdateIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        android.app.Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(todo.title)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentText(todo.description)
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, notification);
    }
}
