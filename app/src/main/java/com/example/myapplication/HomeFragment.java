package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button pomodoroButt = view.findViewById(R.id.startButton);
        pomodoroButt.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PomodoroTimer.class);
            intent.putExtra("StudyTime", (long)(1 * 60 * 1000));
            intent.putExtra("RestTime", (long)(0.5 * 60 * 1000));

            startActivity(intent);
        });

        return view;
    }
}