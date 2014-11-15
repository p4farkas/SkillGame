package hu.uniobuda.nik.androgamers.game_files;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

interface INextGame {
    void NextGame();
}

public abstract class GameAbstract extends View {

    public static final String SZINVALASZTO_NAME = "APP1";
    public static final String RAJZOLGATO_NAME = "APP2";
    public static final String PARBAJOZO_NAME = "APP3";
    public static final String SZAMOLGATO_NAME = "APP4";

    public static final String SZINVALASZTO_TEXT = "Koppints, ha a felirat szövege és színe megegyezik!";
    public static final String RAJZOLGATO_TEXT = "Rajzold meg a legkisebb befoglaló téglalapot!";
    public static final String PARBAJOZO_TEXT = "Te légy a gyorsabb párbajozó!";
    public static final String SZAMOLGATO_TEXT = "Oldd meg az egyenletet!";
    protected static final int GAME_COUNT = 5; //Játszmák száma
    protected static int[] game_points = new int[4]; //Az egyes játékok végső pontszámai
    protected final long GAME_MILLIS = 2500; //Játszma ideje
    protected long old_time = 0; //Játszma időméréshez a régebben mért idő
    protected Thread time_thread; //Játszma méréshez háttérszál
    protected int current_game = 0;
    protected int view_size_width = 1000;
    protected int view_size_height = 500;

    public GameAbstract(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static int getFinalPoint() {
        int sum = 0;
        for (int point : game_points) {
            sum += point;
        }

        return sum;
    }

    public abstract void GetResult(); //Végső pontszám kiszámítása

    public abstract void Init(); //Kezdeti inicializálás

    protected abstract void GameInit(); //Játszma inicializálás
}
