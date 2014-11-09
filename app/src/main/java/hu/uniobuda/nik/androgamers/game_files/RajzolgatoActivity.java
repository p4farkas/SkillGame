package hu.uniobuda.nik.androgamers.game_files;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import hu.uniobuda.nik.androgamers.R;

public class RajzolgatoActivity extends Activity {

    public static void NextGame(Context context) {
        Intent intent = new Intent(context, ShootingActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rajzolgato);

        Toast.makeText(this, GameAbstract.RAJZOLGATO_TEXT, Toast.LENGTH_LONG).show();
    }
}
