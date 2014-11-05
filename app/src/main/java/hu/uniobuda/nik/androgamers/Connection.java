package hu.uniobuda.nik.androgamers;

import android.content.Context;

public class Connection {

    private static ThreadAbstract conn_thread = null;

    public Connection(CONNECTION_TYPE type, String ip, int port, Context context) {
        switch (type) {
            case SERVER: {
                conn_thread = (ThreadAbstract) new Thread(ServerThread.getInstance(port, context));
                conn_thread.start();
                break;
            }
            case CLIENT: {
                conn_thread = (ThreadAbstract) new Thread(ClientThread.getInstance(ip, port, context));
                conn_thread.start();
                break;
            }
        }
    }

    public static void SendMessage(String message) {
        conn_thread.SendMessage(message);
    }

    public void CloseConnection() {
        if (conn_thread != null) {
            conn_thread.interrupt();
            conn_thread = null;
        }
    }

    public static enum CONNECTION_TYPE {SERVER, CLIENT}
}
