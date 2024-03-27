package com.example.myapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class HomeFragment extends Fragment {
    private PomodoroTimer pomodoroTimer;
    public MainActivity mainActivity;
    private GifImageView cookingGif;
    private GifImageView washingGif;
    private GifImageView endGif;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView timerTextView = view.findViewById(R.id.timerTextView);
        Button startPauseButton = view.findViewById(R.id.startPauseButton);
        Button resetButton = view.findViewById(R.id.resetButton);
        TextView iterationTextCount = view.findViewById(R.id.iterationTextCount);
        TextView iterationType = view.findViewById(R.id.iterationType);
        cookingGif = view.findViewById(R.id.gifImageView);
        washingGif = view.findViewById(R.id.washingGif);
        endGif = view.findViewById(R.id.endGif);

        mainActivity = (MainActivity) getActivity();
        if (pomodoroTimer == null)
        {
            pomodoroTimer = new PomodoroTimer(timerTextView, this,
                    startPauseButton, cookingGif, washingGif, (long) (0.2 * 60 * 1000), (long) (0.1 * 60 * 1000), Objects.requireNonNull(mainActivity).currentIterration, iterationTextCount, iterationType);
        }
        CheckToRun();

        resetButton.setOnClickListener(v -> pomodoroTimer.resetTimer());
        return view;
    }

    public void CheckToRun()
    {
        if (Objects.requireNonNull(mainActivity).timerRunning)
        {
            if(mainActivity.currentPhase == PomodoroTimer.TimerPhase.STUDY)
            {
                pomodoroTimer.startTimer(mainActivity.timeleft, pomodoroTimer.restTime, mainActivity.currentPhase);
            }
            else
            {
                pomodoroTimer.startTimer(mainActivity.timeleft,0, mainActivity.currentPhase);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (pomodoroTimer != null)
        {
            if(mainActivity.currentPhase == PomodoroTimer.TimerPhase.STUDY)
            {
                mainActivity.notificationHelper.displayNotification(
                        "Come back to the app within one minute, or you will fail",
                        MainActivity.FAILING_NOTIFICATION_ID,
                        false
                );
            }
            mainActivity.handler.postDelayed(mainActivity.failTask, 10 * 1000); // 60 seconds
            mainActivity.currentPhase = pomodoroTimer.currentPhase;
            mainActivity.timeleft = pomodoroTimer.timeLeftInMillis;
            mainActivity.currentIterration = pomodoroTimer.iterationCount;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pomodoroTimer != null) {
            // Always cancel the notification and remove callbacks for the fail task when resuming the activity
            mainActivity.notificationHelper.cancelNotification(MainActivity.FAILING_NOTIFICATION_ID);
            mainActivity.handler.removeCallbacks(mainActivity.failTask);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pomodoroTimer.resetTimer();
        //pomodoroTimer.timerSound.release(); IF THE APP STARTS TO CRASH RANDOMLY
    }

    public void setTimer(boolean enable)
    {
        if(mainActivity == null)
        {
            mainActivity = (MainActivity) getActivity();
        }
        Objects.requireNonNull(mainActivity).timerRunning = enable;
    }

    public void ResetIteration()
    {
        mainActivity.currentIterration = pomodoroTimer.iterationCountInitial;
        pomodoroTimer.iterationCount = pomodoroTimer.iterationCountInitial;
        mainActivity.currentPhase = PomodoroTimer.TimerPhase.STOPPED;
        mainActivity.timerRunning = false;
        mainActivity.notificationHelper.cancelNotification(MainActivity.TIMER_NOTIFICATION_ID);

        endGif.setVisibility(View.VISIBLE);
        cookingGif.setVisibility(View.INVISIBLE);
        washingGif.setVisibility(View.INVISIBLE);

        Handler handler = new Handler();
        Runnable hideGifRunnable = () -> {
            endGif.setVisibility(View.INVISIBLE);
        };
        handler.postDelayed(hideGifRunnable, 3000); // 3 sec
    }
}