package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

public class PomodoroTimer extends AppCompatActivity {
    private TextView timerTextView;
    private Button startPauseButton;
    private Button resetButton;
    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    private long timeLeftInMillis;
    public long studyTime;
    public long restTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro_timer);

        Intent intent = getIntent();
        if (intent != null) {
            studyTime = intent.getLongExtra("StudyTime", 25 * 60 * 1000);
            restTime = intent.getLongExtra("RestTime", 5 * 60 * 1000);
            timeLeftInMillis = studyTime;
        }

        //Locates the buttons
        timerTextView = findViewById(R.id.timerTextView);
        startPauseButton = findViewById(R.id.startPauseButton);
        resetButton = findViewById(R.id.resetButton);

        startPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerRunning) {
                    pauseTimer();
                } else {
                    startTimer(timeLeftInMillis, restTime);
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        updateTimerText();
    }

    /*
    Set the timer to the preferred amount
     */
    private void startTimer(long studyT, long restT)
    {
        countDownTimer = new CountDownTimer(studyT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                if (timerRunning)
                {
                    timerRunning = false;
                    startTimer(restT, 0);
                }
                else
                {
                    timerRunning = false;
                    startPauseButton.setText("Start");
                }
            }
        }.start();
        timerRunning = true;
        startPauseButton.setText("Pause");
    }

    /*
    Pauses the timer
     */
    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        startPauseButton.setText("Resume");
    }

    /*
    Reset the timer
     */
    private void resetTimer() {
        timeLeftInMillis = studyTime; // Reset to 25 minutes
        updateTimerText();
        if (timerRunning) {
            pauseTimer();
        }
        startPauseButton.setText("Start");
    }

    /*
    Updates the time text according to the left time
     */
    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        @SuppressLint("DefaultLocale") String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(timeLeftFormatted);
    }
}