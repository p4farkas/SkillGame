package hu.uniobuda.nik.androgamers.game_files;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Random;

public class SzinvalasztoView extends GameAbstract {

    private static final int COLOR_COUNT = 13;
    private static Random rand = new Random();

    private static int code_index = 0;
    private static int name_index = 0;

    private static int[] code_indices;
    private static int[] name_indices;
    private static INextGame next_game;
    final int ORANGE = Color.parseColor("#ffa500");
    final int PURPLE = Color.parseColor("#663399");
    final int BROWN = Color.parseColor("#f4a460");
    final int PINK = Color.parseColor("#ff69b4");
    private int[] color_codes = null;
    private String[] color_names = null;
    private int view_size_width;
    private int view_size_height;
    private Bitmap canvasBitmap;
    private Paint canvasPaint;
    private Paint textPaint;
    private Paint scorePaint;
    private int final_point = 0;
    private Context context;

    private boolean thread_run = true;

    public SzinvalasztoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.isInEditMode();

        Init();
    }

    public static void setNext_game(INextGame _next_game) {
        next_game = _next_game;
    }

    //Játék kezdőértékeinek beállítása
    public static boolean setInitParameters(String line) {

        String[] tokens = line.split(":");

        if (!tokens[0].equals(GameAbstract.SZINVALASZTO_NAME))
            return false;

        code_indices = new int[GAME_COUNT];
        name_indices = new int[GAME_COUNT];

        int index = 0;
        int length = tokens.length;
        for (int i = 1; i < length; i += 2) {
            if (i < (GAME_COUNT << 1)) {
                code_indices[index] = Integer.parseInt(tokens[i]);
                name_indices[index++] = Integer.parseInt(tokens[i + 1]);
            }
        }

        return true;
    }

    //A játék kezdeti paraméterei
    public static String getGameInitString() {

        StringBuilder builder = new StringBuilder();
        builder.append(GameAbstract.SZINVALASZTO_NAME).append(":");
        for (int i = 0; i < GAME_COUNT; i++) {
            getRandomColor();
            builder.append(code_index).append(":").append(name_index).append(":");
        }

        return builder.toString();
    }

    //A felirat színének meghatározása
    public static void getRandomColor() {
        code_index = rand.nextInt(COLOR_COUNT);
        int max = code_index + 1 >= COLOR_COUNT ? COLOR_COUNT : code_index + 1;
        int min = code_index - 1 < 0 ? 0 : code_index - 1;
        name_index = rand.nextInt(max - min) + min; //A 33% valószínűség a helyes színre
    }

    @Override
    public void Init() {

        view_size_width = 1000; //dummy values
        view_size_height = 500;

        color_codes = new int[]{Color.WHITE, Color.BLACK, Color.CYAN, Color.DKGRAY,
                Color.YELLOW, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.RED, ORANGE,
                PURPLE, BROWN, PINK};
        color_names = new String[]{"Fehér", "Fekete", "Cián", "Szürke", "Sárga", "Kék",
                "Zöld", "Magenta", "Piros", "Narancssárga", "Lila", "Barna", "Rózsaszín"};

        canvasPaint = new Paint(Paint.DITHER_FLAG);
        canvasPaint.setColor(Color.WHITE);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStrokeCap(Paint.Cap.ROUND);

        scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);
        scorePaint.setTextSize(18);
        scorePaint.setTextAlign(Paint.Align.LEFT);

        old_time = System.currentTimeMillis();

        //Háttérszálon figyeljük az időt
        time_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    if (!thread_run)
                        break;

                    long current_time = System.currentTimeMillis();

                    if ((current_time - old_time) >= GAME_MILLIS) {
                        current_game++;
                        old_time = current_time;
                        GameInit();
                    }

                } while (current_game < GAME_COUNT);

                game_points[0] = final_point;

                if (next_game != null)
                    next_game.NextGame();
            }
        });
    }

    @Override
    protected void GameInit() {
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        view_size_width = w;
        view_size_height = h;

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);

        canvas.drawText("Eredmény: " + String.valueOf(getFinalPoint() + final_point), 10, 20, scorePaint);

        if (color_codes == null || color_names == null || current_game > 4)
            return;

        code_index = color_codes[code_indices[current_game]];
        name_index = name_indices[current_game];

        String text = color_names[name_index];
        textPaint.setColor(code_index);

        textPaint.setTextSize(view_size_width >> 3);
        PointF textpoint = GetTextPoint(text);
        canvas.drawText(text, textpoint.x, textpoint.y, textPaint);

        if (time_thread.getState() == Thread.State.NEW)
            time_thread.start();
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                GetResult(); //Minden érintésnél kiértékeljük az eredményt
                break;
            default:
                return false;
        }

        return true;
    }

    //A felirat helyének meghatározása
    private PointF GetTextPoint(String text) {
        Rect rect = new Rect();

        textPaint.getTextBounds(text, 0, text.length(), rect);
        float x = (view_size_width / 2) - (Math.abs(rect.right - rect.left) / 2);
        float y = view_size_height >> 1;

        return new PointF(x, y);
    }

    //Az eredmény kiértékelése
    @Override
    public void GetResult() {
        if (name_indices[current_game] == code_indices[current_game])
            final_point += 100;
        else
            Log.i("Szinvalaszto", "NEM TALÁLT");

        current_game++;
        old_time = System.currentTimeMillis();
        if (current_game < GAME_COUNT)
            GameInit();
        else {
            game_points[0] = final_point;
            if (time_thread != null) {
                thread_run = false;
                time_thread.interrupt();
            }
            if (next_game != null)
                next_game.NextGame();
        }
    }
}
