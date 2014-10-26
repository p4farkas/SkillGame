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
    }


}
