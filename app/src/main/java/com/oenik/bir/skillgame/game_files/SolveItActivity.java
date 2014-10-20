package com.oenik.bir.skillgame.game_files;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oenik.bir.skillgame.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SolveItActivity extends Activity {

    View.OnClickListener onClickListenerFailed = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView score = (TextView) findViewById(R.id.solve_it_score_label);
            score.setText("FAILED");
            //next game
        }
    };

    View.OnClickListener onClickListenerPassed = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView score = (TextView) findViewById(R.id.solve_it_score_label);
            score.setText("1");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solve_it);

        Toast.makeText(this, "Oldd meg az egyenletet!", Toast.LENGTH_LONG).show();

        //equation
        String[] equations = new String[] {"5*5+10=35", "56/7+6=14", "(91-27)/8=8"};
        Random r = new Random();
        String selected = equations[r.nextInt(3)];
        char key = selected.charAt(r.nextInt(selected.length()));

        //bad answers
        List<String> badAnswers = new ArrayList<String>();
        badAnswers.add("*");
        badAnswers.add("/");
        badAnswers.add("+");
        badAnswers.add("-");
        badAnswers.add("(");
        badAnswers.add(")");
        for (int i=0; i<10; i++){
            badAnswers.add(String.valueOf(i));
        }
        badAnswers.remove(String.valueOf(key));

        //view
        TextView tv = (TextView) findViewById(R.id.equation_label);
        selected = selected.replaceFirst(Character.toString(key),"?");
        tv.setText(selected);

        //generating randoms
        int solution = r.nextInt(3);
        int badAnswerNumber1 = r.nextInt(badAnswers.size());
        String firstBadAnswer = badAnswers.get(badAnswerNumber1);
        badAnswers.remove(badAnswers.get(badAnswerNumber1));
        int badAnswerNumber2 = r.nextInt(badAnswers.size());
        String secondBadAnswer = badAnswers.get(badAnswerNumber2);

        switch(solution) {
            case 0:
                //set solution answer for #1
                Button button1_1 = (Button) findViewById(R.id.first_answer);
                button1_1.setText(String.valueOf(key));
                button1_1.setOnClickListener(onClickListenerPassed);
                //set other answer for #2
                Button button2_1 = (Button) findViewById(R.id.second_answer);
                button2_1.setText(firstBadAnswer);
                button2_1.setOnClickListener(onClickListenerFailed);
                //set other answer for #3
                Button button3_1 = (Button) findViewById(R.id.third_answer);
                button3_1.setText(secondBadAnswer);
                button3_1.setOnClickListener(onClickListenerFailed);
                break;
            case 1:
                //set solution answer for #2
                Button button2_2 = (Button) findViewById(R.id.second_answer);
                button2_2.setText(String.valueOf(key));
                button2_2.setOnClickListener(onClickListenerPassed);
                //set other answer for #1
                Button button1_2 = (Button) findViewById(R.id.first_answer);
                button1_2.setText(firstBadAnswer);
                button1_2.setOnClickListener(onClickListenerFailed);
                //set other answer for #3
                Button button3_2 = (Button) findViewById(R.id.third_answer);
                button3_2.setText(secondBadAnswer);
                button3_2.setOnClickListener(onClickListenerFailed);
                break;
            case 2:
                //set solution answer for #3
                Button button3_3 = (Button) findViewById(R.id.third_answer);
                button3_3.setText(String.valueOf(key));
                button3_3.setOnClickListener(onClickListenerPassed);
                //set other answer for #1
                Button button1_3 = (Button) findViewById(R.id.second_answer);
                button1_3.setText(firstBadAnswer);
                button1_3.setOnClickListener(onClickListenerFailed);
                //set other answer for #2
                Button button2_3 = (Button) findViewById(R.id.first_answer);
                button2_3.setText(secondBadAnswer);
                button2_3.setOnClickListener(onClickListenerFailed);
                break;
        }
    }

}