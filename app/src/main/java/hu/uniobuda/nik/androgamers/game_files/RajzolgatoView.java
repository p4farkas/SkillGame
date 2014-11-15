package hu.uniobuda.nik.androgamers.game_files;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.Arrays;
import java.util.Random;

public class RajzolgatoView extends GameAbstract {

    private static final int POINT_LENGTH = 50;
    static Point[] points;
    private static Random rand = new Random();
    private static INextGame next_game;
    float start_x = 0;
    float start_y = 0;
    float end_x = 0;
    float end_y = 0;
    Point[] actual_points;
    boolean[] out_indices;
    private Paint drawPaint;
    private Paint pointPaint;
    private Paint out_pointPaint;
    private Paint canvasPaint;
    private Paint rectPaint;
    private Paint scorePaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private int back_color;
    private Rect bounding_rect;
    private Rect draw_rect;
    private int final_point = 0;

    private Context context;

    public RajzolgatoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.isInEditMode();

        Init();
    }

    public static void setNext_game(INextGame next_game) {
        RajzolgatoView.next_game = next_game;
    }

    //Kezdőértékek beállítása a paraméterben átadott sorból
    public static boolean setInitParameters(String line) {
        String[] tokens = line.split(":");

        if (!tokens[0].equals(GameAbstract.RAJZOLGATO_NAME))
            return false;

        points = new Point[POINT_LENGTH * GAME_COUNT];

        int point_index = 0;
        for (int i = 1; i < tokens.length; i += 2) {
            if (point_index < points.length) {
                points[point_index++] = new Point(Integer.parseInt(tokens[i]), Integer.parseInt(tokens[i + 1]));
            }
        }

        return true;
    }

    //A játék kezdeti paraméterei
    public static String getGameInitString(int view_size_height, int view_size_width) {
        StringBuilder builder = new StringBuilder();
        builder.append(GameAbstract.RAJZOLGATO_NAME).append(":");

        double gauss_h;
        for (int k = 0; k < GAME_COUNT; k++) {
            for (int i = 0; i < POINT_LENGTH; i++) {
                gauss_h = Math.abs(rand.nextGaussian() * 100); //A pontok horizontális szórodása miatt
                gauss_h = ((int) gauss_h > (view_size_width * 0.1)) ? 0 : gauss_h;
                int x = rand.nextInt(view_size_width - view_size_width / 2) + view_size_width / 4 + (int) gauss_h;
                int y = rand.nextInt(view_size_height - view_size_height / 2) + view_size_height / 4;
                builder.append(x).append(":").append(y).append(":");
            }
        }

        return builder.toString();
    }

    @Override
    public void Init() {
        back_color = Color.WHITE;

        drawPaint = new Paint();
        drawPaint.setColor(Color.RED);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(6);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setColor(Color.BLACK);

        out_pointPaint = new Paint();
        out_pointPaint.setAntiAlias(true);
        out_pointPaint.setColor(Color.BLUE);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
        canvasPaint.setColor(Color.WHITE);

        rectPaint = new Paint();
        rectPaint.setColor(Color.DKGRAY);
        rectPaint.setAntiAlias(true);
        rectPaint.setStrokeWidth(3);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeJoin(Paint.Join.ROUND);
        rectPaint.setStrokeCap(Paint.Cap.ROUND);
        rectPaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));

        scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);
        scorePaint.setTextSize(18);
        scorePaint.setTextAlign(Paint.Align.LEFT);

        out_indices = new boolean[POINT_LENGTH];

        actual_points = new Point[POINT_LENGTH];

//        old_time = System.currentTimeMillis();
//
//        //Háttérszálon figyeljük az időt
//        time_thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                do {
//                    if (!thread_run)
//                        break;
//
//                    long current_time = System.currentTimeMillis();
//
//                    if ((current_time - old_time) >= GAME_MILLIS) {
//                        current_game++;
//                        old_time = current_time;
//                        GameInit();
//                    }
//
//                } while (current_game < GAME_COUNT - 1 && thread_run);
//
//                if (!thread_run)
//                    return;
//
//                game_points[1] = final_point;
//
//                thread_run2 = false;
//                RajzolgatoActivity.NextGame(context);
//            }
//        });

        GameInit();

