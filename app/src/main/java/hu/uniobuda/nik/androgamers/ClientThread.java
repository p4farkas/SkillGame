package hu.uniobuda.nik.androgamers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;

import hu.uniobuda.nik.androgamers.game_files.GameAbstract;
import hu.uniobuda.nik.androgamers.game_files.RajzolgatoView;
import hu.uniobuda.nik.androgamers.game_files.ShootingView;
import hu.uniobuda.nik.androgamers.game_files.SolveItView;
import hu.uniobuda.nik.androgamers.game_files.SzinvalasztoView;
import hu.uniobuda.nik.androgamers.main_menu.ConnectActivity;
import hu.uniobuda.nik.androgamers.main_menu.ProfileActivity;

public class ClientThread implements Runnable {

    public static int port = 1234;
    public static String ip = "192.168.0.220";
    public static ClientThread clientThread = null;
    public static boolean client_run = false;
    private static Socket server_socket;
    private static OutputStream outstream;
    private static InputStreamReader streamreader;
    private static PrintWriter printWriter;
    private static IFinalResult final_result;
    private final String pointString = "POINT";
    private Context context;

    private ClientThread(String ip_new, int port_new, Context context_new) {
        ip = ip_new;
        port = port_new;

        context = context_new;
    }

    public static void setFinal_result(IFinalResult _final_result) {
        final_result = _final_result;
    }

    //Singleton
    public static ClientThread getInstance(String ip, int port, Context context) {
        if (clientThread == null)
            clientThread = new ClientThread(ip, port, context);

        return clientThread;
    }

    //String üzenet küldése
    public static boolean SendMessage(String message) {
        try {

            if (server_socket == null)
                return false;

            if (outstream == null)
                outstream = server_socket.getOutputStream();

            if (printWriter == null)
                printWriter = new PrintWriter(outstream, true);

            printWriter.println(message);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    //A játékok osztályai generálják a kezdőértékeket
    //Visszatérési érték az összefűzött string
    private String getGameInitString() throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("INIT_DATA:");
        String rajzolgatoString = RajzolgatoView.getGameInitString(500, 1000);
        String szinvalasztoString = SzinvalasztoView.getGameInitString();
        String shootingString = ShootingView.getGameInitString();
        String solveitString = SolveItView.getGameInitString();

        builder.append(szinvalasztoString).append(rajzolgatoString)
                .append(shootingString).append(solveitString).append("END");

        String filename = "InitParameters";
        String text = builder.toString();
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return text;
    }

    @Override
    public void run() {
        try {
            server_socket = new Socket(ip, port);

            streamreader = new InputStreamReader(server_socket.getInputStream());

            String init_data = getGameInitString(); //A játékok random kezdőértékei
            SendMessage(init_data); //Játék kezdeti paraméterek elküldése

            SendImage(ProfileActivity.loadImage(context)); //Kép elküldése

            client_run = true;

            BufferedReader bufferedReader = new BufferedReader(streamreader);

            String line;
            while (client_run && !Thread.currentThread().isInterrupted()) {
                line = bufferedReader.readLine();

                if (line != null) {
                    //A kliens sikeresen kapcsolódott a szerverhez
                    if (line.substring(0, 2).equals("OK")) {
                        ConnectActivity.server_connected = true;

                    }//A szerver elküldte az összpontszámát és várakozik a kliensére
                    else if (line.substring(0, 5).equals(pointString)) {
                        int point = Integer.parseInt(line.substring(6));
                        do {
                            //Várakozás a játék befejezésére
                        } while (!GameAbstract.isGame_finished());

                        if (final_result != null) {
                            final_result.getFinalPoint(point);
                        }

                        //Összpontszám elküldése
                        SendMessage("POINT:" + GameAbstract.getFinalPoint());
                    }
                }
            }

        } catch (IOException e) {
            Log.e("Client", e.getMessage());
            client_run = false;
        }
    }

    //A kép Base64 kódolása és küldése
    public boolean SendImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.anonym);
        }

        //JPEG kódolás
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream);
        byte[] byteArray = stream.toByteArray();
        long comp_size = byteArray.length;

        byte[] image = Base64.encode(byteArray, 0);
        int base64_size = image.length;

        //A játékos nevének lekérése
        String player_message = ProfileActivity.getUsername(context);
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
