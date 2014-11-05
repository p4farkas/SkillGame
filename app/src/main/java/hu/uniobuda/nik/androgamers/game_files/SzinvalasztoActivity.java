package hu.uniobuda.nik.androgamers.game_files;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import hu.uniobuda.nik.androgamers.R;

interface INextGame{
    void NextGame();
}

public class SzinvalasztoActivity extends Activity implements INextGame {

    public void NextGame() {
        Intent intent = new Intent(SzinvalasztoActivity.this, RajzolgatoActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_szinvalaszto);

        SzinvalasztoView.setNext_game(this);
        Toast.makeText(this, GameAbstract.SZINVALASZTO_TEXT, Toast.LENGTH_LONG).show();
    }
}
