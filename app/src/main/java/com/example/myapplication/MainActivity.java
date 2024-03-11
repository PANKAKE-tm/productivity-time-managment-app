package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private Runnable failTask;
    private static final String CHANNEL_ID = "channel_id";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        Button pomodoroButt = findViewById(R.id.startButton);
        pomodoroButt.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), PomodoroTimer.class);
            intent.putExtra("StudyTime", (long)(1 * 60 * 1000));
            intent.putExtra("RestTime", (long)(0.5 * 60 * 1000));

            startActivity(intent);
        });
    }

    private void checkNotificationPermission() {
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            new AlertDialog.Builder(this)
                    .setTitle("Notifications Disabled")
                    .setMessage("Notifications are essential for this app. Please enable them in settings.")
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Intent to open the app's notification settings
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    protected void onStart() {
        super.onStart();
        checkNotificationPermission();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // User has left the activity, do something here
        Log.d("ActivityLifecycle", "User has left the app");

        displayNotification("Come back to the app within one minute, or you will fail");

        // Start the fail timer
        failTask = () -> displayNotification("Failed");
        handler.postDelayed(failTask, 60 * 1000); // 60 seconds
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cancel the notification and timer task
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);

        handler.removeCallbacks(failTask);
    }

    private void createNotificationChannel() {
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void displayNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Set your own icon
                .setContentTitle("Pomodoro App")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
