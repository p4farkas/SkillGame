package com.oenik.bir.skillgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //RelativeLayout screen = (RelativeLayout)findViewById(R.id.relative_layout_main);
        //TransitionDrawable transition = (TransitionDrawable)screen.getBackground();
        //transition.startTransition(500);

        RelativeLayout screen = (RelativeLayout)findViewById(R.id.relative_layout_main);
        AnimationDrawable backgroundAnimation = (AnimationDrawable) screen.getBackground();
        backgroundAnimation.start();


        Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent connectActivity = new Intent(MainActivity.this, ConnectActivity.class);
                startActivity(connectActivity);
            }
        });

        Button infoButton = (Button) findViewById(R.id.info_button);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.dialog_message)
                        .setTitle(R.string.dialog_title)
                        .setCancelable(false)
                        .setPositiveButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
