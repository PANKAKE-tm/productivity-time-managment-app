package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    private final Handler handler = new Handler();
    private Runnable failTask;
    private NotificationHelper notificationHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationHelper = new NotificationHelper(this);
        notificationHelper.createNotificationChannel();

        bottomNavigationView = findViewById(R.id.bottomNavView);
        frameLayout = findViewById(R.id.frameLayout);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

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
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.navHome);
        loadFragment(new HomeFragment(), true);
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (isAppInitialized) {
            fragmentTransaction.add(R.id.frameLayout, fragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayout, fragment);
        }
        fragmentTransaction.commit();
    }
}
