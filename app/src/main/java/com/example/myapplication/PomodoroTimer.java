package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Button;

public class PomodoroTimer extends AppCompatActivity {
    private TextView timerTextView;
    private Button startPauseButton;
    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    private long timeLeftInMillis;
    public long studyTime;
    public long restTime;

    private final Handler handler = new Handler();
    private Runnable failTask;
    private NotificationHelper notificationHelper;

    public enum TimerPhase {
        STUDY, REST, STOPPED
    }

    public TimerPhase currentPhase = TimerPhase.STOPPED;

    @Override/*
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        notificationHelper = new NotificationHelper(this);
        notificationHelper.createNotificationChannel();

        Intent intent = getIntent();
        if (intent != null) {
            studyTime = intent.getLongExtra("StudyTime", 25 * 60 * 1000);
            restTime = intent.getLongExtra("RestTime", 5 * 60 * 1000);
            timeLeftInMillis = studyTime;
        }

        timerTextView = findViewById(R.id.timerTextView);
        startPauseButton = findViewById(R.id.startPauseButton);
        Button resetButton = findViewById(R.id.resetButton);

        startPauseButton.setOnClickListener(v -> {
            if (timerRunning) {
                pauseTimer();
            } else {
                if (currentPhase == TimerPhase.STOPPED || currentPhase == TimerPhase.REST) {
                    startStudyTimer();
                } else {
                    startRestTimer();
                }
            }
        });

        resetButton.setOnClickListener(v -> resetTimer());

        updateTimerText();
    }*/

    protected void onStart() {
        super.onStart();
        notificationHelper.checkNotificationPermission();
    }

    protected void onStop() {
        super.onStop();
        if (currentPhase == TimerPhase.STUDY) {
            notificationHelper.displayNotification("Come back to the app within one minute, or you will fail");

            // Define the fail task
            failTask = () -> {
                if (currentPhase == TimerPhase.STUDY) { // Double-check phase in case it changes
                    notificationHelper.displayNotification("Failed");
                }
            };
            handler.postDelayed(failTask, 60 * 1000); // 60 seconds
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Always cancel the notification and remove callbacks for the fail task when resuming the activity
        notificationHelper.cancelNotification();
        handler.removeCallbacks(failTask);
    }

    public void startStudyTimer() {
        countDownTimer = new CountDownTimer(studyTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                startRestTimer();
            }
        }.start();
        timerRunning = true;
        currentPhase = TimerPhase.STUDY;
        startPauseButton.setText(getString(R.string.pause));
    }

    public void startRestTimer() {
        countDownTimer = new CountDownTimer(restTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                currentPhase = TimerPhase.STOPPED;
                startPauseButton.setText(getString(R.string.start));
                timeLeftInMillis = studyTime; // Prepare for next study session
                updateTimerText();
            }
        }.start();
        timerRunning = true;
        currentPhase = TimerPhase.REST;
        startPauseButton.setText(getString(R.string.pause));
    }

    public void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        startPauseButton.setText(getString(R.string.resume));
    }

    public void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timeLeftInMillis = studyTime;
        timerRunning = false;
        currentPhase = TimerPhase.STOPPED;
        updateTimerText();
        startPauseButton.setText(getString(R.string.start));
    }

    public void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        @SuppressLint("DefaultLocale") String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(timeLeftFormatted);
    }
}
