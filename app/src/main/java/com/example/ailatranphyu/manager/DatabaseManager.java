package com.example.ailatranphyu.manager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.example.ailatranphyu.App;
import com.example.ailatranphyu.model.HighScore;
import com.example.ailatranphyu.model.Question;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class DatabaseManager {
    private String DATABASE_NAME = "Question";

    @SuppressLint("SdCardPath")
    private String DATABASE_PATH = android.os.Build.VERSION.SDK_INT >= 17 ?
            (App.getContext().getApplicationInfo().dataDir + "/databases/")
                    : ("/data/data/" + App.getContext().getPackageName() + "/databases/");

    private static final String SQL_GET_15_QUESTION = "select * from (select* from Question order by random()) group by level order by level limit 15";

    private Context context;

    private SQLiteDatabase sqLiteDatabase;

    public DatabaseManager(Context context) {
        this.context = context;
        copyDatabases();
        writeScore();
    }

    private void copyDatabases() {
        try {

            InputStream mInput = context.getAssets().open(DATABASE_NAME);
            String outFileName =  DATABASE_PATH + DATABASE_NAME;
            new File(DATABASE_PATH).mkdirs();
            OutputStream mOutput = new FileOutputStream(outFileName);
            byte[] mBuffer = new byte[1024];
            int mLength;
            while ((mLength = mInput.read(mBuffer)) > 0) {
                mOutput.write(mBuffer, 0, mLength);
            }
            mOutput.flush();
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            mOutput.close();
            mInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openDatabase() {
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            sqLiteDatabase = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME,
                    null, SQLiteDatabase.OPEN_READWRITE);
        }
    }

    private void closeDatabase() {
        if (sqLiteDatabase != null
                && sqLiteDatabase.isOpen()) {
            sqLiteDatabase.close();
        }
    }

    public void writeScore() {
        ContentValues values = new ContentValues();
        values.put("Name","tuyen");
        values.put("Score", 200000);
        insert("HighScore", values);
    }

    public ArrayList<HighScore> getHighScore() {
        openDatabase();
        String sql = "select * from HighScore ORDER BY Score DESC";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        if (cursor == null
                || cursor.getCount() == 0) {
            return null;
        }
        ArrayList<HighScore> highScores = new ArrayList<>();
        cursor.moveToFirst();
        String name;
        int score;
        while (cursor.isAfterLast() == false) {
            name = cursor.getString(cursor.getColumnIndex("Name"));
            score = cursor.getInt(cursor.getColumnIndex("Score"));

            highScores.add(new HighScore(name, score));
            cursor.moveToNext();
        }
        closeDatabase();
        return highScores;
    }

    public void insert(String tableName, ContentValues values) {
        openDatabase();
        sqLiteDatabase.insert(tableName, null, values);
        closeDatabase();
    }

    public void delete(String tableName, String whereClause, String[] whereArgs) {
        openDatabase();
        sqLiteDatabase.delete(tableName, whereClause, whereArgs);
        closeDatabase();
    }

    public void update(String tableName, ContentValues values, String whereClause, String[] whereArgs) {
        openDatabase();
        sqLiteDatabase.update(tableName, values, whereClause, whereArgs);
        closeDatabase();
    }

    public Question getQuestionByLevel(int lv) {
        openDatabase();
        String sql = "select * from Question where level = '" + lv + "' order by random()  limit 1";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        if (cursor == null
                || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();

        String question, caseA, caseB, caseC, caseD;
        int level, trueCase;

        question = cursor.getString(cursor.getColumnIndex("question"));
        caseA = cursor.getString(cursor.getColumnIndex("casea"));
        caseB = cursor.getString(cursor.getColumnIndex("caseb"));
        caseC = cursor.getString(cursor.getColumnIndex("casec"));
        caseD = cursor.getString(cursor.getColumnIndex("cased"));
        level = cursor.getInt(cursor.getColumnIndex("level"));
        trueCase = cursor.getInt(cursor.getColumnIndex("truecase"));
        Question question1 = new Question(question, caseA, caseB, caseC, caseD, trueCase, level);
        closeDatabase();
        return question1;
    }

    public ArrayList<Question> get15Questions() {
        openDatabase();
        ArrayList<Question> questions = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_GET_15_QUESTION, null);
        if (cursor == null
                || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            String q = cursor.getString(cursor.getColumnIndex("question"));
            String a = cursor.getString(cursor.getColumnIndex("casea"));
            String b = cursor.getString(cursor.getColumnIndex("caseb"));
            String c = cursor.getString(cursor.getColumnIndex("casec"));
            String d = cursor.getString(cursor.getColumnIndex("cased"));
            int t = cursor.getInt(cursor.getColumnIndex("truecase"));
            int l = cursor.getInt(cursor.getColumnIndex("level"));
            questions.add(new Question(q, a, b, c, d, t, l));
            cursor.moveToNext();
        }
        closeDatabase();
        return questions;
    }


}