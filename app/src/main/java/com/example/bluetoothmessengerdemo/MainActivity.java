package com.example.bluetoothmessengerdemo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.companion.CompanionDeviceManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import static android.content.ContentValues.TAG;
import com.example.BluetoothMessengerDemo.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "[BT_Chat_App] MainActivity -> ";
    public static final int REQUEST_BLUETOOTH_PERMISSION = 1;
    public static final int BLUETOOTH_REQUEST_CODE = 1;
    public static final int SELECT_DEVICE_REQUEST_CODE = 2;
    public static final int FIND_REQUEST = 3;
    public static final int REQUEST_DISCOVERABILITY = 4;

    private static String APP_NAME;
    private static final UUID APP_UUID = UUID.fromString("ab0e7500-9205-11ec-b909-0242ac120002");

    ChatDataBase chatDataBase = null;
    String name;
    AppCompatButton discoverBtn;
    TextView status;
    TextView notFoundText;
    ListView listView;
    ArrayList<String> bluetoothList = null;
    BluetoothDevice pairingDevice = null;

    final Handler handler = new Handler();

    ArrayList<BluetoothDevice> bluetoothDevices = null;
    FuncManager funcManager=new FuncManager();
    FuncManager.ListAdapter listAdapter = null;
    ChatListAdapter chatListAdapter = null;
   // FuncManager.ChatListAdapter chatListAdapter = null;
    private BluetoothAdapter bluetoothAdapter = null;

    ClientSocket clientSocket = null;
    ServerSocket serverSocket = null;
    IntentFilter intentFilter = null, intentFilter2 = null, intentFilter3 = null, intentFilter4 = null;
    Intent discoveryIntent = null;

    ConstraintLayout chatLayout = null, commandCenterLayout;
    TextView clientName;
    EditText enteredMsg;
    AppCompatButton sendMsgBtn, disconnectBtn;
    SendReceive sendReceive = null;
    ArrayList<String> chatList = null;
    ArrayList<Byte> chatPosition = null;
    ListView chatListView = null;
    DeviceActivity deviceActivity=new DeviceActivity(MainActivity.this);
    String targetName;
    ChatActivity chatActivity=new ChatActivity(MainActivity.this);
    GetBoundedDevices getBoundedDevicesVar=new GetBoundedDevices(MainActivity.this);
    private static final int MESSAGE_READ = 1;
    private static final int MESSAGE_WRITE = 2;
    private static final int MESSAGE_TOAST = 3;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate Entry");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = getIntent().getStringExtra("name");
        Objects.requireNonNull(getSupportActionBar()).setTitle(name);

        // taking permission to access location ...

        initializeVariables();
        setListeners();

        // checking whether the bluetooth is available or not in the device
        BytePair result = deviceActivity.checkBluetoothCompatibility();
        bluetoothAdapter=result.getElement2();
        if (result.getElement1()) {                                     //      this is actually THE RESULT OF checkBluetoothCompatibility() METHOD
            deviceActivity.requestEnableBluetooth(bluetoothAdapter);    //  Class DeviceActivity
        } else {
            return;
        }
        registerBroadcastReceivers();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            deviceActivity.requestFindDevicesPermission();  //Class DeviceActivity
        }
        // starts server thread by default until client clicks to connect a certain device to connect
        // when client try to connect with a device , serverSocket is stopped

        //getBoundedDevices(); // gets the previously bounded devices
        getBoundedDevicesVar.getBoundedDevices(bluetoothAdapter,bluetoothList,notFoundText,bluetoothDevices,listAdapter);
        stopEverything();
        if (name.equals("Client")) {
            discoverBtn.setVisibility(View.VISIBLE);
            enableDiscoverability(null);
            try {
                serverSocket.interrupt();
            } catch (Exception ignored) {
            }

            startServerSocket();
        } else {
            startDiscoveryOfDevices();
        }
        Log.d(TAG, "onCreate Exit");

    }

    public void enableDiscoverability(View view) {
        Log.d(TAG, "enableDiscoverability Entry");
        discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(discoveryIntent, REQUEST_DISCOVERABILITY);
        Log.d(TAG, "enableDiscoverability Exit");
    }

    private void startServerSocket() {
        Log.d(TAG, "startServerSocket Entry");
        serverSocket = new ServerSocket();
        serverSocket.start();
        Log.d(TAG, "startServerSocket Exit");
    }

    private void initializeVariables() {
        Log.d(TAG, "initializeVariables Entry");

        APP_NAME = getString(R.string.app_name);

        discoverBtn = findViewById(R.id.discoverBtn);
        listView = findViewById(R.id.boundedDevicesList);
        status = findViewById(R.id.status);
        notFoundText = findViewById(R.id.notFoundText);

        bluetoothList = new ArrayList<>();
        bluetoothDevices = new ArrayList<>();
        listAdapter = funcManager.new ListAdapter(bluetoothList,MainActivity.this);
        listView.setAdapter(listAdapter);
        chatLayout = findViewById(R.id.chatLayout);
        commandCenterLayout = findViewById(R.id.commandCenterView);
        clientName = findViewById(R.id.chatClientName);
        sendMsgBtn = findViewById(R.id.sendMsg);
        disconnectBtn = findViewById(R.id.disconnectBtn);
        enteredMsg = findViewById(R.id.enteredMsg);
        chatListView = findViewById(R.id.chatListView);
        chatList = new ArrayList<>();
        chatPosition = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(chatList);
        //chatListAdapter = funcManager.new ChatListAdapter(chatList,chatPosition,MainActivity.this);
        chatListView.setAdapter(chatListAdapter);
        chatLayout.setVisibility(View.GONE);
        commandCenterLayout.setVisibility(View.VISIBLE);
        discoverBtn.setVisibility(View.GONE);

        Log.d(TAG, "initializeVariables Exit");

    } // used to initialize variables

    private void setListeners() {
        Log.d(TAG, "setListeners Entry");
        listView.setOnItemClickListener(listViewListener);
        listView.setOnItemLongClickListener(listViewLongListener);
        Log.d(TAG, "setListeners Exit");

    }

    AdapterView.OnItemClickListener listViewListener = (parent, view, position, id) -> {
        Log.d(TAG, "listViewListener Callback Entry");

        if (name.equals("Server")) {
            BytePair result = deviceActivity.checkBluetoothCompatibility();
            bluetoothAdapter=result.getElement2();
            if (result.getElement1()) { // here actually checkBluetoothCompatibility() bool value is checked
                deviceActivity.requestEnableBluetooth(bluetoothAdapter);
            } else {
                return;
            }
            if (deviceActivity.checkDiscoverState(bluetoothAdapter)) {
                tempToast("Please wait while scanning...", 0);
                return;
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (bluetoothAdapter.isEnabled()) {

                    clientSocket = new ClientSocket(bluetoothDevices.get(position)); // creates a client version
                    clientSocket.start(); // starts the thread
                }
            } else {
                deviceActivity.requestFindDevicesPermission();
            }
        } else {
            tempToast("Only host can select the device", 1);
        }
        Log.d(TAG, "listViewListener Callback Exit");
    }; // for list view
    AdapterView.OnItemLongClickListener listViewLongListener = (parent, view, position, id) -> {
        Log.d(TAG, "listViewLongListener Callback Entry");

        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            chatActivity.clearChat(bluetoothList.get(position));
            return true;
        });

        popupMenu.show();

        Log.d(TAG, "listViewLongListener Callback Exit");
        return true;
    };

    // permission area
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult Entry");

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FIND_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tempToast("permission granted for finding devices", 1);
                startDiscoveryOfDevices();
            } else {
                tempToast("Permission denied for finding devices", 1);
            }
        }
        Log.d(TAG, "onRequestPermissionsResult Exit");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult Entry");

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DISCOVERABILITY) {

            if (resultCode == RESULT_CANCELED) {
                tempToast("Please make your device discoverable", 0);
            } else {
                tempToast("Discoverable for 5 minutes", 1);

            }
        }
        if (requestCode == BLUETOOTH_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //getBoundedDevices();
                getBoundedDevicesVar.getBoundedDevices(bluetoothAdapter,bluetoothList,notFoundText,bluetoothDevices,listAdapter);
            } else {
                tempToast("Please enable Bluetooth", 1);
            }
        }

        if (requestCode == SELECT_DEVICE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                pairingDevice = data.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE);
            }

            if (pairingDevice != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    tempToast("Permission denied to create bond", 1);
                    return;
                }
                pairingDevice.createBond();
            }
        }
        Log.d(TAG, "onActivityResult Exit");
    }

    // used for discovering devices
    private void startDiscoveryOfDevices() {
        Log.d(TAG, "startDiscoveryOfDevices Entry");

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (bluetoothAdapter.startDiscovery()) {
                //getBoundedDevices();
                getBoundedDevicesVar.getBoundedDevices(bluetoothAdapter,bluetoothList,notFoundText,bluetoothDevices,listAdapter);
                tempToast("Scanning current Location", 1);
                setTheStatus("Scanning...");
            }
        }
        Log.d(TAG, "startDiscoveryOfDevices Exit");
    }

    private void setTheStatus(String msg) {
        Log.d(TAG, "setTheStatus Entry");
        String txt = "Status: " + msg;
        status.setText(txt);
        Log.d(TAG, "setTheStatus Exit");
    }
    //  permission area  completed

    private void tempToast(String message, int time) {
        Log.d(TAG, "tempToast Entry");

        if (time == 0) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
        Log.d(TAG, "tempToast Exit");

    } // this is used for toast messages

    // adapters // ListAdapter was only possible to modularise its in FuncManager
    public class ChatListAdapter extends BaseAdapter {
        private static final String TAG = "[BT_Chat_App] MainActivity -> ChatListAdapter -> ";

        ArrayList<String> list;

        public ChatListAdapter(ArrayList<String> list) {
            Log.d(TAG, "ChatListAdapter Entry");
            this.list = list;
            Log.d(TAG, "ChatListAdapter Exit");

        }

        @Override
        public int getCount() {
            Log.d(TAG, "getCount chatListAdapter");

            return list.size();
        }

        @Override
        public Object getItem(int position) {
            Log.d(TAG, "getItem chatListAdapter");

            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            Log.d(TAG, "getItem Id chatListAdapter");

            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(TAG, "getView Entry");

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.message_list_view_model, parent, false);
            }

            TextView textView = convertView.findViewById(R.id.messageText);

            LinearLayout.LayoutParams textParams = (LinearLayout.LayoutParams) textView.getLayoutParams();

            if (chatPosition.get(position) == (byte) 1) {
                textParams.gravity = Gravity.CENTER | Gravity.START;
                textView.setBackgroundResource(R.drawable.host_chat_background);
            } else {
                textParams.gravity = Gravity.CENTER | Gravity.END;
                textView.setBackgroundResource(R.drawable.client_chat_background);
            }

            textView.setLayoutParams(textParams);
            textView.setText((String) getItem(position));
            Log.d(TAG, "getView Exit");

            return convertView;
        }
    }
    // adapters  completed //

    // used to register receivers
    private void registerBroadcastReceivers() {
        Log.d(TAG, "registerBroadcastReceivers Entry");

        intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        intentFilter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter3 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter4 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(foundReceiver, intentFilter);
        registerReceiver(deviceDisconnectReceiver, intentFilter2);
        registerReceiver(discoveryReceiver, intentFilter3);
        registerReceiver(stateChangeReceiver, intentFilter4);

        Log.d(TAG, "registerBroadcastReceivers Exit");
    }

    // broadcast receiver which receives when it found devices //

    private final BroadcastReceiver foundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            Log.d(TAG, "foundReceiver onReceive Entry");

            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                pairingDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    sendMessageToUi("Permission denied to get name in receiver");
                    return;
                }

                String name = pairingDevice.getName();

                if (name != null && !bluetoothList.contains(name)) {
                    bluetoothList.add(name);
                    bluetoothDevices.add(pairingDevice);
                    listAdapter.notifyDataSetChanged();
                    notFoundText.setVisibility(View.GONE);
                }

            }
            Log.d(TAG, "foundReceiver onReceive Exit");
        }
    };

    private final BroadcastReceiver deviceDisconnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            Log.d(TAG, "deviceDisconnectReceiver onReceive Entry");

            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                int st = intent.getIntExtra(BluetoothDevice.ACTION_ACL_DISCONNECTED, -1);

                if (st != BluetoothDevice.ERROR) {

                    handler.post(() -> {

                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            if (bluetoothAdapter.isDiscovering()) {
                                deviceActivity.setTheStatus("Scanning...",status);
                            }
                        } else {
                            deviceActivity.setTheStatus("Active|Neutral",status);
                        }
                    });

                    chatLayout.setVisibility(View.GONE);
                    commandCenterLayout.setVisibility(View.VISIBLE);
                }
            }
            Log.d(TAG, "deviceDisconnectReceiver onReceive Exit");
        }
    };

    private final BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            Log.d(TAG, "discoveryReceiver onReceive Entry");

            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                handler.post(() -> {
                    if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                        tempToast("Scan completed...", 1);
                        deviceActivity.setTheStatus("Active | Neutral",status);

                    }

                });
            }
            Log.d(TAG, "discoveryReceiver onReceive Exit");
        }
    };

    private final BroadcastReceiver stateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            Log.d(TAG, "stateChangeReceiver onReceive Entry");

            String action = intent.getAction();
            String msg = null, toastMsg = null;

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int ste = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

                switch (ste) {
                    case BluetoothAdapter.STATE_OFF:
                        // use a handler to send info as Inactive
                        msg = "Inactive";
                        toastMsg = "Bluetooth turned OFF";

                        break;
                    case BluetoothAdapter.STATE_ON:
                        // user a handler to send info Active|neutral
                        msg = "Active|Neutral";
                        toastMsg = "Bluetooth turned ON";

                        break;
                }

                if (msg != null) {
                    final String finalMsg = toastMsg;
                    final String finalStatus = msg;
                    handler.post(() -> {
                        tempToast(finalMsg, 1);
                        deviceActivity.setTheStatus(finalStatus,status);
                    });
                }
            }
            Log.d(TAG, "stateChangeReceiver onReceive Exit");
        }
    };
    // broadcast receiver which receives when it found devices

    // server thread
    private class ServerSocket extends Thread {
        private final BluetoothServerSocket serverSocket;

        public ServerSocket() {
            BluetoothServerSocket tmp = null;

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                deviceActivity.requestFindDevicesPermission();
            }
            try {

                if (bluetoothAdapter.isEnabled()) {
                    tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, APP_UUID);
                } else {
                    sendMessageToUi("Please enable Bluetooth");
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            serverSocket = tmp;
        }
        public void run() {
            BluetoothSocket socket = null;

            while (true) {
                try {
                    if (bluetoothAdapter.isEnabled() && serverSocket != null) {
                        socket = serverSocket.accept();
                        sendReceive = new SendReceive(socket);
                        sendReceive.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (socket != null) {
                    BluetoothSocket finalSocket = socket;
                    handler.post(() -> {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            targetName = finalSocket.getRemoteDevice().getName();

                            startChatting();
                        }
                    });
                    cancel();
                    break;
                }
            }
            // do something to interact with device
        }
        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                sendMessageToUi("Failed to close server");
            }
        }
    }

    // client thread
    private class ClientSocket extends Thread {
        private final BluetoothSocket bluetoothSocket;

        public ClientSocket(BluetoothDevice bluetoothDevice) {
            handler.post(() -> tempToast("Connecting...", 1));
            BluetoothSocket tmp = null;

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                deviceActivity.requestFindDevicesPermission();

            }

            targetName = bluetoothDevice.getName();

            try {
                if (bluetoothAdapter.isEnabled()) {
                    tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(APP_UUID);
                } else {
                    sendMessageToUi("Please enable Bluetooth");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendMessageToUi("Failed to create connection in client side");
            }
            bluetoothSocket = tmp;

        }
        public void run() {

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                deviceActivity.requestFindDevicesPermission();
            }
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
            } else {
                sendMessageToUi("Access denied to location: please enable");
            }

            try {
                bluetoothSocket.connect();

                handler.post(() -> {
                    deviceActivity.setTheStatus("connected",status);
                    startChatting();
                });

                sendReceive = new SendReceive(bluetoothSocket);
                sendReceive.start();

            } catch (Exception e) {
                e.printStackTrace();
                sendMessageToUi("Error: Go back restart Host and Client on either side");
                if (serverSocket != null) {
                    serverSocket.cancel();
                }
                cancel();
            }
            // do something to interact with device
        }
        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                sendMessageToUi("Failed to close client Socket");
            }

        }
    }

    // message handler
    Handler messageHandler = new Handler(msg -> {
        Log.d(TAG, "messageHandler Entry");
        String message;
        byte[] bytes;

        switch (msg.what) {
            case MESSAGE_READ:
                // read data
                bytes = (byte[]) msg.obj;
                message = new String(bytes, 0, msg.arg1);
                chatList.add(message);
                chatPosition.add((byte) 1);
                //chatListAdapter.getChatPosition().add((byte) 1);
                break;
            case MESSAGE_WRITE:
                break;
            case MESSAGE_TOAST:
                // toast message
                break;
        }

        chatListView.setSelection(chatListAdapter.getCount() - 1);
        chatListAdapter.notifyDataSetChanged();
        Log.d(TAG, "messageHandler Exit");

        return true;
    });

    private void startChatting() {
        Log.d(TAG, "startChatting Entry");

        commandCenterLayout.setVisibility(View.GONE);
        chatLayout.setVisibility(View.VISIBLE);

        chatActivity.setTheChats(chatList, chatPosition, targetName);       //  Class ChatActivity
        //chatActivity.setTheChats(chatList, chatListAdapter.getChatPosition(), targetName);
        if (targetName == null || targetName.equals("")) {
            targetName = "username restricted";
        }
        if (targetName.length() > 15) {
            targetName = targetName.substring(0, 15);
        }
        String tempName = "Target : " + targetName;
        clientName.setText(tempName);

        Log.d(TAG, "startChatting Exit");

    }

    public void sendMsgToUserBtn(View view) {
        // write data......
        Log.d(TAG, "sendMsgToUserBtn Entry");

        String msgTxt;
        try {
            msgTxt = enteredMsg.getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
            sendMessageToUi("Please enter message");
            return;
        }

        msgTxt = msgTxt.trim();
        if (msgTxt.equals("")) {
            sendMessageToUi("Please enter message");
            return;
        }

        chatList.add(msgTxt);
        byte[] bytes = msgTxt.getBytes();

        sendReceive.writeMessage(bytes);
        enteredMsg.setText(""); // clearing the message
        chatPosition.add((byte) 2);
        //chatListAdapter.getChatPosition().add((byte) 2);
        Log.d(TAG, "sendMsgToUserBtn Exit");
    }

    public void disconnectTheChat(View view) {
        Log.d(TAG, "disconnectTheChat Entry");
        if (sendReceive != null) {
            sendReceive.cancel();
            chatLayout.setVisibility(View.GONE);
            commandCenterLayout.setVisibility(View.VISIBLE);
            stopEverything();
            finish();
        }
        Log.d(TAG, "disconnectTheChat Exit");

    }

    private void sendMessageToUi(String msg) {
        Log.d(TAG, "sendMessageToUi Entry");
        handler.post(() -> tempToast(msg, 1));
        Log.d(TAG, "sendMessageToUi Exit");
    }

    private class SendReceive extends Thread {
        private static final String TAG = "[BT_Chat_App] MainActivity -> SendReceive -> ";

        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        private byte[] buffer;

        public SendReceive(BluetoothSocket bluetoothSocket) {
            Log.d(TAG, "SendReceive Entry");
            this.bluetoothSocket = bluetoothSocket;

            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
            } catch (Exception e) {
                e.printStackTrace();
                sendMessageToUi("Error getting InputStream");
            }

            try {
                tempOut = bluetoothSocket.getOutputStream();
            } catch (Exception e) {
                e.printStackTrace();
                sendMessageToUi("Error writing dataStream");
            }

            inputStream = tempIn;
            outputStream = tempOut;
            Log.d(TAG, "SendReceive Exit");
        }

        public void run() {
            Log.d(TAG, "run Entry");

            buffer = new byte[1024];
            int numOfBytes;

            while (true) {

                try {
                    numOfBytes = inputStream.read(buffer);
                    Message message = messageHandler.obtainMessage(MESSAGE_READ, numOfBytes, -1, buffer);
                    message.sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(() -> {
                        tempToast("User disconnected...", 1);
                        deviceActivity.setTheStatus("Active|Neutral",status);
                        commandCenterLayout.setVisibility(View.VISIBLE);
                        chatLayout.setVisibility(View.GONE);
                    });
                    if (name.equals("Client")) {
                        startServerSocket();
                    }
                    chatActivity.saveTheChats(chatList,chatPosition,handler,targetName);
                    //chatActivity.saveTheChats(chatList,chatListAdapter.getChatPosition(),handler,targetName);
                    stopEverything();

                    break;
                }
            }
            Log.d(TAG, "run Exit");
        }

        public void writeMessage(byte[] bytes) {
            Log.d(TAG, "writeMessage Entry");

            try {
                outputStream.write(bytes);
                Message message = messageHandler.obtainMessage(MESSAGE_WRITE, bytes.length, -1, buffer);
                message.sendToTarget();

            } catch (Exception e) {
                e.printStackTrace();

                sendMessageToUi("Error occurred while sending data...");
            }
            Log.d(TAG, "writeMessage Exit");
        }

        public void cancel() {
            Log.d(TAG, "cancel Entry");

            try {
                bluetoothSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
                sendMessageToUi("Error closing the socket...");
            }
            Log.d(TAG, "cancel Exit");
        }
    }

    private void stopEverything() {
        Log.d(TAG, "stopEverything Entry");
        try {
            serverSocket.cancel();
            serverSocket.interrupt();
            clientSocket.cancel();
            clientSocket.interrupt();
        } catch (Exception ignored) {
        }
        Log.d(TAG, "stopEverything Exit");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed Entry");
        unregisterAll();
        stopEverything();

        super.onBackPressed();
        finish();
        Log.d(TAG, "onBackPressed Exit");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy Entry");
        unregisterAll();
        stopEverything();
        super.onDestroy();
        Log.d(TAG, "onDestroy Exit");

    }

    private void unregisterAll() {
        Log.d(TAG, "unregisterAll Entry");
        try {
            sendReceive.cancel();
            unregisterReceiver(foundReceiver);
            unregisterReceiver(discoveryReceiver);
            unregisterReceiver(stateChangeReceiver);
            unregisterReceiver(discoveryReceiver);
        } catch (Exception ignored) {

        }
        Log.d(TAG, "unregisterAll Exit");
    }
}