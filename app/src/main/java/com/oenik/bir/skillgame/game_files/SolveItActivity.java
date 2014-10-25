package com.oenik.bir.skillgame.game_files;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.oenik.bir.skillgame.R;



public class SolveItActivity extends Activity {
    private Button first;
    private Button second;
    private Button third;

    public static void NextGame(Context context)
    {
        Intent intent = new Intent(context, SolveItActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solve_it);
        first = (Button) findViewById(R.id.first_answer);
        second = (Button) findViewById(R.id.second_answer);
        third = (Button) findViewById(R.id.third_answer);
        SharedPreferences sPrefs = getSharedPreferences("solveItButtons", Context.MODE_PRIVATE);
        first.setText(sPrefs.getString("firstText", ""));
        first.setOnClickListener(sPrefs.getString("firstListener", "") == "onClickListenerPassed" ? onClickListenerPassed : onClickListenerFailed);
        second.setText(sPrefs.getString("secondText", ""));
        second.setOnClickListener(sPrefs.getString("secondListener", "") == "onClickListenerPassed" ? onClickListenerPassed : onClickListenerFailed);
        third.setText(sPrefs.getString("thirdText", ""));
        third.setOnClickListener(sPrefs.getString("thirdListener", "") == "onClickListenerPassed" ? onClickListenerPassed : onClickListenerFailed);
    }

    View.OnClickListener onClickListenerFailed = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(getBaseContext(), "FAILED!", Toast.LENGTH_LONG).show();
            //next game
        }
    };

    View.OnClickListener onClickListenerPassed = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(getBaseContext(), "PASSED!", Toast.LENGTH_LONG).show();
            //next game
        }
    };

}