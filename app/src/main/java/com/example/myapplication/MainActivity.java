package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {
    private final Handler handler = new Handler();
    private Runnable failTask;
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationHelper = new NotificationHelper(this);
        notificationHelper.createNotificationChannel();

        Button pomodoroButt = findViewById(R.id.startButton);
        pomodoroButt.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), PomodoroTimer.class);
            intent.putExtra("StudyTime", (long)(1 * 60 * 1000));
            intent.putExtra("RestTime", (long)(0.5 * 60 * 1000));

            startActivity(intent);
        });
    }

    protected void onStart() {
        super.onStart();
        notificationHelper.checkNotificationPermission();
    }

    @Override
    protected void onStop() {
        super.onStop();
        notificationHelper.displayNotification("Come back to the app within one minute, or you will fail");

        // Define the fail task
        failTask = () -> notificationHelper.displayNotification("Failed");
        handler.postDelayed(failTask, 60 * 1000); // 60 seconds
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cancel the notification and timer task
        notificationHelper.cancelNotification();
        handler.removeCallbacks(failTask);
    }
}
