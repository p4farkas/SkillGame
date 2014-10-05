package com.oenik.bir.skillgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;

public class ClientThread implements Runnable {

    public static int port = 4444;
    public static String ip = "192.168.0.219";
    private Socket server_socket;
    private boolean client_run = false;
    private OutputStream outstream;
    private PrintWriter printWriter;

    private Context context;

    public static ClientThread clientThread = null;

    private ClientThread(String ip_new, int port_new, Context context_new) {
        ip = ip_new;
        port = port_new;

        context = context_new;
    }

    public static ClientThread getInstance(String ip, int port, Context context) {
        if (clientThread == null)
            clientThread = new ClientThread(ip, port, context);

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

            SendImage(BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.anonim));

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
    public boolean SendImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream);
        byte[] byteArray = stream.toByteArray();
        long comp_size = byteArray.length;

        byte[] image = Base64.encode(byteArray, 0);
        int base64_size = image.length;

        String player_message = "Player01";
        SendData(player_message);

        //Header
        String message = "IMG:" + base64_size + ":" + comp_size;
        SendData(message);

        try {
            OutputStreamWriter oos=new OutputStreamWriter(outstream);
            String d = new String(image, Charset.defaultCharset()).trim() + '\n';
            oos.write(d);

            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
