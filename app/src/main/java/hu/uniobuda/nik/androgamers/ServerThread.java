package hu.uniobuda.nik.androgamers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import hu.uniobuda.nik.androgamers.game_files.GameAbstract;
import hu.uniobuda.nik.androgamers.main_menu.ConnectActivity;

public class ServerThread implements Runnable {

    public static int port = 4444;
    public static boolean server_run = false;
    private static ServerThread serverThread = null;
    private static Socket clientSocket;
    private static OutputStream outstream;
    private static PrintWriter printWriter;
    private static IFinalResult final_result;
    private final String imgString = "IMG";
    private final String playerString = "PLAY";
    private final String pointString = "POINT";
    private final String initString = "INIT";
    private ServerSocket serverSocket;
    private PlayerData playerData;
    private Context context;

    private ServerThread(int port_new, Context context_new) {
        port = port_new;
        context = context_new;
    }

    public static void setFinal_result(IFinalResult _final_result) {
        final_result = _final_result;
    }

    //Singleton
    public static ServerThread getInstance(int port, Context context) {
        if (serverThread == null)
            serverThread = new ServerThread(port, context);

        return serverThread;
    }

    //String üzenet küldése
    public static boolean SendMessage(String message) {
        try {

            if (outstream == null && clientSocket != null) {
                outstream = clientSocket.getOutputStream();

                printWriter = new PrintWriter(outstream, true);
            }

            printWriter.println(message);
            printWriter.flush();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
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
            Log.e("setGameInitString()", ex.getMessage());
            return false;
        }
    }

    public void run() {

        try {
            serverSocket = new ServerSocket(port);
            //A szerver a megadott porton várakozik...

        } catch (IOException e) {
            Log.e("Server - ServerThread", e.getMessage());
        }

        server_run = true;

        while (server_run && !Thread.currentThread().isInterrupted()) {

            try {
                clientSocket = serverSocket.accept();

                CommunicationThread commThread = new CommunicationThread();
                new Thread(commThread).start();

            } catch (IOException e) {
                Log.e("Server - ServerThread", e.getMessage());
            }
        }
    }

    private class CommunicationThread implements Runnable {

        BufferedReader input;
        private InputStream input_stream;

        public CommunicationThread() {

            try {

                //Kliens adatok
                //String[] client_address = clientSocket.getRemoteSocketAddress()
                //        .toString().split(":");

                //final String ip = client_address[0].substring(1); //Kliens IP
                //final String port = client_address[1]; //Kliens port

                this.input_stream = clientSocket.getInputStream();
                input = new BufferedReader(new InputStreamReader(input_stream));

                //Adatok fogadása...

            } catch (IOException e) {
                Log.e("Server - CommThread", e.getMessage());
            }
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {

                try {
                    String line = getLine(input);

                    if (line != null) {

                        //A játékos nevének fogadása
                        if (line.substring(0, 4).equals(playerString)) {
                            String player_name = line.split(":")[1];
                            if (playerData == null) playerData = new PlayerData();
                            playerData.setName(player_name);
                            Log.i("Server", "Player: " + player_name);
                        }
                        //A játékos képének fogadása
                        else if (line.substring(0, 3).equals(imgString)) {

                            final Bitmap bitmap = getImage(input, line);

                            if (bitmap != null) {
                                SendMessage("OK");
                                if (playerData == null) playerData = new PlayerData();
                                playerData.setPic(bitmap);

                                ConnectActivity.player_data = playerData;
                                ConnectActivity.client_connected = true;
                            }
                        } //Összpontszám fogadása
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
                        } else if (line.substring(0, 4).equals(initString)) {
                            setGameInitString(line);
                        }
                    }
                } catch (Exception e) {
                    Log.e("Server - CommThread (run)", (e.getMessage() == null) ? "Server run() error" : e.getMessage());
                }
            }
        }

        //Egy sor kiolvasása a streamből
        private String getLine(BufferedReader in) {

            String in_line;

            try {
                in_line = in.readLine();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return null;
            }

            return in_line;
        }

        //A kép beolvasása és Base64 dekódolása
        //Az átküldött hosszparaméterek alapján állapítja meg a kép érvényességét
        private Bitmap getImage(BufferedReader in, String line) {

            //"IMG:BASE64hossz:JPEGhossz"

            String[] values = line.split(":");
            int base64_length = 0;
            int jpeg_length = 0;

            try {
                base64_length = Integer.parseInt(values[1]);
                jpeg_length = Integer.parseInt(values[2]);
            } catch (Exception nfe) {
                Log.e("Server", nfe.toString());
            }

            try {
                StringBuilder builder = new StringBuilder();
                int charsRead;
                char[] buffer = new char[4096];

                int length = 0;

                while ((charsRead = in.read(buffer)) != -1) {
                    String message = new String(buffer).substring(0, charsRead);
                    builder.append(message);
                    length += charsRead;

                    if (length >= base64_length)
                        break;
                }

                byte[] img_bytes = builder.toString().getBytes();

                if (base64_length == img_bytes.length) {
                    byte[] base_decoded = Base64.decode(img_bytes, 0); //Base64 dekódolás

                    if (jpeg_length == base_decoded.length) {
                        return BitmapFactory.decodeByteArray(base_decoded, 0, base_decoded.length); //JPEG dekódolás
                    }
                } else {
                    Log.e("Server", "Hossz nem egyezik");
                }

                Log.e("getImage()", "Input olvasás hiba");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    //Segédosztály a felhasználó adatainak tárolására (név+kép)
    public class PlayerData {

        private Bitmap pic;
        private String name;

        public Bitmap getPic() {
            return pic;
        }

        public void setPic(Bitmap pic) {
            this.pic = pic;
        }

        public String getName() {
            return (name.equals("")) ? "AnonymPlayer" : name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
