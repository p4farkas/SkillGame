package com.oenik.bir.skillgame;

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

import java.util.Random;

public class RajzolgatoView extends GameAbstract {

    static final int POINT_LENGTH = 50;

    private Paint drawPaint;
    private Paint pointPaint;
    private Paint out_pointPaint;
    private Paint canvasPaint;
    private Paint rectPaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private int back_color;

    ResultUpdate result_update;

    float start_x = 0;
    float start_y = 0;
    float end_x = 0;
    float end_y = 0;

    Point[] points;
    boolean[] out_indices;

    Random r;

    public RajzolgatoView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.isInEditMode();

        Init();
    }

    public void setResult_update(ResultUpdate result_update) {
        this.result_update = result_update;
    }

    @Override
    public void Init()
    {
        back_color = Color.parseColor("#D3D3D3");

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
        rectPaint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));

        r = new Random();
        points = new Point[POINT_LENGTH];
        out_indices = new boolean[POINT_LENGTH];

        old_time = System.currentTimeMillis();

        //Háttérszálon figyeljük az időt
        time_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    long current_time = System.currentTimeMillis();

                    if ((current_time - old_time) >= GAME_MILLIS) {
                        current_game++;
                        old_time = current_time;
                        GameInit();
                    }

                } while (current_game < GAME_COUNT);
            }
        });

        GameInit();

        time_thread.start();
    }

    @Override
    protected void GameInit()
    {
        points = GetRandomPoints(r, view_size_width, view_size_height, POINT_LENGTH);
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

    //A függvény a paraméterben kapott téglalapban szórja szét a random pontokat
    private Point[] GetRandomPoints(Random r, int view_size_width, int view_size_height, int length)
    {
        Point[] points = new Point[length];

        //horizontális irányban egy kicsit elhúzza a random gauss
        double gauss_h;
        for (int i=0;i<length;i++) {
            gauss_h = Math.abs(r.nextGaussian() * 100);
            gauss_h = ((int) gauss_h > (view_size_width * 0.1)) ? 0 : gauss_h;
            points[i] = new Point(r.nextInt(view_size_width - view_size_width / 2) + view_size_width / 4 + (int)gauss_h, r.nextInt(view_size_height - view_size_height / 2) + view_size_height / 4);
        }

        return points;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);

        //Random pontok kirajzolása
        for(int i=0;i<POINT_LENGTH;i++)
        {
            canvas.drawCircle((float) points[i].x, (float) points[i].y, 5, (!out_indices[i]) ? pointPaint : out_pointPaint);
        }
    }

    public boolean onTouchEvent(MotionEvent event)
    {
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

                //swapping
                if (end_x < start_x){
                    float dummy = start_x;
                    start_x= end_x;
                    end_x = dummy;
                }

                //swapping
                if (end_y < start_y){
                    float dummy = start_y;
                    start_y= end_y;
                    end_y = dummy;
                }

                drawCanvas.drawColor(back_color);
                drawCanvas.drawRect(start_x, start_y, end_x, end_y, drawPaint);
                break;
            case MotionEvent.ACTION_UP:
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
    public void GetResult()
    {
        Rect bounding_rect = getBoundingRect(points);
        int bounding_area = bounding_rect.width() * bounding_rect.height();

        Rect draw_rect = new Rect((int)start_x, (int)start_y, (int)end_x, (int)end_y);

        //Random pontokat befoglaló téglalap kirajzolása
        drawCanvas.drawRect(bounding_rect, rectPaint);

        //Rajzolás során a kihagyott pontok száma
        int faults = 0;
        for(int i=0;i<POINT_LENGTH;i++)
        {
           if (!draw_rect.contains(points[i].x, points[i].y)) {
               out_indices[i] = true;
               faults++;
           }
        }

        //Meghívjuk az adott interfész metódusát (értesítjük az interfész implementálóit az eredményről)
        //if (result_update != null)
        //    result_update.onResultUpdate(bounding_area, draw_area, faults);

        current_game++;
        old_time = System.currentTimeMillis();
        if (current_game < GAME_COUNT)
            GameInit();
    }

    //Visszatérési értéke a random pontok legkisebb befoglaló téglalapja
    private Rect getBoundingRect (Point[] points)
    {
        int minleft = 0;
        int mintop = 0;
        int maxright = 0;
        int maxbottom = 0;

        for (int i=0;i<POINT_LENGTH;i++)
        {
            if (points[i].x<points[minleft].x){ minleft = i; }
            if (points[i].y<points[mintop].y){ mintop = i; }
            if (points[i].x>points[maxright].x){ maxright = i; }
            if (points[i].y>points[maxbottom].y){ maxbottom = i; }
        }

        return new Rect(points[minleft].x, points[mintop].y, points[maxright].x, points[maxbottom].y);
    }
}
