package hu.uniobuda.nik.androgamers.game_files;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import hu.uniobuda.nik.androgamers.R;

public class ShootingView extends GameAbstract {

    static int score;

    private static List<Integer> gameRound;
    private static Random r = new Random();
    private static INextGame next_game;
    private Handler handler;
    private Paint scorePaint;
    private Context context;
    private Bitmap bg;
    private Handler back_handler;
    private Vibrator vibrator;
    private long startTime;
    private boolean started = false;
    private boolean thread_run = true;

    public ShootingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        scorePaint = new Paint();
        this.context = context;

        Init();
    }

    public static void setNext_game(INextGame next_game) {
        ShootingView.next_game = next_game;
    }

    //Processing pregenerated GameInit parameters
    public static boolean setInitParameters(String line) {

        String[] tokens = line.split(":");

        if (!tokens[0].equals(GameAbstract.PARBAJOZO_NAME))
            return false;

        gameRound = new ArrayList<Integer>(GAME_COUNT);

        int length = tokens.length;
        for (int i = 1; i < length; i++) {
            gameRound.add(Integer.parseInt(tokens[i]));
        }

        return true;
    }

    //GameInit parameters generation
    public static String getGameInitString() {

        StringBuilder builder = new StringBuilder();
        builder.append(GameAbstract.PARBAJOZO_NAME).append(":");
        for (int i = 0; i < GAME_COUNT; i++) {
            int delayTime = r.nextInt(15000);
            builder.append(delayTime).append(":");
        }
        return builder.toString();
    }

    //set final result in GameAbstract
    @Override
    public void GetResult() {
        game_points[2] = score;
    }

    //startup initialization
    @Override
    public void Init() {
//        setInitParameters(ShootingView.getGameInitString());
        scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);
        scorePaint.setTextSize(18);
        scorePaint.setTextAlign(Paint.Align.LEFT);

        score = 0;
        back_handler = new Handler();
        handler = new Handler();
        time_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    if (!thread_run)
                        return;

                    if (started) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        GameInit();
                        bg = BitmapFactory.decodeResource(context.getResources(), R.drawable.cowboy);
                        postInvalidate();
                    }
                } while (current_game < GAME_COUNT && thread_run);
                GetResult();

                if (next_game != null)
                    next_game.NextGame();
            }
        });
        time_thread.start();

        bg = BitmapFactory.decodeResource(context.getResources(), R.drawable.cowboy);
        GameInit();
    }

    //next game round initialization
    @Override
    protected void GameInit() {
        started = false;

//        int delayTime = r.nextInt(15000);

        //get game parameters predefined by setInitParameters
        int delayTime = gameRound.get(0);
        gameRound.remove(0);

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bg = BitmapFactory.decodeResource(context.getResources(), R.drawable.front_gun);
                postInvalidate();
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
        }, delayTime);
        current_game++;
    }

    //creating view for game
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        //draw background
        int x = (width - bg.getWidth()) >> 1;
        int y = (height - bg.getHeight()) >> 1;
        canvas.drawBitmap(bg, x, y, null);

        canvas.drawText("Eredmény: " + String.valueOf(getFinalPoint() + score), 10, 20, scorePaint);
    }

    //detecting shooting
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event != null) {
            //shoot on time
            if (started) {
                long endTime = System.currentTimeMillis() - startTime;
                score = 1000 - (int) endTime;
            }
            //early shot
            else {
                started = false;
                GameInit();
                score -= 250;
                postInvalidate();
            }
        }
        return super.onTouchEvent(event);
    }
}