//        time_thread.start();
    }

    @Override
    protected void GameInit() {
        int start = (current_game == 0) ? 0 : current_game * POINT_LENGTH;
        actual_points = Arrays.copyOfRange(points, start, (current_game + 1) * POINT_LENGTH);
        start_x = 0;
        start_y = 0;
        end_x = 0;
        end_y = 0;
        draw_rect = null;
        bounding_rect = null;
        out_indices = new boolean[POINT_LENGTH];
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        view_size_width = w;
        view_size_height = h;

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawCanvas = canvas;
        drawCanvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);

        canvas.drawText("Eredmény: " + String.valueOf(getFinalPoint() + final_point), 10, 20, scorePaint);

        if (actual_points.length == 0)
            return;

        //Random pontok kirajzolása
        for (int i = 0; i < POINT_LENGTH; i++) {
            drawCanvas.drawCircle((float) actual_points[i].x, (float) actual_points[i].y, 5, (!out_indices[i]) ? pointPaint : out_pointPaint);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Kezdőpont mentése
                start_x = touchX;
                start_y = touchY;
                break;
            case MotionEvent.ACTION_MOVE:
                //Végpont mentése
                end_x = touchX;
                end_y = touchY;

                drawCanvas.drawColor(back_color);
                break;
            case MotionEvent.ACTION_UP:

                //swapping
                if (end_x < start_x) {
                    float dummy = start_x;
                    start_x = end_x;
                    end_x = dummy;
                }

                //swapping
                if (end_y < start_y) {
                    float dummy = start_y;
                    start_y = end_y;
                    end_y = dummy;
                }

                drawCanvas.drawRect(start_x, start_y, end_x, end_y, drawPaint);

                GetResult();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    //Az eredmény kiértékelése
    @Override
    public void GetResult() {
        bounding_rect = getBoundingRect(points);
        int bounding_area = bounding_rect.width() * bounding_rect.height();

        draw_rect = new Rect((int) start_x, (int) start_y, (int) end_x, (int) end_y);
        int draw_area = draw_rect.width() * draw_rect.height();

        //Random pontokat befoglaló téglalap kirajzolása
        drawCanvas.drawRect(bounding_rect, rectPaint);

        //Rajzolás során a kihagyott pontok száma
        int faults = 0;
        for (int i = 0; i < POINT_LENGTH; i++) {
            if (!draw_rect.contains(actual_points[i].x, actual_points[i].y)) {
                out_indices[i] = true;
                faults++;
            }
        }

        //Hozzáadjuk a játék végső pontszámához
        final_point += getScore(bounding_area, draw_area, faults);

        current_game++;

        invalidate();

        //Várakozás két menet között...
        new Thread(new Runner()).start();
    }

    //Visszatérési értéke a random pontok legkisebb befoglaló téglalapja
    private Rect getBoundingRect(Point[] points) {
        int minleft = 0;
        int mintop = 0;
        int maxright = 0;
        int maxbottom = 0;

        for (int i = 0; i < POINT_LENGTH; i++) {
            if (points[i].x < points[minleft].x) {
                minleft = i;
            }
            if (points[i].y < points[mintop].y) {
                mintop = i;
            }
            if (points[i].x > points[maxright].x) {
                maxright = i;
            }
            if (points[i].y > points[maxbottom].y) {
                maxbottom = i;
            }
        }

        return new Rect(points[minleft].x, points[mintop].y, points[maxright].x, points[maxbottom].y);
    }

    private int getScore(int bounding_area, int draw_area, int faults) {
        int FAULT_MUL = 10;

        int score = 0;
        if (draw_area != 0)
            score = 1000 - Math.abs(draw_area - bounding_area) / 100 - faults * FAULT_MUL;

        return (score < 0) ? 0 : score;
    }

    //Várakozás
    private class Runner implements Runnable {
        long start = System.currentTimeMillis();
        boolean thread_run = true;

        @Override
        public void run() {
            while (thread_run) {
                long current = System.currentTimeMillis();
                if ((current - start) >= 1000) {
                    thread_run = false;
                    if (current_game < GAME_COUNT)
                        GameInit();
                    else {
                        thread_run = false;

                        if (next_game != null)
                            next_game.NextGame();
                    }
                }
            }
        }
    }
}
