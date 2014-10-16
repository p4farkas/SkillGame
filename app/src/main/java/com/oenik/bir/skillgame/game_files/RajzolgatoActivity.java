package com.oenik.bir.skillgame.game_files;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.oenik.bir.skillgame.R;

public class RajzolgatoActivity extends Activity {

    public static void NextGame(Context context)
    {
        Intent intent = new Intent(context, ShootingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rajzolgato);

        Toast.makeText(this, "Rajzold meg a legkisebb befoglaló téglalapot!", Toast.LENGTH_LONG).show();
    }
}
