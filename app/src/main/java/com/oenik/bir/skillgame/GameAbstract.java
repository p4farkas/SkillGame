package com.oenik.bir.skillgame;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public abstract class GameAbstract extends View {

    public GameAbstract(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract void GetResult();
    public abstract void Init();

    protected final int GAME_COUNT = 5;
    protected final long GAME_MILLIS = 1000;
    protected long old_time = 0;
    protected Thread time_thread;
}
