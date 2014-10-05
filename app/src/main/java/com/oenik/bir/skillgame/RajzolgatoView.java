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

    private Paint drawPaint;
    private Paint pointPaint;
    private Paint out_pointPaint;
    private Paint canvasPaint;
    private Paint rectPaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private int back_color;

    ResultUpdate result_update;

    private float[] draw_points;

    static final int POINT_LENGTH = 50;

    private int view_size_width;
    private int view_size_height;

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
        view_size_width = 1000;
        view_size_height = 500;

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

        draw_points = new float[4];

        r = new Random();
        points = new Point[POINT_LENGTH];
        out_indices = new boolean[POINT_LENGTH];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        view_size_width = w;
        view_size_height = h;

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

        points = GetRandomPoints(r, view_size_width, view_size_height, POINT_LENGTH);
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
                draw_points[0] = touchX;
                draw_points[1] = touchY;
                break;
            case MotionEvent.ACTION_MOVE:
                //Végpont mentése
                draw_points[2] = touchX;
                draw_points[3] = touchY;
                drawCanvas.drawColor(back_color);
                drawCanvas.drawRect(draw_points[0], draw_points[1], draw_points[2], draw_points[3], drawPaint);
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

    @Override
    public void GetResult()
    {
        Rect bounding_rect = getBoundingRect(points);
        int bounding_area = bounding_rect.width() * bounding_rect.height();

        Rect draw_rect = getDrawRect(draw_points);
        int draw_area = draw_rect.width() * draw_rect.height();

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
        if (result_update != null)
            result_update.onResultUpdate(bounding_area, draw_area, faults);
    }

    //A rajzolt téglalapból Rect típust csinál
    private Rect getDrawRect(float[] points)
    {
        if (points[0]<points[2] && points[1]>points[3])
            return new Rect((int)points[0], (int)points[3], (int)points[2], (int)points[1]);
        else if (points[0]>points[2] && points[1]>points[3])
            return new Rect((int)points[2], (int)points[3], (int)points[0], (int)points[1]);
        else if (points[0]>points[2] && points[1]<points[3])
            return new Rect((int)points[2], (int)points[1], (int)points[0], (int)points[3]);
        else
            return new Rect((int)points[0], (int)points[1], (int)points[2], (int)points[3]);
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
