package hu.uniobuda.nik.androgamers.main_menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import hu.uniobuda.nik.androgamers.R;

public class HelpActivity extends Activity {

    private Button app1Button;
    private Button app2Button;
    private Button app3Button;
    private Button app4Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        app1Button = (Button) findViewById(R.id.help_app1);
        app1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HelpActivity.this);
                builder.setMessage(R.string.dialog_app1_message)
                        .setTitle(R.string.dialog_app1_title)
                        .setCancelable(false)
                        .setPositiveButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        app2Button = (Button) findViewById(R.id.help_app2);
        app2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HelpActivity.this);
                builder.setMessage(R.string.dialog_app2_message)
                        .setTitle(R.string.dialog_app2_title)
                        .setCancelable(false)
                        .setPositiveButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        app3Button = (Button) findViewById(R.id.help_app3);
        app3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HelpActivity.this);
                builder.setMessage(R.string.dialog_app3_message)
                        .setTitle(R.string.dialog_app3_title)
                        .setCancelable(false)
                        .setPositiveButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        app4Button = (Button) findViewById(R.id.help_app4);
        app4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HelpActivity.this);
                builder.setMessage(R.string.dialog_app4_message)
                        .setTitle(R.string.dialog_app4_title)
                        .setCancelable(false)
                        .setPositiveButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }
}
