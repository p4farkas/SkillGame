package com.oenik.bir.skillgame.game_files;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oenik.bir.skillgame.R;

import java.util.Random;


public class ShootingActivity extends Activity {

    private long startTime;
    private boolean started = false;
    private View root;
    private Handler back_handler;
    private Vibrator vibrator;

    public static void NextGame(Context context)
    {
        Intent intent = new Intent(context, SolveItActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shooting);

        Toast.makeText(this, "Te légy a gyorsabb párbajozó!", Toast.LENGTH_LONG).show();

        Random r = new Random();

        back_handler = new Handler();
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView iv = (ImageView) findViewById(R.id.shooting_image);
                iv.setImageResource(R.drawable.front_gun);
                View dummyView = findViewById(R.id.shooting_image);
                root = dummyView.getRootView();
                root.setBackgroundColor(Color.RED);
                startTime = System.currentTimeMillis();
                started = true;

                //Shooting...
                new Thread(new Runnable() {
                    long start = System.currentTimeMillis();
                    boolean run = true;

                    @Override
                    public void run() {
                        while (run) {
                            long current = System.currentTimeMillis();
                            if ((current - start) >= 50) {
                                run = false;
                                back_handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        root.setBackgroundColor(Color.WHITE);
                                        if(vibrator!=null)
                                        vibrator.vibrate(100);
                                    }
                                });
                            }
                        }
                    }

                }).start();
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
