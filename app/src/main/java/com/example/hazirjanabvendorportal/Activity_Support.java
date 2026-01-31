package com.example.hazirjanabvendorportal;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Support extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        toolbar = Toolbar.getInstance();
        toolbar.setup(this);
    }
}