package com.example.bluetoothmessengerdemo;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Toasts extends AppCompatActivity{
    void tempToast(Context context, String message, int time) {
        Log.d(TAG, "tempToast Entry");

        if (time == 0) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context , message, Toast.LENGTH_LONG).show();
        }
        Log.d(TAG, "tempToast Exit");
    }
}
