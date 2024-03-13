package com.example.myapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HomeFragment extends Fragment {
    public PomodoroTimer pomodoroTimer;
    public int iterationCount = 2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView timerTextView = view.findViewById(R.id.timerTextView);
        Button startPauseButton = view.findViewById(R.id.startPauseButton);
        Button resetButton = view.findViewById(R.id.resetButton);
        TextView iterationTextCount = view.findViewById(R.id.iterationTextCount);
        TextView iterationType = view.findViewById(R.id.iterationType);

        if (pomodoroTimer == null) {
            pomodoroTimer = new PomodoroTimer(timerTextView, this,
                    startPauseButton, (long) (0.2 * 60 * 1000), (long) (0.1*60 * 1000), iterationCount, iterationTextCount, iterationType);
        }
        pomodoroTimer.updateTimerText();
        resetButton.setOnClickListener(v -> pomodoroTimer.resetTimer());
        return view;
    }

    public void onStart() {
        super.onStart();
        pomodoroTimer.notificationHelper.checkNotificationPermission();
    }

    public void onStop() {
        super.onStop();
        if (pomodoroTimer.currentPhase == PomodoroTimer.TimerPhase.STUDY) {
            pomodoroTimer.notificationHelper.displayNotification("Come back to the app within one minute, or you will fail");

            // Define the fail task
            pomodoroTimer.failTask = () -> {
                if (pomodoroTimer.currentPhase == PomodoroTimer.TimerPhase.STUDY) { // Double-check phase in case it changes
                    pomodoroTimer.notificationHelper.displayNotification("Failed");
                }
            };
            pomodoroTimer.handler.postDelayed(pomodoroTimer.failTask, 60 * 1000); // 60 seconds
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Always cancel the notification and remove callbacks for the fail task when resuming the activity
        pomodoroTimer.notificationHelper.cancelNotification();
        pomodoroTimer.handler.removeCallbacks(pomodoroTimer.failTask);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pomodoroTimer.timerSound.release();
    }
}