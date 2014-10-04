package com.oenik.bir.skillgame;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;


public class ShootingActivity extends Activity {

    private long startTime;
    boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shooting);

        Random r = new Random();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView iv = (ImageView) findViewById(R.id.shooting_image);
                iv.setImageResource(R.drawable.shooting_man);
                startTime = System.currentTimeMillis();
                started = true;
            }
        }, r.nextInt(15000));


        RelativeLayout shootingActivity = (RelativeLayout) findViewById(R.id.relative_layout_shooting);
        shootingActivity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                TextView shootingScore = (TextView) findViewById(R.id.shooting_score_label);
                if (!started) shootingScore.setText("FAILED");
                else {
                    long endTime = System.currentTimeMillis() - startTime;
                    shootingScore.setText(Long.toString(endTime));
                }
                //next game
                return false;
            }
        });
    }
}
