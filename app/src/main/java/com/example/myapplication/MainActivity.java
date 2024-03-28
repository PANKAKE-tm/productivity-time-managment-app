package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public boolean timerRunning = false;
    public long timeleft = 0;
    public int currentIterration = 1;
    public PomodoroTimer.TimerPhase currentPhase;
    public final Handler handler = new Handler();
    public Runnable failTask;
    public NotificationHelper notificationHelper;
    public static final int FAILING_NOTIFICATION_ID = 1;
    public static final int TIMER_NOTIFICATION_ID = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationHelper = new NotificationHelper(this);
        notificationHelper.createNotificationChannel();

        failTask = () -> {
            if (currentPhase == PomodoroTimer.TimerPhase.STUDY) { // Double-check phase in case it changes
                notificationHelper.displayNotification("Failed", FAILING_NOTIFICATION_ID, false);
                currentPhase = PomodoroTimer.TimerPhase.STOPPED;
                timerRunning = false;
            }
        };

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.navHome)
            {
                loadFragment(new HomeFragment(), false);
            } else if (itemId == R.id.navNews) {
                loadFragment(new NewsFragment(), false);
            } else if (itemId == R.id.navSettings) {
                loadFragment(new SettingsFragment(), false);
            } else if (itemId == R.id.navStatistics) {
                loadFragment(new StatisticsFragment(), false);
            } else { // nav Tasks
                loadFragment(new TasksFragment(), false);
            }
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.navHome);
        loadFragment(new HomeFragment(), true);
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        if (!isAppInitialized) {
            // Check if the fragment is already in the back stack
            boolean isFragmentInBackStack = fragmentManager.popBackStackImmediate(
                    fragment.getClass().getName(), 0);
            // If not, add it to the back stack
            if (!isFragmentInBackStack) {
                fragmentTransaction.addToBackStack(fragment.getClass().getName());
            }
        } else {
            // Clear the back stack if the app is initialized
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        notificationHelper.checkNotificationPermission();
    }
}
