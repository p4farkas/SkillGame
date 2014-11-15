package hu.uniobuda.nik.androgamers.game_files;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import hu.uniobuda.nik.androgamers.R;

public class SzinvalasztoActivity extends Activity implements INextGame {

    private static Handler handler;

    public void NextGame() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SzinvalasztoActivity.this, RajzolgatoActivity.class);
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_szinvalaszto);

        handler = new Handler(Looper.getMainLooper());

        SzinvalasztoView.setNext_game(this);
        Toast.makeText(this, GameAbstract.SZINVALASZTO_TEXT, Toast.LENGTH_LONG).show();
    }
}
