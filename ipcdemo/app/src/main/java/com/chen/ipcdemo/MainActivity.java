package com.chen.ipcdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonConnect;
    private Button buttonDisConnect;
    private Button buutonIsConnected;

    private IConnectionSercice connectionSerciceProxy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonConnect= (Button) findViewById(R.id.btn_connect);
        buttonDisConnect= (Button) findViewById(R.id.btn_disconnect);
        buutonIsConnected= (Button) findViewById(R.id.btn_is_connected);

        buttonConnect.setOnClickListener(this);
        buttonDisConnect.setOnClickListener(this);
        buutonIsConnected.setOnClickListener(this);

        Intent intent=new Intent(this,RemoteService.class);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder iBinder) {
                connectionSerciceProxy=IConnectionSercice.Stub.asInterface(iBinder);
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
            default:
                break;
        }
    }
}
