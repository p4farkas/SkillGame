package com.oenik.bir.skillgame;

public class Connection {

    public static enum CONNECTION_TYPE {SERVER, CLIENT}

    public Connection(CONNECTION_TYPE type, String ip, int port)
    {
        switch (type){
            case SERVER:
            {
                Thread serverThread = new Thread(ServerThread.getInstance(port));
                serverThread.start();
                break;
            }
            case CLIENT:
            {
                Thread client_thread = new Thread(ClientThread.getInstance(ip, port));
                client_thread.start();
                break;
            }
        }
    }
}
