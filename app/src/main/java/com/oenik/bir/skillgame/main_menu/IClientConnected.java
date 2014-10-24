package com.oenik.bir.skillgame.main_menu;

import android.content.Context;

import com.oenik.bir.skillgame.ServerThread;

public  interface IClientConnected{
    public void ClientConnected(final ServerThread.PlayerData playerData, final Context context);
}
