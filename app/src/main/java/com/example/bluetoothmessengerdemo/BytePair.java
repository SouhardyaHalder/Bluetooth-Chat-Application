package com.example.bluetoothmessengerdemo;

import android.bluetooth.BluetoothAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class BytePair extends AppCompatActivity {
    private boolean element1;
    private BluetoothAdapter element2;

    public BytePair(boolean element1, BluetoothAdapter element2) {
        this.element1 = element1;
        this.element2 = element2;
    }

    public boolean getElement1() {
        return element1;
    }

    public BluetoothAdapter getElement2() {
        return element2;
    }
}
