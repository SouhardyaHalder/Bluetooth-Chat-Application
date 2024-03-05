package com.example.bluetoothmessengerdemo;

import static android.content.ContentValues.TAG;

import static com.example.bluetoothmessengerdemo.MainActivity.REQUEST_BLUETOOTH_PERMISSION;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Set;

public class GetBoundedDevices extends AppCompatActivity {
    Context context;
    public GetBoundedDevices(){}
    public GetBoundedDevices(Context context){
        this.context=context;
    }
    public void getBoundedDevices(BluetoothAdapter bluetoothAdapter, ArrayList<String> bluetoothList, TextView notFoundText,ArrayList<BluetoothDevice> bluetoothDevices,FuncManager.ListAdapter listAdapter) {
        Log.d(TAG, "getBoundedDevices Entry");
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH}, REQUEST_BLUETOOTH_PERMISSION);
        Log.d(TAG, "asked for request");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();

            if (bt.size() > 0) {

                for (BluetoothDevice b : bt) {
                    BluetoothClass bluetoothClass = b.getBluetoothClass();
                    int deviceClass = bluetoothClass.getDeviceClass();
                    if (bluetoothClass.getMajorDeviceClass() == BluetoothClass.Device.Major.PHONE &&
                            deviceClass != BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE &&
                            bluetoothClass.getDeviceClass() != BluetoothClass.Device.PHONE_SMART &&
                            bluetoothClass.getDeviceClass() != BluetoothClass.Device.PHONE_CELLULAR) {
                        if (bluetoothList.contains(b.getName())) continue;
                        bluetoothList.add(b.getName());
                        bluetoothDevices.add(b);
                    }
                }
                listAdapter.notifyDataSetChanged();
                notFoundText.setVisibility(View.GONE);

            } else {
                notFoundText.setVisibility(View.VISIBLE);
            }
            Log.d(TAG, "getBoundedDevices Exit");
        }
    }
}
