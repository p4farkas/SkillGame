package com.oenik.bir.skillgame.main_menu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseAR {

    DataBaseHandler db;

    public DataBaseAR(Context context) {
        db = new DataBaseHandler(context);
    }

    public long insertHighScore(int score) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHandler.SCORE, score);
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        long id = sqLiteDatabase.insert(DataBaseHandler.TABLE_NAME, null, contentValues);
        db.close();
        return id;
    }

    public Cursor loadHighScores() {
        SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
        Cursor c = sqLiteDatabase.query(DataBaseHandler.TABLE_NAME, null, null, null, null, null, DataBaseHandler.SCORE + " DESC", "10");
        c.moveToFirst();
        db.close();
        return c;
    }


    class DataBaseHandler extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "SkillGame.db";
        private static final String TABLE_NAME = "highscore";
        private static final String UID = "_id";
        private static final String SCORE = "score";
        private static final int DATABASE_VERSION = 1;
        private Context context;

        public DataBaseHandler(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SCORE + " INTEGER);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
            onCreate(sqLiteDatabase);
        }
    }
}