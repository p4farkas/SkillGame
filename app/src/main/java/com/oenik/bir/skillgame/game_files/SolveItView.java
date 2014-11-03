package com.oenik.bir.skillgame.game_files;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SolveItView extends GameAbstract {

    static final String[] equations = new String[]{"5*5+10=35", "56/7+6=14", "(91-27)/8=8"};
    static final List<Character> badAnswers = new ArrayList<Character>();
    static String selected;
    private String question;

    private static List<Integer> gameRound;

    static int selectedNumber;
    static char key;
    static char firstBadAnswer;
    static char secondBadAnswer;

    static boolean solved = false;
    static int score;
    private SharedPreferences sPrefs;
    private Handler handler;

    private static Random r = new Random();
    private Paint paint;
    private Context context;

    public SolveItView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        this.context = context;
        sPrefs = context.getSharedPreferences("solveItButtons", Context.MODE_PRIVATE);
        Init();
    }

    public static boolean setInitParameters(String line) {

        String[] tokens = line.split(":");

        if (!tokens[0].equals(GameAbstract.SZAMOLGATO_NAME))
            return false;

        gameRound = new ArrayList<Integer>(GAME_COUNT << 2);

        int length = tokens.length;
        for (int i = 1; i < length; i++) {
            gameRound.add(Integer.valueOf(tokens[i].charAt(0)));
        }

        return true;
    }

    public static String getGameInitString() {

        StringBuilder builder = new StringBuilder();
        builder.append(GameAbstract.SZAMOLGATO_NAME).append(":");
        for (int i = 0; i < GAME_COUNT; i++) {
            //equation and solution selection
            selectedNumber = r.nextInt(equations.length);
            selected = equations[selectedNumber];
            key = selected.charAt(r.nextInt(selected.length()));

            //generating other answers
            badAnswers.add('*');
            badAnswers.add('/');
            badAnswers.add('+');
            badAnswers.add('-');
            badAnswers.add('(');
            badAnswers.add(')');
            for (int j = 0; j < 10; j++) {
                badAnswers.add(Character.forDigit(j, 10));
            }
            badAnswers.remove(Character.valueOf(key));
            int badAnswerNumber1 = r.nextInt(badAnswers.size());
            firstBadAnswer = badAnswers.get(badAnswerNumber1);
            badAnswers.remove(badAnswers.get(badAnswerNumber1));
            int badAnswerNumber2 = r.nextInt(badAnswers.size());
            secondBadAnswer = badAnswers.get(badAnswerNumber2);

            builder.append(selectedNumber).append(":").append(key).append(":").append(firstBadAnswer).append(":").append(secondBadAnswer).append(":");
        }

        return builder.toString();
    }

    @Override
    public void GetResult() {
        game_points[4] = score;
    }

    @Override
    public void Init() {
//        //generating bad answers
//        badAnswers.add('*');
//        badAnswers.add('/');
//        badAnswers.add('+');
//        badAnswers.add('-');
//        badAnswers.add('(');
//        badAnswers.add(')');
//        for (int i=0; i<10; i++){
//            badAnswers.add(Character.forDigit(i,10));
//        }
//        badAnswers.remove(Character.valueOf(key));

        score = 0;
        old_time = System.currentTimeMillis();
        handler = new Handler();
        time_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    long current_time = System.currentTimeMillis();

                    if (solved || (current_time - old_time) >= 5000) {
                        old_time = current_time;
                        GameInit();
                        postInvalidate();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ((SolveItActivity) context).setButtons();
                            }
                        });
                    }

                } while (current_game < GAME_COUNT);
                GetResult();
            }
        });
        time_thread.start();

        GameInit();
    }

    @Override
    protected void GameInit() {
        current_game++;

//        //equation and solution selection
//        selectedNumber = r.nextInt(equations.length);
//        selected = equations[selectedNumber];
//        key = selected.charAt(r.nextInt(selected.length()));
//        question = selected.replaceFirst(Pattern.quote(Character.toString(key)), "?");
//
//        //generating other answers
//        int badAnswerNumber1 = r.nextInt(badAnswers.size());
//        firstBadAnswer = badAnswers.get(badAnswerNumber1);
//        badAnswers.remove(badAnswers.get(badAnswerNumber1));
//        int badAnswerNumber2 = r.nextInt(badAnswers.size());
//        secondBadAnswer = badAnswers.get(badAnswerNumber2);

        selectedNumber = gameRound.get(0) - 48;
        gameRound.remove(0);
        selected = equations[selectedNumber];
        int a = gameRound.get(0);
        gameRound.remove(0);
        key = (char) a;
        a = gameRound.get(0);
        gameRound.remove(0);
        firstBadAnswer = (char) a;
        a = gameRound.get(0);
        gameRound.remove(0);
        secondBadAnswer = (char) a;


        //random order
        int solution = r.nextInt(3);
        switch (solution) {
            case 0:
                //set solution answer for #1
                sPrefs.edit().putString("firstText", String.valueOf(key)).apply();
                sPrefs.edit().putString("firstTag", "PASS").apply();
//                //set other answer for #2
                sPrefs.edit().putString("secondText", String.valueOf(firstBadAnswer)).apply();
                sPrefs.edit().putString("secondTag", "FAIL").apply();
//                //set other answer for #3
                sPrefs.edit().putString("thirdText", String.valueOf(secondBadAnswer)).apply();
                sPrefs.edit().putString("thirdTag", "FAIL").apply();
                break;
            case 1:
//                //set solution answer for #2
                sPrefs.edit().putString("secondText", String.valueOf(key)).apply();
                sPrefs.edit().putString("secondTag", "PASS").apply();
//                //set other answer for #1
                sPrefs.edit().putString("firstText", String.valueOf(firstBadAnswer)).apply();
                sPrefs.edit().putString("firstTag", "FAIL").apply();
//                //set other answer for #3
                sPrefs.edit().putString("thirdText", String.valueOf(secondBadAnswer)).apply();
                sPrefs.edit().putString("thirdTag", "FAIL").apply();
                break;
            case 2:
//                //set solution answer for #3
                sPrefs.edit().putString("thirdText", String.valueOf(key)).apply();
                sPrefs.edit().putString("thirdTag", "PASS").apply();
//                //set other answer for #1
                sPrefs.edit().putString("firstText", String.valueOf(firstBadAnswer)).apply();
                sPrefs.edit().putString("firstTag", "FAIL").apply();
//                //set other answer for #2
                sPrefs.edit().putString("secondText", String.valueOf(secondBadAnswer)).apply();
                sPrefs.edit().putString("secondTag", "FAIL").apply();
                break;
        }

        solved = false;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        paint.setColor(Color.BLACK);
        paint.setTextSize(18);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Score: " + Integer.toString(score), 0, 0 + paint.getTextSize(), paint);

        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(question, width >> 1, height >> 2, paint);
    }
}