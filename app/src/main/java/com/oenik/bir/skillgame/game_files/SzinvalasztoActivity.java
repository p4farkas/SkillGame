package com.oenik.bir.skillgame.game_files;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.oenik.bir.skillgame.R;

public class SzinvalasztoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_szinvalaszto);

        Toast.makeText(this, "Koppints, ha a felirat szövege és színe megegyezik!", Toast.LENGTH_LONG).show();
    }
}