package com.oenik.bir.skillgame.game_files;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class SolveItView extends GameAbstract{

    private final String[] equations = new String[] {"5*5+10=35", "56/7+6=14", "(91-27)/8=8"};
    private final List<Character> badAnswers;
    private String selected;
    private String question;

    static int selectedNumber;
    static char key;
    static char firstBadAnswer;
    static char secondBadAnswer;

    static int score;
    private SharedPreferences sPrefs;

    private static Random r = new Random();
    private Paint paint;
    private Context context;

    public SolveItView(Context context, AttributeSet attrs) {
        super(context, attrs);
        badAnswers = new ArrayList<Character>();
        paint = new Paint();
        this.context = context;
        sPrefs = context.getSharedPreferences("solveItButtons", Context.MODE_PRIVATE);
        Init();
    }

//    public static boolean setInitParameters(String line) {
//
//        String[] tokens = line.split(":");
//
//        if (!tokens[0].equals(GameAbstract.SZAMOLGATO_NAME))
//            return false;
//
//        code_indices = new int[GAME_COUNT];
//        name_indices = new int[GAME_COUNT];
//
//        int index = 0;
//        int length = tokens.length;
//        for (int i=1;i<length;i+=2)
//        {
//            if (i < (GAME_COUNT << 1))
//            {
//                code_indices[index] = Integer.parseInt(tokens[i]);
//                name_indices[index++] = Integer.parseInt(tokens[i+1]);
//            }
//        }
//
//        return true;
//    }

    public static String getGameInitString() {

        StringBuilder builder = new StringBuilder();
        builder.append(GameAbstract.SZAMOLGATO_NAME).append(":");
        for (int i=0;i<GAME_COUNT;i++)
        {
            //GameInit();
            builder.append(selectedNumber).append(":").append(key).append(":").append(firstBadAnswer).append(":").append(secondBadAnswer).append(":");
        }

        return builder.toString();
    }

    @Override
    public void GetResult() {

    }

    @Override
    public void Init() {
        //generating bad answers
        badAnswers.add('*');
        badAnswers.add('/');
        badAnswers.add('+');
        badAnswers.add('-');
        badAnswers.add('(');
        badAnswers.add(')');
        for (int i=0; i<10; i++){
            badAnswers.add(Character.forDigit(i,10));
        }
        badAnswers.remove(Character.valueOf(key));


        old_time = System.currentTimeMillis();
        time_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    long current_time = System.currentTimeMillis();

                    if ((current_time - old_time) >= GAME_MILLIS) {
                        current_game++;
                        old_time = current_time;
                        GameInit();
                    }

                } while (current_game < GAME_COUNT);

               SolveItActivity.NextGame(context);
            }
        });

        GameInit();

        time_thread.start();
    }

    @Override
    protected void GameInit() {
        //equation and solution selection
        selectedNumber = r.nextInt(equations.length);
        selected = equations[selectedNumber];
        key = selected.charAt(r.nextInt(selected.length()));
        question = selected.replaceFirst(Pattern.quote(Character.toString(key)), "?");

        //generating other answers
        int badAnswerNumber1 = r.nextInt(badAnswers.size());
        firstBadAnswer = badAnswers.get(badAnswerNumber1);
        badAnswers.remove(badAnswers.get(badAnswerNumber1));
        int badAnswerNumber2 = r.nextInt(badAnswers.size());
        secondBadAnswer = badAnswers.get(badAnswerNumber2);

        //random order
        int solution = r.nextInt(3);
        switch(solution) {
            case 0:
                //set solution answer for #1
                sPrefs.edit().putString("firstText",String.valueOf(key)).apply();
                sPrefs.edit().putString("firstListener","onClickListenerPassed").apply();
//                //set other answer for #2
                sPrefs.edit().putString("secondText",String.valueOf(firstBadAnswer)).apply();
                sPrefs.edit().putString("secondListener","onClickListenerFailed").apply();
//                //set other answer for #3
                sPrefs.edit().putString("thirdText",String.valueOf(secondBadAnswer)).apply();
                sPrefs.edit().putString("thirdListener","onClickListenerFailed").apply();
                break;
            case 1:
//                //set solution answer for #2
                sPrefs.edit().putString("secondText",String.valueOf(key)).apply();
                sPrefs.edit().putString("secondListener","onClickListenerPassed").apply();
//                //set other answer for #1
                sPrefs.edit().putString("firstText",String.valueOf(firstBadAnswer)).apply();
                sPrefs.edit().putString("firstListener","onClickListenerFailed").apply();
//                //set other answer for #3
                sPrefs.edit().putString("thirdText",String.valueOf(secondBadAnswer)).apply();
                sPrefs.edit().putString("thirdListener","onClickListenerFailed").apply();
                break;
            case 2:
//                //set solution answer for #3
                sPrefs.edit().putString("thirdText",String.valueOf(key)).apply();
                sPrefs.edit().putString("thirdListener","onClickListenerPassed").apply();
//                //set other answer for #1
                sPrefs.edit().putString("firstText",String.valueOf(firstBadAnswer)).apply();
                sPrefs.edit().putString("firstListener","onClickListenerFailed").apply();
//                //set other answer for #2
                sPrefs.edit().putString("secondText",String.valueOf(secondBadAnswer)).apply();
                sPrefs.edit().putString("secondListener","onClickListenerFailed").apply();
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        paint.setColor(Color.BLACK);
        paint.setTextSize(18);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Score: "+Integer.toString(score), 0, 0+paint.getTextSize(), paint);

        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(question, width >> 1, height >> 2, paint);
    }
}