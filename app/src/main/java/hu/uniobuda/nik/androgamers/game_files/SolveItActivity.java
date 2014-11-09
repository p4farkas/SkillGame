package hu.uniobuda.nik.androgamers.game_files;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import hu.uniobuda.nik.androgamers.R;
import hu.uniobuda.nik.androgamers.ResultActivity;


public class SolveItActivity extends Activity {
    View.OnClickListener onClickListenerSolveIt = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getTag() == "PASS") {
                //Toast.makeText(getBaseContext(), "PASSED!", Toast.LENGTH_SHORT).show();
                SolveItView.score += 10;
            } else {
                //Toast.makeText(getBaseContext(), "FAILED!", Toast.LENGTH_SHORT).show();
                SolveItView.score -= 5;
            }
            SolveItView.solved = true;
        }
    };
    private Button first;
    private Button second;
    private Button third;

    public static void ShowResult(Context context) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solve_it);
        first = (Button) findViewById(R.id.first_answer);
        first.setOnClickListener(onClickListenerSolveIt);
        second = (Button) findViewById(R.id.second_answer);
        second.setOnClickListener(onClickListenerSolveIt);
        third = (Button) findViewById(R.id.third_answer);
        third.setOnClickListener(onClickListenerSolveIt);

        Toast.makeText(this, GameAbstract.SZAMOLGATO_TEXT, Toast.LENGTH_LONG).show();

        setButtons();
    }

    public void setButtons() {
        SharedPreferences sPrefs = getSharedPreferences("solveItButtons", Context.MODE_PRIVATE);
        first.setText(sPrefs.getString("firstText", ""));
        first.setTag(sPrefs.getString("firstTag", ""));
        second.setText(sPrefs.getString("secondText", ""));
        second.setTag(sPrefs.getString("secondTag", ""));
        third.setText(sPrefs.getString("thirdText", ""));
        third.setTag(sPrefs.getString("thirdTag", ""));
    }


}