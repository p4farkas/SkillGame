package hu.uniobuda.nik.androgamers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import hu.uniobuda.nik.androgamers.game_files.GameAbstract;
import hu.uniobuda.nik.androgamers.main_menu.DataBaseAR;
import hu.uniobuda.nik.androgamers.main_menu.MainActivity;

interface IFinalResult {
    void getFinalPoint(final int point);
}

public class ResultActivity extends Activity implements IFinalResult {

    static TextView pointtext2;
    static TextView result_text;
    private static int final_point2 = 0;
    private static int final_point = 0;
    TextView pointtext;
    Button new_game_button;
    private boolean point_set = false;

    @Override
    public void getFinalPoint(final int point) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (point_set)
                    return;

                final_point2 = point;
                pointtext2.setText("Játékostárs: " + final_point2 + " pont");

                if (final_point > final_point2)
                    result_text.setText("Gratulálunk, nyertél!");
                else if (final_point < final_point2)
                    result_text.setText("Sajnos nem nyertél!");
                else
                    result_text.setText("Döntetlen!");

                point_set = true;
                new_game_button.setEnabled(true);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        new_game_button.setEnabled(false);

        ClientThread.setFinal_result(this);
        ServerThread.setFinal_result(this);

        pointtext = (TextView) findViewById(R.id.final_result1);
        pointtext2 = (TextView) findViewById(R.id.final_result2);
        result_text = (TextView) findViewById(R.id.result_text);
        new_game_button = (Button) findViewById(R.id.new_game_button);
        new_game_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Connection.CloseConnection();

                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final_point = GameAbstract.getFinalPoint();
        pointtext.setText("Én: " + String.valueOf(final_point) + " pont");

        DataBaseAR dbAR = new DataBaseAR(this);
        dbAR.insertHighScore(final_point);

        Connection.SendMessage("POINT:" + final_point);

        Toast.makeText(ResultActivity.this, "Várakozás a játékostárs eredményére...", Toast.LENGTH_LONG).show();
    }
}
