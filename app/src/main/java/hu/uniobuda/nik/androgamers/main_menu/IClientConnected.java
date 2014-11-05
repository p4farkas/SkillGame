package hu.uniobuda.nik.androgamers.main_menu;

import android.content.Context;

import hu.uniobuda.nik.androgamers.ServerThread;

public interface IClientConnected {
    public void ClientConnected(final ServerThread.PlayerData playerData, final Context context);
}
