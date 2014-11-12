package hu.uniobuda.nik.androgamers.main_menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import hu.uniobuda.nik.androgamers.R;


public class MainActivity extends Activity {

    private Button startButton;
    private Button profileButton;
    private Button infoButton;
    private Button helpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //RelativeLayout screen = (RelativeLayout)findViewById(R.id.relative_layout_main);
        //TransitionDrawable transition = (TransitionDrawable)screen.getBackground();
        //transition.startTransition(500);

        //RelativeLayout screen = (RelativeLayout)findViewById(R.id.relative_layout_main);
        //AnimationDrawable backgroundAnimation = (AnimationDrawable) screen.getBackground();
        //backgroundAnimation.start();


        startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent connectActivity = new Intent(MainActivity.this, ConnectActivity.class);
                startActivity(connectActivity);
            }
        });

        profileButton = (Button) findViewById(R.id.profil_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileActivity = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profileActivity);
            }
        });

        infoButton = (Button) findViewById(R.id.info_button);
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

        helpButton = (Button) findViewById(R.id.help_button);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent helpActivity = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(helpActivity);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
