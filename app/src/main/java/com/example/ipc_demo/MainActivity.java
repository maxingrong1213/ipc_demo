package com.example.ipc_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button button_connect;
    private Button button_connect_oneway;
    private Button button_disconnect;
    private Button button_isconnected;

    private IConnectionService connectionServiceProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_connect = findViewById(R.id.btn_connect);
        button_connect_oneway = findViewById(R.id.btn_connect_oneway);
        button_disconnect = findViewById(R.id.btn_disconnect);
        button_isconnected = findViewById(R.id.btn_is_connected);

        button_connect.setOnClickListener(this);
        button_connect_oneway.setOnClickListener(this);
        button_disconnect.setOnClickListener(this);
        button_disconnect.setOnClickListener(this);

        Intent intent = new Intent(this,RemoteService.class);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                connectionServiceProxy = IConnectionService.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_connect:
                try {
                    connectionServiceProxy.connect();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_connect_oneway:
                try {
                    connectionServiceProxy.connect_oneway();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_disconnect:
                try {
                    connectionServiceProxy.disconnect();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_is_connected:
                try {
                    boolean isconnect = connectionServiceProxy.isConnected();
                    Log.d("MXRMXR","isconnect is"+ isconnect);
                    //Toast.makeText(this,String.valueOf(isconnect),Toast.LENGTH_LONG).show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}