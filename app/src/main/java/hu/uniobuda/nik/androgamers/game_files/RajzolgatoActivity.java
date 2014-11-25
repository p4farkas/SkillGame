package hu.uniobuda.nik.androgamers.game_files;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import hu.uniobuda.nik.androgamers.R;

public class RajzolgatoActivity extends Activity implements INextGame {

    private static Handler handler;

    //Következő játék
    @Override
    public void NextGame() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(RajzolgatoActivity.this, ShootingActivity.class);
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rajzolgato);

        handler = new Handler(Looper.getMainLooper());

        RajzolgatoView.setNext_game(this);

        Toast.makeText(this, GameAbstract.RAJZOLGATO_TEXT, Toast.LENGTH_LONG).show();
    }
}
