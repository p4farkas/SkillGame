package com.oenik.bir.skillgame;

public class Connection {

    public static enum CONNECTION_TYPE {SERVER, CLIENT}

    public Connection(CONNECTION_TYPE type)
    {
        switch (type){
            case SERVER:
            {
                Thread serverThread = new Thread(ServerThread.getInstance());
                serverThread.start();
                break;
            }
            case CLIENT:
            {
                Thread client_thread = new Thread(ClientThread.getInstance());
                client_thread.start();
                break;
            }
        }
    }
}
