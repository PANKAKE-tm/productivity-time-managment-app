package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID = "channel_id";

    private final Context context;

    public NotificationHelper(Context context) {
        this.context = context;
    }

    // creates channel for all notifications - only needs to be called once
    public void createNotificationChannel() {
        CharSequence name = context.getString(R.string.channel_name);
        String description = context.getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    // displays notification with specified ID - call as many times as you please
    public void displayNotification(String message, int notificationID, boolean setSilent) {
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground) // Ensure you have this icon in your drawable resources
                    .setContentTitle("Pomodoro App")
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSilent(setSilent);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(notificationID, builder.build());
        } catch (SecurityException e) {
            Log.e("NotificationHelper", "SecurityException while posting notification: " + e.getMessage());
            // Handle the exception or notify the user as appropriate
            // let's just hope this doesn't happen for now :-)
        }
    }

    // checks if user has given necessary permissions, only needs to be called once
    public void checkNotificationPermission() {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            new AlertDialog.Builder(context)
                    .setTitle("Notifications Disabled")
                    .setMessage("Notifications are essential for this app. Please enable them in settings.")
                    .setPositiveButton("Settings", (dialog, which) -> {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                        context.startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    // nuke notification out of existence, specified by id
    public void cancelNotification(int notificationID) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(notificationID);
    }
}
