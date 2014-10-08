package com.oenik.bir.skillgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Random;

public class SzinvalasztoView extends GameAbstract {

    final int ORANGE = Color.parseColor("#ffa500");
    final int PURPLE = Color.parseColor("#663399");
    final int BROWN = Color.parseColor("#f4a460");
    final int PINK = Color.parseColor("#ff69b4");
    private int[] color_codes = null;
    private String[] color_names = null;

    private int view_size_width;
    private int view_size_height;

    private Bitmap canvasBitmap;
    private Paint canvasPaint;
    private Paint textPaint;

    Random r;

    private int code_index = 0;
    private int name_index = 0;

    public SzinvalasztoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isInEditMode();

        Init();
    }

    @Override
    public void Init() {

        view_size_width = 1000;
        view_size_height = 500;

        color_codes = new int[]{Color.WHITE, Color.BLACK, Color.CYAN, Color.DKGRAY,
                Color.YELLOW, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.RED, ORANGE,
                PURPLE, BROWN, PINK};
        color_names = new String[]{"Fehér", "Fekete", "Cián", "Szürke", "Sárga", "Kék",
                "Zöld", "Magenta", "Piros", "Narancssárga", "Lila", "Barna", "Rózsaszín"};

        r = new Random();

        canvasPaint = new Paint(Paint.DITHER_FLAG);
        canvasPaint.setColor(Color.WHITE);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStrokeCap(Paint.Cap.ROUND);

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
                        postInvalidate();
                    }

                } while (current_game < GAME_COUNT);
            }
        });
    }

    @Override
    protected void GameInit() {
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        view_size_width = w;
        view_size_height = h;

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);

        if (color_codes == null || color_names == null)
            return;

        getRandomColor();

        String text = color_names[name_index];
        textPaint.setColor(color_codes[code_index]);

        textPaint.setTextSize(view_size_width / 9);
        PointF textpoint = GetTextPoint(text);
        canvas.drawText(text, textpoint.x, textpoint.y, textPaint);

        if (time_thread.getState() == Thread.State.NEW)
            time_thread.start();
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                GetResult(); //Minden érintésnél kiértékeljük az eredményt
                break;
            default:
                return false;
        }

        return true;
    }

    //A felirat színének meghatározása
    private void getRandomColor() {
        int length = color_codes.length;

        code_index = r.nextInt(length);
        int max = code_index+1 >=length ? length : code_index+1;
        int min = code_index-1 < 0 ? 0 : code_index-1;
        name_index = r.nextInt(max-min)+min; //A 33% valószínűség a helyes színre
    }

    //A felirat helyének meghatározása
    private PointF GetTextPoint(String text) {
        Rect rect = new Rect();

        textPaint.getTextBounds(text, 0, text.length(), rect);
        float x = view_size_width / 2 - rect.width() / 2;
        float y = view_size_height / 2;

        return new PointF(x, y);
    }

    //Az eredmény kiértékelése
    @Override
    public void GetResult() {
        if (name_index == code_index)
            Log.i("Szinvalaszto", "TALÁLT");
        else
            Log.i("Szinvalaszto", "NEM TALÁLT");

        current_game++;
        old_time = System.currentTimeMillis();
        if (current_game < GAME_COUNT)
            GameInit();
    }
}
