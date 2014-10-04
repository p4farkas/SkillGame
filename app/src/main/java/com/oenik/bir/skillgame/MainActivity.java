package com.oenik.bir.skillgame;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

interface ResultUpdate {

    void onResultUpdate(int bounding_area, int draw_area, int faults);
}

public class MainActivity extends Activity implements ResultUpdate {

    private SzinvalasztoView drawing_view;
    private TextView textview_eredmeny;
    private TextView textview_bunti;
    private TextView textview_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawing_view = (SzinvalasztoView) findViewById(R.id.drawing_view);
        textview_eredmeny = (TextView) findViewById(R.id.textView);
        textview_bunti = (TextView) findViewById(R.id.textView2);
        textview_score = (TextView) findViewById(R.id.textView3);

        //drawing_view.setResult_update(this);

        drawing_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                drawing_view.onTouchEvent(motionEvent);

                return true;
            }
        });

        //Connection connection = new Connection(Connection.CONNECTION_TYPE.SERVER);
    }

    public void onResultUpdate(int bounding_area, int draw_area, int faults) {
        textview_eredmeny.setText(String.valueOf(bounding_area) + " - " + String.valueOf(draw_area));
        textview_bunti.setText(String.valueOf(faults));
        textview_score.setText(String.valueOf(getScore(bounding_area, draw_area, faults)));
    }

    private int getScore(int bounding_area, int draw_area, int faults) {
        int FAULT_MUL = 10;

        int score = 0;
        if (draw_area != 0)

            score = 1000 - Math.abs(draw_area - bounding_area) / 100 - faults * FAULT_MUL;
        return (score < 0) ? 0 : score;
    }
}