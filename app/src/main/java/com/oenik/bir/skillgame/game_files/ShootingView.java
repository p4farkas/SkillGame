package com.oenik.bir.skillgame.game_files;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.oenik.bir.skillgame.R;

import java.util.Random;

public class ShootingView extends GameAbstract {

    static int score;

    private static Random r = new Random();
    private Paint paint;
    private Context context;
    private Bitmap bg;
    private Handler back_handler;
    private Vibrator vibrator;
    private long startTime;
    private boolean started = false;

    public ShootingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        this.context = context;

        Init();
    }

    @Override
    public void GetResult() {

    }

    @Override
    public void Init() {
        bg = BitmapFactory.decodeResource(context.getResources(), R.drawable.gunman);
        GameInit();
    }

    @Override
    protected void GameInit() {
        back_handler = new Handler();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bg = BitmapFactory.decodeResource(context.getResources(), R.drawable.front_gun);
                getRootView().setBackgroundColor(Color.RED);
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
                                        getRootView().setBackgroundColor(Color.WHITE);
                                        if (vibrator != null)
                                            vibrator.vibrate(100);
                                    }
                                });
                            }
                        }
                    }

                }).start();
            }
        }, r.nextInt(15000));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int x = (width - bg.getWidth()) >> 1;
        int y = (height - bg.getHeight()) >> 1;
        canvas.drawBitmap(bg, x, y, null);

        paint.setColor(Color.BLACK);
        paint.setTextSize(18);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Score: " + Integer.toString(score), 0, 0 + paint.getTextSize(), paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event!=null) {
            if (!started) ShootingActivity.NextGame(context);
            else {
                long endTime = System.currentTimeMillis() - startTime;
                score = (int) endTime;
                ShootingActivity.NextGame(context);
            }
        }
        return super.onTouchEvent(event);
    }
}
