package hu.uniobuda.nik.androgamers.main_menu;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.IOException;

import hu.uniobuda.nik.androgamers.ClientThread;
import hu.uniobuda.nik.androgamers.Connection;
import hu.uniobuda.nik.androgamers.R;
import hu.uniobuda.nik.androgamers.ServerThread;
import hu.uniobuda.nik.androgamers.game_files.GameAbstract;
import hu.uniobuda.nik.androgamers.game_files.RajzolgatoView;
import hu.uniobuda.nik.androgamers.game_files.ShootingView;
import hu.uniobuda.nik.androgamers.game_files.SolveItView;
import hu.uniobuda.nik.androgamers.game_files.SzinvalasztoActivity;
import hu.uniobuda.nik.androgamers.game_files.SzinvalasztoView;

public class ConnectActivity extends Activity implements IClientConnected, IServerConnected {

    private static Dialog dialog;
    private Button host_button;
    private Button client_button;
    private EditText host_port_text;
    private EditText client_port_text;
    private EditText client_ip_text;
    private Connection connection;

    //Szerver esetén ez a callback, hogy a másik játékos csatlakozott
    public void ClientConnected(final ServerThread.PlayerData playerData, final Context context) {
        Looper.prepare();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();

                //Megjelenítjük a játékostárs felhasználónevét és képét
                final Dialog player_dialog = new Dialog(context);
                player_dialog.setContentView(R.layout.player_dialog);
                player_dialog.setTitle(playerData.getName());
                ImageView image = (ImageView) player_dialog.findViewById(R.id.image);
                if (playerData.getPic() != null) {
                    image.setImageBitmap(playerData.getPic());
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            player_dialog.dismiss();

                            try {
                                String initString = readInitParameters();
                                processingInitString(initString);

                                Intent intent = new Intent(ConnectActivity.this, SzinvalasztoActivity.class);
                                startActivity(intent);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                player_dialog.setCanceledOnTouchOutside(true);
                player_dialog.show();
            }
        });

        Looper.loop();
    }

    //Kliens esetén ez a callback, hogy a másik játékos csatlakozott
    public void ServerConnected(Context context) {
        try {
            String initString = readInitParameters();
            processingInitString(initString);

            Intent intent = new Intent(ConnectActivity.this, SzinvalasztoActivity.class);
            startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processingInitString(String line) {
        //Első játék
        int index1 = line.indexOf(GameAbstract.RAJZOLGATO_NAME);
        String elso = line.substring(0, index1);
        SzinvalasztoView.setInitParameters(elso);

        //Második játék
        int index2 = line.indexOf(GameAbstract.PARBAJOZO_NAME);
        String masodik = line.substring(index1, index2);
        RajzolgatoView.setInitParameters(masodik);

        //Harmadik játék
        int index3 = line.indexOf(GameAbstract.SZAMOLGATO_NAME);
        String harmadik = line.substring(index2, index3);
        ShootingView.setInitParameters(harmadik);

        //Negyedik játék
        int index4 = line.indexOf("END");
        String negyedik = line.substring(index3, index4);
        SolveItView.setInitParameters(negyedik);
    }

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

        //Az interfész segítségével a háttérszálak értesíteni tudják ezt az activityt
        ServerThread.setClient_interface(this);
        ClientThread.setServer_interface(this);
    }

    //Beolvassuk a játékok kezdeti paramétereit
    public String readInitParameters() throws IOException {

        StringBuilder builder = new StringBuilder();
        String FILENAME = "InitParameters";
        FileInputStream fis = openFileInput(FILENAME);
        byte[] buffer = new byte[4096];
        int len;
        while ((len = fis.read(buffer)) > 0)
            builder.append(new String(buffer));

        fis.close();

        return builder.toString().substring(10); //INIT_DATA levágása
    }

    //Szerver háttérszál indítása a megadott porton
    private void HostConnect() {
        int port = 8888;
        try {
            port = Integer.parseInt(host_port_text.getText().toString());
        } catch (NumberFormatException e) {
        }

        connection = new Connection(Connection.CONNECTION_TYPE.SERVER, null,
                port, ConnectActivity.this);

        dialog = ProgressDialog.show(ConnectActivity.this, "",
                "Várakozás a játékostársra...", true);
        dialog.setCancelable(true);
    }

    //Kliens háttérszál indítása a megadott ip címmel és porttal
    private void ClientConnect() {
        String ip = client_ip_text.getText().toString().equals("") ? "192.168.1.1" :
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
}
