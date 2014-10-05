package com.oenik.bir.skillgame;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread implements Runnable {

    public static int port = 4444;
    public static String ip = "192.168.0.219";
    private Socket server_socket;
    private boolean client_run = false;
    private OutputStream outstream;
    private PrintWriter printWriter;

    public static ClientThread clientThread = null;

    private ClientThread(String ip_new, int port_new) {
        ip = ip_new;
        port = port_new;
    }

    public static ClientThread getInstance(String ip, int port) {
        if (clientThread == null)
            clientThread = new ClientThread(ip, port);

        return clientThread;
    }

    public boolean SendData(String message) {
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
            client_run = true;

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(server_socket.getInputStream()));

            while (client_run && !Thread.currentThread().isInterrupted()) {
                bufferedReader.readLine();
            }

        } catch (IOException e) {
            Log.e("Client", e.getMessage());
        } finally {
            if (server_socket != null) {
                try {
                    server_socket.close();
                } catch (IOException e) {
                    Log.i("Client", e.getMessage());
                }
            }
        }
    }

    //A kép Base64 kódolása
    public String EncodeImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return Base64.encodeToString(byteArray, 0);
    }
}
