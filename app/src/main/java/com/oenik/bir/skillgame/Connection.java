package com.oenik.bir.skillgame;

import android.content.Context;

public class Connection {

    public static enum CONNECTION_TYPE {SERVER, CLIENT}

    public Connection(CONNECTION_TYPE type, String ip, int port, Context context)
    {
        switch (type){
            case SERVER:
            {
                Thread serverThread = new Thread(ServerThread.getInstance(port, context));
                serverThread.start();
                break;
            }
            case CLIENT:
            {
                Thread client_thread = new Thread(ClientThread.getInstance(ip, port, context));
                client_thread.start();
                break;
            }
        }
    }
}
