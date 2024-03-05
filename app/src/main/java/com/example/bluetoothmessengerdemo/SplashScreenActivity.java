package com.example.bluetoothmessengerdemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.BluetoothMessengerDemo.R;

import java.util.Objects;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = "[BT_Chat_App] SplashScreenActivity -> ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate Entry");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Thread(){
            @RequiresApi(api = Build.VERSION_CODES.S)
            public void run(){
                try{
                    sleep(2500);
                }catch (Exception ignored){

                }finally {
                    startActivity(new Intent(SplashScreenActivity.this,MainActivity2.class));
                    finish();
                }
            }
        }.start();

        Log.d(TAG, "onCreate Exit");
    }
}