package com.example.bluetoothmessengerdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import com.example.BluetoothMessengerDemo.R;

public class MainActivity2 extends AppCompatActivity {
    private static final String TAG = "[BT_Chat_App] MainActivity222 -> ";

    private static final int FIND_REQUEST = 3;

    AppCompatButton hostBtn, clientBtn;
    Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate Entry");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestFindDevicesPermission();
        }


        hostBtn = findViewById(R.id.hostBtn);
        clientBtn = findViewById(R.id.clientBtn);
        intent = new Intent(MainActivity2.this, MainActivity.class);

        Log.d(TAG, "onCreate Exit");
    }

    public void HOSTBtn(View view) {
        Log.d(TAG, "ServerBtn Entry");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestFindDevicesPermission();
        } else {
            intent.putExtra("name", "Server");
            startActivity(intent);
        }

        Log.d(TAG, "ServerBtn Exit");
    }

    public void CLIENTBtn(View view) {
        Log.d(TAG, "CLIENTBtn Entry");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestFindDevicesPermission();
        } else {
            intent.putExtra("name", "Client");
            startActivity(intent);
        }

        Log.d(TAG, "CLIENTBtn Exit");
    }


    final void requestFindDevicesPermission() {
        Log.d(TAG, "requestFindDevicesPermission Entry");

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity2.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(MainActivity2.this)
                    .setTitle("Permission needed")
                    .setMessage("requires access to location to find devices")
                    .setPositiveButton("ok", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FIND_REQUEST))
                    .setNegativeButton("cancel", (dialog, which) -> {
                        tempToast();
                        dialog.dismiss();
                    }).create().show();
        } else {

            ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FIND_REQUEST);
        }

        Log.d(TAG, "requestFindDevicesPermission Exit");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult Entry");

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FIND_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "permission granted for finding devices", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied for finding devices", Toast.LENGTH_LONG).show();
            }
        }

        Log.d(TAG, "onRequestPermissionsResult Exit");

    }

    private void tempToast() {
        Log.d(TAG, "tempToast Entry");

        Toast.makeText(this, "Permission cancelled", Toast.LENGTH_LONG).show();

        Log.d(TAG, "tempToast Exit");
    } // this is used for toast messages

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu Entry");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        Log.d(TAG, "onCreateOptionsMenu Exit");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected Entry");

        startActivity(new Intent(MainActivity2.this, AboutActivity.class));

        Log.d(TAG, "onOptionsItemSelected Exit");

        return true;
    }
}