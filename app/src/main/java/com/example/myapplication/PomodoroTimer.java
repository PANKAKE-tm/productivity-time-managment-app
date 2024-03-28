package com.example.myapplication;

import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.media.MediaPlayer;

import pl.droidsonroids.gif.GifImageView;

public class PomodoroTimer {
    private final TextView timerTextView;
    private final Button startPauseButton;
    private final GifImageView startGif;
    private final GifImageView cookingGif;
    private final GifImageView washingGif;
    private final GifImageView endGif;
    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    public long timeLeftInMillis;
    public long studyTime;
    public long restTime;

    public int iterationCount;
    public final int iterationCountInitial;
    private final TextView iterationTextCount;
    private final TextView iterationType;

    public MediaPlayer timerSound;

    public enum TimerPhase {
        STUDY, REST, STOPPED
    }

    public TimerPhase currentPhase = TimerPhase.STOPPED;
    private final Fragment fragment;

    public PomodoroTimer(TextView timerTextView, Fragment fragment, Button startPauseButton, GifImageView startGif, GifImageView cookingGif, GifImageView washingGif, GifImageView endGif, long sTime, long rTime, int iterationCount, TextView iterationTextCount, TextView iterationType) {
        this.fragment = fragment;
        this.startPauseButton = startPauseButton;
        this.startGif = startGif;
        this.cookingGif = cookingGif;
        this.washingGif = washingGif;
        this.endGif = endGif;
        this.timerTextView = timerTextView;
        this.timerSound = MediaPlayer.create(fragment.getContext(), R.raw.timersound);
        this.iterationCount = iterationCount;
        this.iterationCountInitial = iterationCount;
        this.iterationTextCount = iterationTextCount;
        this.iterationType = iterationType;

        studyTime = sTime;
        restTime = rTime;
        timeLeftInMillis = studyTime;

        startPauseButton.setOnClickListener(v -> {
            if (timerRunning) {
                //pauseTimer();
                resetTimerInternal();
                SwitchGifs(false);
            } else if (currentPhase == TimerPhase.STOPPED || currentPhase == TimerPhase.STUDY) {
                startTimer(timeLeftInMillis, restTime, TimerPhase.STUDY);
                startBaking();
                SwitchGifs(true);
            } else {
                startTimer(timeLeftInMillis, 0, TimerPhase.REST);
                SwitchGifs(false);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void startTimer(long studyT, long restT, TimerPhase timerPhase) {
        HomeFragment homeFragment = (HomeFragment) fragment;
        countDownTimer = new CountDownTimer(studyT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                homeFragment.mainActivity.notificationHelper.displayNotification("Time left to " + currentPhase.toString() + " : " + formattedTimeLeft(), MainActivity.TIMER_NOTIFICATION_ID, true);
                updateTimerText();
            }

            @Override
            public void onFinish() {
                if (currentPhase == TimerPhase.STUDY) {
                    startTimer(restT, 0, TimerPhase.REST);

                } else {
                    iterationCount--;
                    if (iterationCount > 0) {
                        startTimer(studyTime, restTime, TimerPhase.STUDY);
                        SwitchGifs(true);
                    } else {
                        resetTimerInternal();
                        timerSound.start();
                        homeFragment.mainActivity.notificationHelper.cancelNotification(MainActivity.TIMER_NOTIFICATION_ID);
                        TimerFinish();
                    }
                }
            }
        }.start();
        currentPhase = timerPhase;

        if (!timerSound.isPlaying()) {
            timerSound.start();
        }

        timerRunning = true;
        homeFragment.setTimer(true);
        startPauseButton.setText("Stop");

        SwitchGifs(currentPhase == TimerPhase.STUDY);
    }

/*    public void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        HomeFragment homeFragment = (HomeFragment) fragment;
        homeFragment.mainActivity.notificationHelper.cancelNotification(MainActivity.TIMER_NOTIFICATION_ID);
        startPauseButton.setText(fragment.getString(R.string.resume));
    }*/

    @SuppressLint("SetTextI18n")
    public void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        timeLeftInMillis = studyTime;
        timerRunning = false;
        currentPhase = TimerPhase.STOPPED;
        updateTimerText();
        HomeFragment homeFragment = (HomeFragment) fragment;
        homeFragment.mainActivity.notificationHelper.cancelNotification(MainActivity.TIMER_NOTIFICATION_ID);

        startPauseButton.setText("Begin");
        iterationTextCount.setText(String.valueOf(iterationCountInitial));

        hideGifs();
    }

    @SuppressLint("SetTextI18n")
    public void resetTimerInternal() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        timeLeftInMillis = studyTime;
        timerRunning = false;
        currentPhase = TimerPhase.STOPPED;
        updateTimerText();
        HomeFragment homeFragment = (HomeFragment) fragment;
        homeFragment.mainActivity.notificationHelper.cancelNotification(MainActivity.TIMER_NOTIFICATION_ID);
        homeFragment.setTimer(false);
        homeFragment.mainActivity.currentPhase = TimerPhase.STOPPED;

        startPauseButton.setText("Begin");
        iterationTextCount.setText(String.valueOf(iterationCountInitial));
    }

    @SuppressLint("SetTextI18n")
    public void updateTimerText() {
        timerTextView.setText(formattedTimeLeft());
        @SuppressLint("DefaultLocale") String iterationString = String.format("%d", iterationCount);

        if (iterationTextCount.getText() != iterationString) {
            iterationTextCount.setText(iterationString);
        }

        if (currentPhase == TimerPhase.STUDY) {
            iterationType.setText("Baking your pankake!");
        } else if (currentPhase == TimerPhase.STOPPED) {
            iterationType.setText("Start baking");
            hideGifs();
        } else {
            iterationType.setText("Pankake cooked!");
        }
    }

    @SuppressLint("DefaultLocale")
    private String formattedTimeLeft() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @SuppressLint("SetTextI18n")
    public void TimerFinish() {
        HomeFragment homeFragment = (HomeFragment) fragment;
        homeFragment.ResetIteration();
        iterationType.setText("Start baking");
    }

    public void SwitchGifs(boolean cookingVisible) {
        if (cookingVisible) {
            cookingGif.setVisibility(View.VISIBLE);
            washingGif.setVisibility(View.INVISIBLE);
        } else {
            cookingGif.setVisibility(View.INVISIBLE);

            endGif.setVisibility(View.VISIBLE);
            Handler handler = new Handler();
            Runnable hideGifRunnable = () -> {
                endGif.setVisibility(View.INVISIBLE);
                washingGif.setVisibility(View.VISIBLE);
            };
            handler.postDelayed(hideGifRunnable, 1000);
        }
    }

    public void hideGifs() {
        cookingGif.setVisibility(View.INVISIBLE);
        washingGif.setVisibility(View.INVISIBLE);
    }

    public void startBaking() {
        startGif.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        Runnable hideGifRunnable = () -> startGif.setVisibility(View.INVISIBLE);
        handler.postDelayed(hideGifRunnable, 1000);
    }
}
