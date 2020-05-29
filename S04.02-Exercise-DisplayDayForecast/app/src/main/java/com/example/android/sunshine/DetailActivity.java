package com.example.android.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        mTextView = findViewById(R.id.tv_display_weather_day);
        if (intent.hasExtra("weatherDay")) {
            String weatherDayMessage = intent.getStringExtra("weatherDay");
            mTextView.setText(weatherDayMessage);
        }

    }
}