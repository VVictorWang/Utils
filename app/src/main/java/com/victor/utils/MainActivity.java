package com.victor.utils;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.victor.utils.view.CircleProgressView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final CircleProgressView circleProgressView = (CircleProgressView) findViewById(R.id.ci);
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                circleProgressView.setProgress(20);
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(mRunnable, 2000);
    }

}
