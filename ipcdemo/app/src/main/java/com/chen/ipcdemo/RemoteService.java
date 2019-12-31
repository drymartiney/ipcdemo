package com.chen.ipcdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.chen.ipcdemo.entity.Message;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by yy on 2019/12/30.
 */

public class RemoteService extends Service {
    private boolean isConnected=false;

    private Handler handler=new Handler(Looper.getMainLooper());

    private ArrayList<MessageReceiveListener> messageReceiveListenerArrayList=new ArrayList<>();

    private ScheduledFuture scheduledFuture;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private IConnectionSercice connectionSercice=new IConnectionSercice.Stub() {
        @Override
        public void connect() throws RemoteException {
            try {
                Thread.sleep(5000);
                isConnected=true;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RemoteService.this,"connect",Toast.LENGTH_SHORT).show();
                    }
                });
                scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        for (MessageReceiveListener messageReceiveListener:messageReceiveListenerArrayList){
                            Message message=new Message();
                            message.setContent("this message from remote");
                            try {
                                messageReceiveListener.onReceiveMessage(message);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },5000,5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void disconnect() throws RemoteException {
            isConnected=false;
            scheduledFuture.cancel(true);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RemoteService.this,"disconnect",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public boolean isConnected() throws RemoteException {
            return isConnected;
        }
    };

    private IMessageService messageService= new IMessageService.Stub() {
        @Override
        public void sendMessage(final Message message) throws RemoteException {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RemoteService.this,message.getContent(),Toast.LENGTH_SHORT).show();
                }
            });
            if (isConnected){
                message.setSendSuccess(true);
            }else {
                message.setSendSuccess(false);
            }
        }

        @Override
        public void registerMessageReceiveListener(MessageReceiveListener messageReceiveListener) throws RemoteException {
            if (messageReceiveListener!=null){
                messageReceiveListenerArrayList.add(messageReceiveListener);
            }
        }

        @Override
        public void unRegisterMessageReceiveListener(MessageReceiveListener messageReceiveListener) throws RemoteException {
            if (messageReceiveListener!=null){
                messageReceiveListenerArrayList.remove(messageReceiveListener);
            }
        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return connectionSercice.asBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        scheduledThreadPoolExecutor=new ScheduledThreadPoolExecutor(1);
    }
}
