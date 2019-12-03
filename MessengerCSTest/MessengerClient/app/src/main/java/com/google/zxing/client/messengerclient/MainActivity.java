package com.google.zxing.client.messengerclient;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static Button mButtontouch = null;
    private static Button mButton = null;
    private static final int MSG_FROM_CLIENT = 0x10001;
    private static final int MSG_TO_CLIENT = 0x10002;
    private static Integer index = new Integer(1);
    private static final String NICK_NAME = "nickName";
    boolean isConn=false;
    private Thread vthread ;

    class vTHREAD implements Runnable {
        private Handler handler = null;
        private int i = 100;
        public vTHREAD(Looper looper) {
            handler = new Handler(looper);
        }
        @Override
        public void run() {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    while(i !=0 ) {
                        i --;
                        mButton.callOnClick();
                    }
                }
            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtontouch = (Button)findViewById(R.id.button2);
        mButtontouch.setOnClickListener(new MyClickListener2());
        mButton = (Button)findViewById(R.id.sendMsg);
        mButton.setOnClickListener(new MyClickListener());
        Intent intent = new Intent();
        intent.setClassName("com.google.zxing.client.messegerservice","com.google.zxing.client.messegerservice.MessengerService");
        bindService(intent, mConnection, BIND_AUTO_CREATE);
        vthread = new Thread(new vTHREAD(getMainLooper()));
    }

    public class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d("XjTest","MainActivity::getClick() xj 2");
            Message msgFromClient = new Message();
            //构造传给客户端的bundle
            Bundle toServiceDate = new Bundle();
            toServiceDate.putString(NICK_NAME,"这里是客户端" + index.toString());
            index++;
            msgFromClient.what = MSG_FROM_CLIENT;
            msgFromClient.setData(toServiceDate);
            //将自己定义的messenger设置在要发送出去的msg里面，在服务器那边才能通过这个messenger将消息发送回来客户端
//            msgFromClient.replyTo = mClient;
            msgFromClient.replyTo = new Messenger(new MessengerS());
            if (isConn)
            {
                //往服务端发送消息
                try {
                    mService.send(msgFromClient);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class MyClickListener2 implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            vthread.start();
        }
    }

    //注册一个messenger，监听系统消息
    //mClient ，在前面说过了，messenger的通讯都是这样的，要想发送消息，必须在接收端定义一个messenger，用来接收数据，然后将这个
    //messenger的实例传回给发送端，让发送端调用这个实例的messenger.send方法来发送消息
    private Messenger mClient=new Messenger(new Handler(){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msgFromServer)
        {
            switch (msgFromServer.what)
            {
                case MSG_TO_CLIENT:
                    Bundle data = msgFromServer.getData();
                    Log.d("XjTest","MainActivity::mClient::handleMessage() 服务器返回内容" + data.get(NICK_NAME));
                    break;
            }
            super.handleMessage(msgFromServer);
        }
    });

    private class MessengerS extends Handler {
        @Override
        public void handleMessage(Message msgFromServer)
        {
            Log.d("XjTest","MainActivity::MessengerS::handleMessage()");
            switch (msgFromServer.what)
            {
                case MSG_TO_CLIENT:
                    Bundle data = msgFromServer.getData();
                    Log.d("XjTest","MainActivity::mClient::handleMessage() 服务器返回内容" + data.get(NICK_NAME));
                    break;
            }
            super.handleMessage(msgFromServer);
        }
    }


    private Messenger mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //mService,也就是服务端定义的那个messenger，只有拿到这个服务端的messenger，才能发送消息给服务端
            mService=new Messenger(service);
            System.out.println("链接成功");
            Log.d("XjTest","MainActivity::ServiceConnection::onServiceConnected()");
            isConn=true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mClient = null;
            isConn=false;
            Log.d("XjTest","MainActivity::ServiceConnection::onServiceDisconnected()");
            System.out.println("链接失败");
        }
    };


}
