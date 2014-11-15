package hu.uniobuda.nik.androgamers;

import android.content.Context;

public class Connection {

    private static Thread conn_thread = null;
    private static boolean server = false;

    public Connection(CONNECTION_TYPE type, String ip, int port, Context context) {
        switch (type) {
            case SERVER: {
                conn_thread = new Thread(ServerThread.getInstance(port, context));
                conn_thread.start();
                server = true;
                break;
            }
            case CLIENT: {
                conn_thread = new Thread(ClientThread.getInstance(ip, port, context));
                conn_thread.start();
                server = false;
                break;
            }
        }
    }

    public static void SendMessage(String message) {
        if (server)
            ServerThread.SendMessage(message);
        else
            ClientThread.SendMessage(message);
    }

    public static void CloseConnection() {
        ServerThread.server_run = false;
        ClientThread.client_run = false;

        if (conn_thread != null) {
            conn_thread.interrupt();
            conn_thread = null;
        }
    }

    public static enum CONNECTION_TYPE {SERVER, CLIENT}
}
