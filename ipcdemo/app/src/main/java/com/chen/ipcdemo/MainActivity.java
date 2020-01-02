package com.chen.ipcdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chen.ipcdemo.entity.Message;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonConnect;
    private Button buttonDisConnect;
    private Button buutonIsConnected;

    private Button buttonSendMessage;
    private Button buttonRegisterListener;
    private Button buttonUnregisterListener;

    private Button buttonSendByMessenger;

    private IConnectionSercice connectionSerciceProxy;
    private IMessageService messageServiceProxy;
    private IServiceManager serviceManagerProxy;

    private Messenger messengerProxy;

    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            Bundle bundle=msg.getData();
            //防止反序列化出现异常
            bundle.setClassLoader(Message.class.getClassLoader());
            final Message message=bundle.getParcelable("message");

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,message.getContent(), Toast.LENGTH_SHORT).show();
                }
            },3000);
        }
    };
    private Messenger clientMessenger=new Messenger(handler);
    private MessageReceiveListener messageReceiveListener=new MessageReceiveListener.Stub() {
        @Override
        public void onReceiveMessage(final Message message) throws RemoteException {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,"message:"+message.getContent(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonConnect= (Button) findViewById(R.id.btn_connect);
        buttonDisConnect= (Button) findViewById(R.id.btn_disconnect);
        buutonIsConnected= (Button) findViewById(R.id.btn_is_connected);

        buttonSendMessage= (Button) findViewById(R.id.btn_send_message);
        buttonRegisterListener= (Button) findViewById(R.id.btn_register_listener);
        buttonUnregisterListener= (Button) findViewById(R.id.btn_unregister_listener);

        buttonSendByMessenger= (Button) findViewById(R.id.btn_messenger);

        buttonConnect.setOnClickListener(this);
        buttonDisConnect.setOnClickListener(this);
        buutonIsConnected.setOnClickListener(this);

        buttonSendMessage.setOnClickListener(this);
        buttonRegisterListener.setOnClickListener(this);
        buttonUnregisterListener.setOnClickListener(this);

        buttonSendByMessenger.setOnClickListener(this);

        Intent intent=new Intent(this,RemoteService.class);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder iBinder) {
                try {
                    serviceManagerProxy=IServiceManager.Stub.asInterface(iBinder);
                    connectionSerciceProxy=IConnectionSercice.Stub.asInterface(serviceManagerProxy.getService(IConnectionSercice.class.getSimpleName()));
                    messageServiceProxy=IMessageService.Stub.asInterface(serviceManagerProxy.getService(IMessageService.class.getSimpleName()));
                    messengerProxy=new Messenger(serviceManagerProxy.getService(Messenger.class.getSimpleName()));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_connect:
                try {
                    connectionSerciceProxy.connect();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_disconnect:
                try {
                    connectionSerciceProxy.disconnect();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_is_connected:
                boolean isConnected= false;
                try {
                    isConnected = connectionSerciceProxy.isConnected();
                    Toast.makeText(this,String.valueOf(isConnected),Toast.LENGTH_SHORT).show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_send_message:
                try {
                    Message message=new Message();
                    message.setContent("message send from main");
                    messageServiceProxy.sendMessage(message);
                    Log.d(MainActivity.class.getSimpleName(),String.valueOf(message.isSendSuccess()));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_register_listener:
                try {
                    messageServiceProxy.registerMessageReceiveListener(messageReceiveListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_unregister_listener:
                try {
                    messageServiceProxy.unRegisterMessageReceiveListener(messageReceiveListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_messenger:
                try {
                    Message message=new Message();
                    message.setContent("send messenger from main by Messenger");
                    android.os.Message data=new android.os.Message();
                    //将服务端发送的数据，复制给客户端
                    data.replyTo=clientMessenger;
                    Bundle bundle=new Bundle();
                    bundle.putParcelable("message",message);
                    data.setData(bundle);
                    messengerProxy.send(data);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
