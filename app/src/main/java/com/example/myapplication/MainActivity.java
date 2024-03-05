package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button pomodoroButt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pomodoroButt = findViewById(R.id.startButton);
        pomodoroButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PomodoroTimer.class);
                intent.putExtra("StudyTime", (long)(0.1 * 60 * 1000));
                intent.putExtra("RestTime", (long)(0.12 * 60 * 1000));

                startActivity(intent);
            }
        });
    }
}
