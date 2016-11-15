package com.example.gabriel.vinyl.content.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.gabriel.vinyl.content.User;

/**
 * Created by Gabriel on 11/15/2016.
 */

public class VinylDatabase extends SQLiteOpenHelper {

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_TOKEN = "token";

    private static final String DATABASE_NAME = "vinyl.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_USERS + "( " + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_TOKEN + " text not null);";


    public VinylDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(sqLiteDatabase);
    }

    public void saveUser(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TOKEN, user.getToken());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_USERS, null, contentValues);
        db.close();
    }

    public User getCurrentUser() {
        Cursor c = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            c = getReadableDatabase().rawQuery("select token from " + TABLE_USERS, null, null);
        }
        if (c.moveToFirst()) {
            return new User(null, null, c.getString(0));
        } else {
            return null;
        }
    }
}
