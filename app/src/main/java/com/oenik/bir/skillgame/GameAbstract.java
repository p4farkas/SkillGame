package com.oenik.bir.skillgame;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public abstract class GameAbstract extends View {

    public GameAbstract(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract void GetResult(); //Végső pontszám kiszámítása
    public abstract void Init(); //Kezdeti inicializálás
    protected abstract void GameInit(); //Játszma inicializálás

    protected final int GAME_COUNT = 5; //Játszmák száma
    protected final long GAME_MILLIS = 1000; //Játszma ideje
    protected long old_time = 0; //Játszma időméréshez a régebben mért idő
    protected Thread time_thread; //Játszma méréshez háttérszál
    protected int current_game = 0;
    protected int view_size_width;
    protected int view_size_height;
}
