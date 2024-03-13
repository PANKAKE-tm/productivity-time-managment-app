package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HomeFragment extends Fragment {

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




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        PomodoroTimer pomodoroTimer = new PomodoroTimer();

/*
        Intent intent = getIntent();
        if (intent != null) {
            studyTime = intent.getLongExtra("StudyTime", 25 * 60 * 1000);
            restTime = intent.getLongExtra("RestTime", 5 * 60 * 1000);
            timeLeftInMillis = studyTime;
        }*/

        timerTextView = view.findViewById(R.id.timerTextView);
        startPauseButton = view.findViewById(R.id.startPauseButton);
        Button resetButton = view.findViewById(R.id.resetButton);

        startPauseButton.setOnClickListener(v -> {
            if (timerRunning) {
                pomodoroTimer.pauseTimer();
            } else {
                if (pomodoroTimer.currentPhase == PomodoroTimer.TimerPhase.STOPPED
                        || pomodoroTimer.currentPhase == PomodoroTimer.TimerPhase.REST) {

                    pomodoroTimer.startStudyTimer();
                } else {
                    pomodoroTimer.startRestTimer();
                }
            }
        });

        resetButton.setOnClickListener(v -> pomodoroTimer.resetTimer());

        pomodoroTimer.updateTimerText();

        return view;
    }
}