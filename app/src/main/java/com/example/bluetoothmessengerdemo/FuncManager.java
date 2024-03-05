package com.example.bluetoothmessengerdemo;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.BluetoothMessengerDemo.R;
import java.util.ArrayList;

public class FuncManager extends AppCompatActivity {
    public class ListAdapter extends BaseAdapter{
        private static final String TAG = "[BT_Chat_App] MainActivity -> ListAdapter -> ";

        ArrayList<String> list;
        Context context;
        public ListAdapter(ArrayList<String> list,Context context) {
            Log.d(TAG, "ListAdapter Entry");
            this.list = list;
            this.context=context;
            Log.d(TAG, "ListAdapter Exit");

        }

        @Override
        public int getCount() {
            Log.d(TAG, "getCount 1 ListAdapter");
            return list.size();
        }
        @Override
        public Object getItem(int position) {
            Log.d(TAG, "getItem ListAdapter");
            return list.get(position);
        }
        @Override
        public long getItemId(int position) {
            Log.d(TAG, "getItemId ListAdapter");
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(TAG, "getView Entry");

            if (convertView == null) {
               // convertView = getLayoutInflater().inflate(R.layout.list_view_model, parent, false);
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.list_view_model, parent, false);
            }

            TextView textView = convertView.findViewById(R.id.deviceName);
            textView.setText((String) getItem(position));
            Log.d(TAG, "getView Exit");

            return convertView;
        }
    }

    public class ChatListAdapter extends BaseAdapter {
        private static final String TAG = "[BT_Chat_App] MainActivity -> ChatListAdapter -> ";

        ArrayList<String> list;
        ArrayList<Byte> chatPosition = null;
        Context context;
        public ChatListAdapter(ArrayList<String> list,ArrayList<Byte> chatPosition,Context context) {
            Log.d(TAG, "ChatListAdapter Constructor Entry");
            this.list = list;
            this.chatPosition=chatPosition;
            this.context=context;
            Log.d(TAG, "ChatListAdapter Constructor Exit");
        }

        public ArrayList<Byte> getChatPosition() {
            return chatPosition;
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
                //convertView = getLayoutInflater().inflate(R.layout.message_list_view_model, parent, false);
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.list_view_model, parent, false);
            }

            TextView textView = convertView.findViewById(R.id.messageText);

            if (textView.getLayoutParams() == null) {
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                textView.setLayoutParams(textParams);
            }

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
}
