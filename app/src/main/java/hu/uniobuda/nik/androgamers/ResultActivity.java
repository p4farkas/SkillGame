package hu.uniobuda.nik.androgamers;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import hu.uniobuda.nik.androgamers.game_files.GameAbstract;
import hu.uniobuda.nik.androgamers.main_menu.DataBaseAR;

public class ResultActivity extends Activity {

    static TextView pointtext2;
    private static int final_point2 = 0;

    TextView pointtext;
    private int final_point = 0;

    public static void getFinalPoint(int point) {
        final_point2 = point;
        pointtext2.setText("István " + final_point2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        pointtext = (TextView) findViewById(R.id.final_result1);
        pointtext2 = (TextView) findViewById(R.id.final_result2);

        final_point = GameAbstract.getFinalPoint();
        pointtext.setText("Pista: " + String.valueOf(final_point));

        DataBaseAR dbAR = new DataBaseAR(this);
        dbAR.insertHighScore(final_point);

        Connection.SendMessage("POINT:" + final_point);
    }
}
