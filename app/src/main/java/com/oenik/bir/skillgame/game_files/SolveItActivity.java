package com.oenik.bir.skillgame.game_files;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oenik.bir.skillgame.R;

import java.util.Random;


public class SolveItActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solve_it);

        Toast.makeText(this, "Oldd meg az egyenletet!", Toast.LENGTH_LONG).show();

        String[] equations = new String[] {"5*5+10=35", "56/7+6=14", "(91-27)/8=8"};
        Random r = new Random();
        String selected = equations[r.nextInt(3)];
        char key = selected.charAt(r.nextInt(selected.length()));

        TextView tv = (TextView) findViewById(R.id.equation_label);
        selected = selected.replaceFirst(Character.toString(key),"?");
        tv.setText(selected);

        int solution = r.nextInt(3);

        switch(solution) {
            case 0:
                //set solution answer for #1
                Button button1_1 = (Button) findViewById(R.id.first_answer);
                button1_1.setText(String.valueOf(key));
                button1_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView score = (TextView) findViewById(R.id.solve_it_score_label);
                        score.setText("1");
                        //next game
                    }
                });
                //set other answer for #2
                Button button2_1 = (Button) findViewById(R.id.second_answer);
                button2_1.setText("a");
                button2_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView score = (TextView) findViewById(R.id.solve_it_score_label);
                        score.setText("FAILED");
                        //next game
                    }
                });
                //set other answer for #3
                Button button3_1 = (Button) findViewById(R.id.third_answer);
                button3_1.setText("b");
                button3_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView score = (TextView) findViewById(R.id.solve_it_score_label);
                        score.setText("FAILED");
                        //next game
                    }
                });
                break;
            case 1:
                //set solution answer for #2
                Button button2_2 = (Button) findViewById(R.id.second_answer);
                button2_2.setText(String.valueOf(key));
                button2_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView score = (TextView) findViewById(R.id.solve_it_score_label);
                        score.setText("1");
                        //next game
                    }
                });
                //set other answer for #1
                Button button1_2 = (Button) findViewById(R.id.first_answer);
                button1_2.setText("a");
                button1_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView score = (TextView) findViewById(R.id.solve_it_score_label);
                        score.setText("FAILED");
                        //next game
                    }
                });
                //set other answer for #3
                Button button3_2 = (Button) findViewById(R.id.third_answer);
                button3_2.setText("b");
                button3_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView score = (TextView) findViewById(R.id.solve_it_score_label);
                        score.setText("FAILED");
                        //next game
                    }
                });
                break;
            case 2:
                //set solution answer for #3
                Button button3_3 = (Button) findViewById(R.id.third_answer);
                button3_3.setText(String.valueOf(key));
                button3_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView score = (TextView) findViewById(R.id.solve_it_score_label);
                        score.setText("1");
                        //next game
                    }
                });
                //set other answer for #1
                Button button1_3 = (Button) findViewById(R.id.second_answer);
                button1_3.setText("a");
                button1_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView score = (TextView) findViewById(R.id.solve_it_score_label);
                        score.setText("FAILED");
                        //next game
                    }
                });
                //set other answer for #2
                Button button2_3 = (Button) findViewById(R.id.first_answer);
                button2_3.setText("b");
                button2_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView score = (TextView) findViewById(R.id.solve_it_score_label);
                        score.setText("FAILED");
                        //next game
                    }
                });
                break;
        }
    }

}
