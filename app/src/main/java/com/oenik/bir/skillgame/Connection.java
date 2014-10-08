package com.oenik.bir.skillgame;

import android.content.Context;

public class Connection {

    public static enum CONNECTION_TYPE {SERVER, CLIENT}

    private Thread conn_thread = null;

    public Connection(CONNECTION_TYPE type, String ip, int port, Context context)
    {
        switch (type){
            case SERVER:
            {
                conn_thread = new Thread(ServerThread.getInstance(port, context));
                conn_thread.start();
                break;
            }
            case CLIENT:
            {
                conn_thread = new Thread(ClientThread.getInstance(ip, port, context));
                conn_thread.start();
                break;
            }
        }
    }

    public void CloseConnection()
    {
        if (conn_thread != null) {
            conn_thread.interrupt();
            conn_thread = null;
        }
    }
}
