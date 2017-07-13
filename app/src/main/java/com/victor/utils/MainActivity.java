package com.victor.utils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


//    private com.victor.utils.view.RippleView ripple;
//    private android.widget.TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        this.text = (TextView) findViewById(R.id.text);
//        this.ripple = (RippleView) findViewById(ripple);
//        ripple.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ripple.startRipple();
//            }
//        });
//        ripple.setRippleStateListener(new RippleView.RippleAnimationListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float fraction = animation.getAnimatedFraction();
//                int value = (int) (fraction * 100);
//                text.setText(String.valueOf(value) + "%");
//            }
//
//            @Override
//            public void onAnimationStart(Animator animation) {
//                text.setText("0%");
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//            }
//        });
//    }
    }
}
