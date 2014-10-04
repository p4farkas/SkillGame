package com.oenik.bir.skillgame;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

interface ResultUpdate {

    void onResultUpdate(int bounding_area, int draw_area, int faults);
}

public class DrawingActivity extends Activity implements ResultUpdate {

    private DrawingGameView drawing_view;
    private TextView textview_eredmeny;
    private TextView textview_bunti;
    private TextView textview_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        drawing_view = (DrawingGameView) findViewById(R.id.drawing_view);
        textview_eredmeny = (TextView) findViewById(R.id.textView);
        textview_bunti = (TextView) findViewById(R.id.textView2);
        textview_score = (TextView) findViewById(R.id.textView3);

        drawing_view.setResult_update(this);

        setColor();

        drawing_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                drawing_view.onTouchEvent(motionEvent);

                return true;
            }
        });
    }

    public void onResultUpdate(int bounding_area, int draw_area, int faults)
    {
        textview_eredmeny.setText(String.valueOf(bounding_area) + " - " + String.valueOf(draw_area));
        textview_bunti.setText(String.valueOf(faults));
        textview_score.setText(String.valueOf(getScore(bounding_area, draw_area, faults)));
    }

    private int getScore(int bounding_area, int draw_area, int faults)
    {
        int FAULT_MUL = 10;

        int score = 0;
        if (draw_area != 0)

            score = 1000 - Math.abs(draw_area-bounding_area)/100 - faults * FAULT_MUL;
        return (score<0) ? 0 : score;
    }

    private void setColor()
    {
        final int ORANGE = Color.parseColor("#ffa500");
        final int PURPLE = Color.parseColor("#663399");
        final int BROWN = Color.parseColor("#f4a460");
        final int PINK = Color.parseColor("#ff69b4");

        int[] color_codes = new int[]{Color.WHITE, Color.BLACK, Color.CYAN, Color.DKGRAY,
                Color.YELLOW, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.RED, ORANGE,
                PURPLE, BROWN, PINK};
        String[] color_names = new String[]{"Fehér", "Fekete", "Cián", "Szürke", "Sárga", "Kék",
                "Zöld", "Magenta", "Piros", "Narancssárga", "Lila", "Barna", "Rózsaszín"};

        Random r = new Random();
        int index = r.nextInt(color_codes.length);

        textview_score.setTextColor(color_codes[index]);
        textview_score.setText(color_names[index]);
        textview_score.setTextSize(30);
    }
}