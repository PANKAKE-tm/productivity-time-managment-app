package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button pomodoroButt = findViewById(R.id.startButton);
        pomodoroButt.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), PomodoroTimer.class);
            intent.putExtra("StudyTime", (long)(1 * 60 * 1000));
            intent.putExtra("RestTime", (long)(0.5 * 60 * 1000));

            startActivity(intent);
        });
    }
}
