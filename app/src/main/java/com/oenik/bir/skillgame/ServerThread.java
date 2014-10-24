package com.oenik.bir.skillgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.oenik.bir.skillgame.game_files.RajzolgatoView;
import com.oenik.bir.skillgame.game_files.SzinvalasztoView;
import com.oenik.bir.skillgame.main_menu.IClientConnected;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread implements Runnable {

    public static int port = 4444;
    private static ServerThread serverThread = null;
    private static IClientConnected client_interface;
    private final String imgString = "IMG";
    private final String playerString = "PLAY";
    boolean server_run = false;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private OutputStream outstream;
    private PrintWriter printWriter;
    private PlayerData playerData;
    private Context context;

    private ServerThread(int port_new, Context context_new) {
        port = port_new;
        context = context_new;
    }

    public static void setClient_interface(IClientConnected _client_interface) {
        client_interface = _client_interface;
    }

    //Singleton
    public static ServerThread getInstance(int port, Context context) {
        if (serverThread == null)
            serverThread = new ServerThread(port, context);

        return serverThread;
    }

    //String üzenet küldése
    public boolean SendData(String message) {
        try {

            if (outstream == null && clientSocket != null) {
                outstream = clientSocket.getOutputStream();

                printWriter = new PrintWriter(outstream, true);
            }

            printWriter.println(message);

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void run() {

        try {
            serverSocket = new ServerSocket(port);
            Log.i("Server", "Server is listening on " + port);

        } catch (IOException e) {
            Log.e("Server - ServerThread", e.getMessage());
        }

        server_run = true;

        while (server_run && !Thread.currentThread().isInterrupted()) {

            try {

                String initdataString = getGameInitString(); //A játékok random kezdőértékei

                clientSocket = serverSocket.accept();
                SendData(initdataString); //Játék kezdeti paraméterek elküldése

                CommunicationThread commThread = new CommunicationThread();
                new Thread(commThread).start();

            } catch (IOException e) {
                Log.e("Server - ServerThread", e.getMessage());
            }
        }
    }

    //A játékok osztályai legenerálják a kezdőértékeket
    //Visszatérési érték az összefűzött string
    private String getGameInitString() throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("INIT_DATA:");
        String rajzolgatoString = RajzolgatoView.getGameInitString(500, 1000);
        String szinvalasztoString = SzinvalasztoView.getGameInitString();
        //String solveitString
        //String shootingString

        builder.append(szinvalasztoString);
        builder.append(rajzolgatoString);
        //builder.append(solveitString);
        //builder.append(shootingString);

        String filename = "InitParameters";
        String string = builder.toString();
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    private class CommunicationThread implements Runnable {

        BufferedReader input;
        private InputStream input_stream;

        public CommunicationThread() {

            try {

                String[] client_address = clientSocket.getRemoteSocketAddress()
                        .toString().split(":");

                final String ip = client_address[0].substring(1); //Kliens IP
                final String port = client_address[1]; //Kliens port

                Log.i("Server", "Client: " + ip + ":" + port);

                this.input_stream = clientSocket.getInputStream();
                input = new BufferedReader(new InputStreamReader(input_stream));

                Log.i("Server", "Receiving...");

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

                        //A játékos nevét kaptuk meg
                        if (line.substring(0, 4).equals(playerString)) {
                            String player_name = line.split(":")[1];
                            if (playerData == null) playerData = new PlayerData();
                            playerData.setName(player_name);
                            Log.i("Server", "Player: " + player_name);
                        }
                        //A játékos képét kaptuk meg
                        else if (line.substring(0, 3).equals(imgString)) {

                            final Bitmap bitmap = getImage(input, line);

                            if (bitmap != null) {
                                if (playerData == null) playerData = new PlayerData();
                                playerData.setPic(bitmap);
                                if (client_interface != null)
                                    client_interface.ClientConnected(playerData, context);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("Server - CommThread (run)", (e.getMessage() == null) ? "Server run() error" : e.getMessage());
                }
            }
        }

        //Egy sor kiolvasása a streamből
        private String getLine(BufferedReader in) {

            String in_line = null;

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
                        return BitmapFactory.decodeByteArray(base_decoded, 0, base_decoded.length); //jpeg dekódolás
                    }
                } else {
                    Log.e("Server", "Hossz nem egyezik");
                }

                Log.e("getImage()", "Input reading failed");
            } catch (IOException e) {
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
