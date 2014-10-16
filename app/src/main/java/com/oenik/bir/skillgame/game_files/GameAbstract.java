package com.oenik.bir.skillgame.game_files;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public abstract class GameAbstract extends View {

    public static final String SZINVALASZTO_NAME = "APP1";
    public static final String RAJZOLGATO_NAME = "APP2";
    public static final String PARBAJOZO_NAME = "APP3";
    public static final String SZAMOLGATO_NAME = "APP4";
    protected static final int GAME_COUNT = 5; //Játszmák száma
    protected final long GAME_MILLIS = 2500; //Játszma ideje
    protected long old_time = 0; //Játszma időméréshez a régebben mért idő
    protected Thread time_thread; //Játszma méréshez háttérszál
    protected int current_game = 0;
    protected int view_size_width = 1000;
    protected int view_size_height = 500;

    public GameAbstract(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract void GetResult(); //Végső pontszám kiszámítása

    public abstract void Init(); //Kezdeti inicializálás

    protected abstract void GameInit(); //Játszma inicializálás
}
