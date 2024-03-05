package com.example.bluetoothmessengerdemo;

import static android.content.ContentValues.TAG;

import static com.example.bluetoothmessengerdemo.MainActivity.BLUETOOTH_REQUEST_CODE;
import static com.example.bluetoothmessengerdemo.MainActivity.FIND_REQUEST;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class DeviceActivity extends AppCompatActivity {
    Toasts t=new Toasts();
    Context context;
    public DeviceActivity(){

    }
    public DeviceActivity(Context context){
        this.context=context;
    }
    public boolean checkDiscoverState(BluetoothAdapter bluetoothAdapter) {
        Log.d(TAG, "checkDiscoverState Entry");

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestFindDevicesPermission();
            return true;
        }
        Log.d(TAG, "checkDiscoverState Exit");

        return bluetoothAdapter.isDiscovering();
    }
    final void requestFindDevicesPermission() {
        Log.d(TAG, "requestFindDevicesPermission Entry");

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(context)
                    .setTitle("Permission needed")
                    .setMessage("requires access to location to find devices")
                    .setPositiveButton("ok", (dialog, which) -> ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FIND_REQUEST))
                    .setNegativeButton("cancel", (dialog, which) -> {
                        t.tempToast(context,"Permission cancelled", 1);
                        dialog.dismiss();
                    }).create().show();
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FIND_REQUEST);
        }
        Log.d(TAG, "requestFindDevicesPermission Exit");

    }
    public BytePair checkBluetoothCompatibility() {
        Log.d(TAG, "checkBluetoothCompatibility Entry");

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            t.tempToast(context, "Error: Unsupported device", 1);
            return new BytePair(false, null);
        }
        Log.d(TAG, "checkBluetoothCompatibility Exit");
        return new BytePair(true, bluetoothAdapter);
    }
    public void setTheStatus(String msg, TextView status) {
        Log.d(TAG, "setTheStatus Entry");
        String txt = "Status: " + msg;
        status.setText(txt);
        Log.d(TAG, "setTheStatus Exit");
    }
    public void requestEnableBluetooth(BluetoothAdapter bluetoothAdapter) {
        Log.d(TAG, "requestEnableBluetooth Entry");

        if (!bluetoothAdapter.isEnabled()) {
            Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(bluetoothIntent, BLUETOOTH_REQUEST_CODE);
            }
        }
        Log.d(TAG, "requestEnableBluetooth Exit");
    }
//    void startDiscoveryOfDevices(BluetoothAdapter bluetoothAdapter,GetBoundedDevices getBoundedDevicesVar,TextView status) {
//        Log.d(TAG, "startDiscoveryOfDevices Entry");
//
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            if (bluetoothAdapter.startDiscovery()) {
//                //getBoundedDevices();
//                getBoundedDevicesVar.getBoundedDevices(bluetoothAdapter,bluetoothList,notFoundText,bluetoothDevices,listAdapter);
//                t.tempToast(context,"Scanning current Location", 1);
//                setTheStatus("Scanning...",status);
//            }
//        }
//        Log.d(TAG, "startDiscoveryOfDevices Exit");
//    }
}
