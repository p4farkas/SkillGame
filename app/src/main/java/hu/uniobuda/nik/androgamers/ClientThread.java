package hu.uniobuda.nik.androgamers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;

import hu.uniobuda.nik.androgamers.game_files.GameAbstract;
import hu.uniobuda.nik.androgamers.main_menu.IServerConnected;

public class ClientThread extends ThreadAbstract {

    public static int port = 4444;
    public static String ip = "192.168.0.219";
    public static ClientThread clientThread = null;
    private static IServerConnected server_interface;
    private final String pointString = "POINT";
    private Socket server_socket;
    private boolean client_run = false;
    private OutputStream outstream;
    private PrintWriter printWriter;
    private Context context;

    private ClientThread(String ip_new, int port_new, Context context_new) {
        ip = ip_new;
        port = port_new;

        context = context_new;
    }

    public static void setServer_interface(IServerConnected _server_interface) {
        server_interface = _server_interface;
    }

    //Singleton
    public static ClientThread getInstance(String ip, int port, Context context) {
        if (clientThread == null)
            clientThread = new ClientThread(ip, port, context);

        return clientThread;
    }

    //String üzenet küldése
    public boolean SendMessage(String message) {
        try {

            if (outstream == null && server_socket != null) {
                outstream = server_socket.getOutputStream();

                printWriter = new PrintWriter(outstream, true);
            }

            printWriter.println(message);

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void run() {
        try {
            server_socket = new Socket(ip, port);
            Log.i("Client", "Connected to server: " + ip + ":" + port);

            SendImage(BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.anonim)); //Kép elküldése

            client_run = true;

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(server_socket.getInputStream()));

            String line = "";
            while (client_run && !Thread.currentThread().isInterrupted()) {
                line = bufferedReader.readLine();

                if (line.substring(0, 5).equals(pointString)) {
                    int point = Integer.parseInt(line.substring(6));
                    ResultActivity.getFinalPoint(point);

                    SendMessage("POINT:" + GameAbstract.getFinalPoint());
                } else if (setGameInitString(line)) {
                    if (server_interface != null)
                        server_interface.ServerConnected(context);
                }
            }

        } catch (IOException e) {
            Log.e("Client", e.getMessage());
        }
    }

    //A játékok kezdeti paramétereinek beolvasása és mentése
    private boolean setGameInitString(String line) throws IOException {
        try {
            String FILENAME = "InitParameters";
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(line.getBytes());
            fos.flush();
            fos.close();
            return true;
        } catch (FileNotFoundException ex) {
            return false;
        }
    }

    //A kép Base64 kódolása és küldése
    public boolean SendImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream);
        byte[] byteArray = stream.toByteArray();
        long comp_size = byteArray.length;

        byte[] image = Base64.encode(byteArray, 0);
        int base64_size = image.length;

        //A játékos neve
        String player_message = "Béla";
        SendMessage("PLAY:" + player_message);

        //Header
        String message = "IMG:" + base64_size + ":" + comp_size;
        SendMessage(message);

        try {
            OutputStreamWriter oos = new OutputStreamWriter(outstream);
            String d = new String(image, Charset.defaultCharset()).trim() + '\n';
            oos.write(d);

            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
