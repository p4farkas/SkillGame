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

    public static void showResult(Context context)
    {
//        Intent intent = new Intent(context, ??? );
//        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solve_it);
        first = (Button) findViewById(R.id.first_answer);
        second = (Button) findViewById(R.id.second_answer);
        third = (Button) findViewById(R.id.third_answer);

        Toast.makeText(this, "Oldd meg az egyenletet!", Toast.LENGTH_LONG).show();

        setButtons();
    }

    public void setButtons(){
        SharedPreferences sPrefs = getSharedPreferences("solveItButtons", Context.MODE_PRIVATE);
        first.setText(sPrefs.getString("firstText", ""));
        first.setTag(sPrefs.getString("firstTag", ""));
        first.setOnClickListener(onClickListenerSolveIt);
        second.setText(sPrefs.getString("secondText", ""));
        second.setTag(sPrefs.getString("secondTag", ""));
        second.setOnClickListener(onClickListenerSolveIt);
        third.setText(sPrefs.getString("thirdText", ""));
        third.setTag(sPrefs.getString("thirdTag", ""));
        third.setOnClickListener(onClickListenerSolveIt);
    }

    View.OnClickListener onClickListenerSolveIt = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getTag()=="PASS") {
                //Toast.makeText(getBaseContext(), "PASSED!", Toast.LENGTH_LONG).show();
                SolveItView.score+=10;
            }
            else {
                //Toast.makeText(getBaseContext(), "FAILED!", Toast.LENGTH_LONG).show();
                SolveItView.score-=2;
            }
            SolveItView.solved = true;
        }
    };



}