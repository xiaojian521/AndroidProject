package com.xj.demosets;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BlutoothActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "xjtest";

    private Context mContext;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiorecord);

        mContext = getApplicationContext();
        initWidget();
        registBlutoothDeviceState();
    }

    public void initWidget() {
        mTextView = (TextView)findViewById(R.id.textView);
        mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }

    public void registBlutoothDeviceState() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(new BluetoothMonitorReceiver(), intentFilter);
    }

    public class BluetoothMonitorReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action != null){
                switch (action) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        StringBuilder text = new StringBuilder();
                        int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                        switch (blueState) {
                            case BluetoothAdapter.STATE_TURNING_ON:
                                Log.e(TAG,"蓝牙正在打开");
                                text.append("蓝牙正在打开.");
                                break;
                            case BluetoothAdapter.STATE_ON:
                                Log.e(TAG,"蓝牙已经打开");
                                text.append("蓝牙已经打开.");
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                Log.e(TAG,"蓝牙正在关闭");
                                text.append("蓝牙正在关闭.");
                                break;
                            case BluetoothAdapter.STATE_OFF:
                                Log.e(TAG,"蓝牙已经关闭");
                                text.append("蓝牙已经关闭.");
                                break;
                        }
                        text.append("\n").append(mTextView.getText().toString());
                        break;
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        //Toast.makeText(context,"蓝牙设备已连接",Toast.LENGTH_SHORT).show();
                        break;

                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        //Toast.makeText(context,"蓝牙设备已断开",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }

}
