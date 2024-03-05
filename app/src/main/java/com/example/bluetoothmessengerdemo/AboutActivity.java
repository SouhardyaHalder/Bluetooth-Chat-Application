package com.example.bluetoothmessengerdemo;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.BluetoothMessengerDemo.R;


public class AboutActivity extends AppCompatActivity {

    private static final String TAG = "[BT_Chat_App] AboutActivity -> ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate Entry");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Log.d(TAG, "onCreate Exit");
    }
}