package com.oenik.bir.skillgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

class ServerThread implements Runnable {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    public static int port = 4444;
    boolean server_run = false;
    private OutputStream outstream;
    private PrintWriter printWriter;
    private static ServerThread serverThread = null;

    private Context context;

    private final String imgString = "IMG";
    private final String playerString = "PLAY";

    private ServerThread(int port_new, Context context_new) {
        port = port_new;

        context = context_new;
    }

    public static ServerThread getInstance(int port, Context context) {
        if (serverThread == null)
            serverThread = new ServerThread(port, context);

        return serverThread;
    }

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

                String[] client_address = clientSocket.getRemoteSocketAddress()
                        .toString().split(":");

                final String ip = client_address[0].substring(1);
                final String port = client_address[1];

                Log.i("Client", "Client: " + ip + port);

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

                        if (line.substring(0, 3).equals(imgString)) {

                            final Bitmap bitmap = getImage(input, line);

                            if (bitmap != null) {
                                Log.i("Server", "Méret: " + bitmap.getWidth() + "," + bitmap.getHeight());
                                ConnectActivity.ShowPlayerDialog(bitmap, context);
                            }
                        }
                        else if (line.substring(0, 4).equals(playerString))
                        {
                            String player_name = getLine(input).split(":")[1];
                        }
                    }
                } catch (Exception e) {
                    Log.e("Server - CommThread (run)", e.getMessage());

                    break;
                }
            }
        }

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
                String message = "";
                int charsRead = 0;
                char[] buffer = new char[1024*4];

                int length = 0;

                while ((charsRead = in.read(buffer)) != -1) {
                    message = new String(buffer).substring(0, charsRead);
                    builder.append(message);
                    length += charsRead;
                    Log.i("Server", "Length: " + String.valueOf(length));

                    if (length>=base64_length)
                        break;
                }

                byte[] img_bytes = builder.toString().getBytes();

                Log.i("Server", String.valueOf(img_bytes.length) + " - " + String.valueOf(base64_length));

                if (base64_length == img_bytes.length) {
                    byte[] base_decoded = Base64.decode(img_bytes, 0);

                    if (jpeg_length == base_decoded.length) {
                        return BitmapFactory.decodeByteArray(base_decoded, 0, base_decoded.length);
                    }
                } else {
                    Log.i("Server", "Hossz nem egyezik");
                }

                Log.e("getImage()", "Input reading failed");
            }
            catch (IOException e) {
            }

            return null;

        }
    }
}
