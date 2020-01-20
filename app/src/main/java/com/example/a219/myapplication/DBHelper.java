package com.example.a219.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


//즐겨찾기에 사용할 DB이다
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE MYLIST (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, director TEXT, date TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insert(String title, String director, String date) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO MYLIST VALUES(null, '" + title + "', " + director + ", '" + date + "');");
        db.close();
    }
    public void delete(String title) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM MYLIST WHERE title='" + title + "';");
        db.close();
    }



    public String getResult() {
        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        Cursor cursor = db.rawQuery("SELECT * MYLIST", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(1)
                    + "\n"
                    + cursor.getString(2)
                    + "\n"
                    + cursor.getString(3)
                    + " 등록";

        }

        return result;
    }

}
