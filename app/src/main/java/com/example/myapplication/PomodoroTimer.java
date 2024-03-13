package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.media.MediaPlayer;

public class PomodoroTimer{
    private final TextView timerTextView;
    private final Button startPauseButton;
    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    private long timeLeftInMillis;
    public long studyTime;
    public long restTime;

    private float iterationCount;
    private int iterationCountInitial;
    private TextView iterationTextCount;
    private TextView iterationType;

    public MediaPlayer timerSound;
    public final Handler handler = new Handler();
    public Runnable failTask;
    public NotificationHelper notificationHelper;
    public enum TimerPhase {
        STUDY, REST, STOPPED
    }
    public TimerPhase currentPhase = TimerPhase.STOPPED;
    private final Fragment fragment;

    public  PomodoroTimer(TextView timerTextView, Fragment fragment, Button startPauseButton, long sTime, long rTime, int iterationCount, TextView iterationTextCount, TextView iterationType)
    {
        notificationHelper = new NotificationHelper(fragment);
        notificationHelper.createNotificationChannel();

        this.fragment = fragment;
        this.startPauseButton = startPauseButton;
        this.timerTextView = timerTextView;
        this.timerSound = MediaPlayer.create(fragment.getContext(), R.raw.timersound);
        this.iterationCount = iterationCount;
        this.iterationCountInitial = iterationCount;
        this.iterationTextCount = iterationTextCount;
        this.iterationType = iterationType;

        studyTime = sTime;
        restTime = rTime;

        timeLeftInMillis = studyTime;

        startPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerRunning) {
                    pauseTimer();
                } else if (currentPhase == TimerPhase.STOPPED || currentPhase == TimerPhase.STUDY) {
                    startTimer(timeLeftInMillis, restTime, TimerPhase.STUDY);
                }
                else
                {
                    startTimer(0, timeLeftInMillis, TimerPhase.REST);
                }
            }
        });
    }

    private void startTimer(long studyT, long restT, TimerPhase timerPhase)
    {
            countDownTimer = new CountDownTimer(studyT, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeftInMillis = millisUntilFinished;
                    updateTimerText();
                }

                @Override
                public void onFinish() {
                    if (currentPhase == TimerPhase.STUDY)
                    {
                        startTimer(restT, 0, TimerPhase.REST);
                    }
                    else
                    {
                        iterationCount--;
                        if (iterationCount > 0)
                        {
                            startTimer(studyTime, restTime, TimerPhase.STUDY);
                        }
                        else
                        {
                            resetTimer();
                            timerSound.start();
                        }
                    }
                }
            }.start();
            currentPhase = timerPhase;
            timerSound.start();
            timerRunning = true;
            startPauseButton.setText("Pause");
    }

    public void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        startPauseButton.setText(fragment.getString(R.string.resume));
    }

    public void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timeLeftInMillis = studyTime;
        timerRunning = false;
        currentPhase = TimerPhase.STOPPED;
        updateTimerText();
        startPauseButton.setText(fragment.getString(R.string.start));
        iterationTextCount.setText(String.valueOf(iterationCountInitial));
    }

    public void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        @SuppressLint("DefaultLocale") String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(timeLeftFormatted);
        @SuppressLint("DefaultLocale") String iterationString = String.format("%d", (int) (iterationCount));
        if(iterationTextCount.getText() != iterationString)
        {
            iterationTextCount.setText(iterationString);
        }
        if(iterationType.getText() != currentPhase.toString())
        {
            iterationType.setText(currentPhase.toString());
        }
    }
}
