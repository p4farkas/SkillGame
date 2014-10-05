package com.oenik.bir.skillgame;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public class ConnectActivity extends Activity {

    private Button host_button;
    private Button client_button;
    private EditText host_port_text;
    private EditText client_port_text;
    private EditText client_ip_text;

    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        Init();
    }

    private void Init() {

        host_port_text = (EditText) findViewById(R.id.port_number_host_input);
        client_port_text = (EditText) findViewById(R.id.port_number_client_input);
        client_ip_text = (EditText) findViewById(R.id.ip_address_input);

        host_button = (Button) findViewById(R.id.host_button);
        host_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HostConnect();
            }
        });

        client_button = (Button) findViewById(R.id.client_button);
        client_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClientConnect();
            }
        });
    }

    private void HostConnect() {
        int port = 8888;
        try {
            port = Integer.parseInt(host_port_text.getText().toString());
        } catch (NumberFormatException e) {
        }

        connection = new Connection(Connection.CONNECTION_TYPE.SERVER, null,
                port, ConnectActivity.this);

        //dialog = ProgressDialog.show(ConnectActivity.this, "",
        //        "Várakozás a játékostársra...", true);
    }

    private void ClientConnect() {
        String ip = client_ip_text.getText().toString().equals("") ? "192.168.0.192" :
                client_ip_text.getText().toString();
        int port = 8888;
        try {
            port = Integer.parseInt(client_port_text.getText().toString());
        } catch (NumberFormatException e) {
        }

        connection = new Connection(Connection.CONNECTION_TYPE.CLIENT,
                ip, port, ConnectActivity.this);

        //dialog = ProgressDialog.show(ConnectActivity.this, "",
        //        "Csatlakozás a játékostárshoz...", true);
    }

    public static void ShowPlayerDialog(final Bitmap bitmap, final Context context) {
        Looper.prepare();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.player_dialog);
                dialog.setTitle("Player01");
                ImageView image = (ImageView) dialog.findViewById(R.id.image);
                image.setImageBitmap(bitmap);
                dialog.show();
            }
        });

        Looper.loop();
    }
}
