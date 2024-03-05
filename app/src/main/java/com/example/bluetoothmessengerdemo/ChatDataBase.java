package com.example.bluetoothmessengerdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ChatDataBase extends SQLiteOpenHelper {

    //Log Tag :
    private static final String TAG = "[BT_Chat_App] ChatDataBase -> ";

    public ChatDataBase(@Nullable Context context) {
        super(context, "Chat.db", null, 1);
        Log.d(TAG, "ChatDataBase Constructor");
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        Log.d(TAG, "onCreate Entry");
        db.execSQL("Create Table Chat(name TEXT primary key, chats TEXT)");
        Log.d(TAG, "onCreate Exit");
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade Exit");
        db.execSQL("Drop Table if exists Chat");
        Log.d(TAG, "onUpgrade Exit");
    }


    public boolean insertData(String name, String chats) {
        Log.d(TAG, "insertData Entry");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", name);
        cv.put("chats", chats);

        long result = 0;
        try {
            result = db.insert("Chat", null, cv);
        } catch (Exception ignored) {}
        Log.d(TAG, "insertData Exit");
        return result != -1;
    }

    public boolean updateData(String name, String chats) {
        Log.d(TAG, "updateData Entry");

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("chats", chats);

        long result = db.update("Chat", cv, "name=?", new String[]{name});
        Log.d(TAG, "updateData Exit");

        return result != -1;
    }


    public Cursor getData() {
        Log.d(TAG, "getData Entry");
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "getData Exit");

        return db.rawQuery("Select * from Chat", null);
    }

    public boolean deleteSpecific(String name){
        Log.d(TAG, "deleteSpecific Entry");

        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete("Chat","name=?",new String[]{name});
        Log.d(TAG, "deleteSpecific Entry");

        return result != -1;
    }


}
