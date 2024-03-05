package com.example.bluetoothmessengerdemo;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import static android.content.ContentValues.TAG;

public class ChatActivity extends AppCompatActivity {
    private Context context;
    private Toasts t=new Toasts();
    public ChatActivity(){
    }
    public ChatActivity(Context context) {
        this.context = context;
    }
    public void setTheChats(ArrayList<String> chatList,ArrayList<Byte> chatPosition,String targetName){
        Log.d(TAG, "setTheChats Entry");

        ChatDataBase chatDataBase = new ChatDataBase(context);
        Cursor cursor = chatDataBase.getData();

        String totalChats = null;
        String[] totalChatArray;

        if (cursor.getCount() == 0) return;

        chatList.clear();
        chatPosition.clear();

        try {
            while (cursor.moveToNext()) {
                if (cursor.getString(0).equals(targetName)) {
                    totalChats = cursor.getString(1);
                    break;
                }
            }
            if (totalChats != null) {

                totalChatArray = totalChats.split("₧");

                for (String s : totalChatArray) {
                    int position = -1;
                    try {
                        String p = s.charAt(0) + "";
                        position = Integer.parseInt(p);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (position != -1) {
                        chatPosition.add((byte) position);
                        chatList.add(s.substring(1));
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        chatDataBase.close();
        Log.d(TAG, "setTheChats Exit");
    }
    public void saveTheChats(ArrayList<String> chatList,ArrayList<Byte> chatPosition,Handler handler,String targetName){
        Log.d(TAG, "saveTheChats Entry");

        ChatDataBase chatDataBase = new ChatDataBase(context);

        StringBuilder totalChats = new StringBuilder();
        String tempChat = "";

        for (int i = 0; i < chatList.size(); i++) {
            tempChat = chatPosition.get(i) + chatList.get(i) + "₧";
            totalChats.append(tempChat);

        }

        if (chatDataBase.insertData(targetName, totalChats.toString())) {
            sendMessageToUi("Chats saved...",handler,context);
        } else {
            if (chatDataBase.updateData(targetName, totalChats.toString())) {
                sendMessageToUi("Chats updated...",handler,context);
            } else {
                sendMessageToUi("Error saving chats...",handler,context);
            }
        }

        chatDataBase.close();
        Log.d(TAG, "saveTheChats Exit");
    }
    public void clearChat(String name){
        Log.d(TAG, "clearChat Entry");

        ChatDataBase chatDataBase = new ChatDataBase(context);

        if (chatDataBase.deleteSpecific(name)) {
            t.tempToast(context,"Chat cleared...", 1);
        } else {
            t.tempToast(context,"Error deleting chats...", 1);
        }
        Log.d(TAG, "clearChat Exit");
    }
    public void sendMessageToUi(String msg, Handler handler,Context context) {
        Log.d(TAG, "sendMessageToUi Entry");
        handler.post(() -> t.tempToast(context , msg, 1));
        Log.d(TAG, "sendMessageToUi Exit");
    }
}
