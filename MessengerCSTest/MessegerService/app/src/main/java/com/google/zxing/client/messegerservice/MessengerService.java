package com.google.zxing.client.messegerservice;

import android.app.Service;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.util.Log;

import androidx.annotation.Nullable;

public class MessengerService extends Service {

    private static Integer index = new Integer(1);
    //客户端消息标志
    private static final int MSG_FROM_CLIENT = 0x10001;
    //服务端消息标志
    private static final int MSG_TO_CLIENT = 0x10002;
    //传递消息的参数标志
    private static final String NICK_NAME = "nickName";

    @Override
    public void onCreate() {
        Log.d("XjTest","MessengerService::onCreate()");
    }

    //用来接收客户端message的messaHandler,用来给后面的messenger传入的，解析从客户端获取的message
    private static class MessagerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            //获得message实例
            Message msgToClient = Message.obtain(msg);
            switch (msg.what){
                case MSG_FROM_CLIENT:
                    Log.d("XjTest","MessengerService::MessagerHandler::handleMessage() 获得来自客户端的信息 " + msg.getData().getString(NICK_NAME));
                    //构造传回客户端的数据bundle
                    Bundle toClicentDate = new Bundle();
                    toClicentDate.putString(NICK_NAME,"这是服务端发出的消息"+ index.toString());
                    index++;
                    msgToClient.setData(toClicentDate);
                    msgToClient.what = MSG_TO_CLIENT;

                    //传回Client
                    try {
                        //msg.replyTo在客户端有定义，其实这就是第二个messenger，在messenger中，
                        // 发送消息和接收消息都必须要有一个messenger，而在这个从客户端获取的msg中，其实在客户端已经通过msg.reply=xxx，设置了接收消息的messenger了
                        msg.replyTo.send(msgToClient);
                    } catch (RemoteException e) {

                    }

                    break;
                default:
                    System.out.println("接收到别的信息");
                    Log.d("XjTest","MessengerService::MessagerHandler::handleMessage() 接收到别的信息 ");
                    super.handleMessage(msg);
            }
        }
    }

    private final Messenger mMessenger =new Messenger((new MessagerHandler()));

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

}
