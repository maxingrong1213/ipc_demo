package com.example.ipc_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ipc_demo.entity.Message;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button button_connect;
    private Button button_connect_oneway;
    private Button button_disconnect;
    private Button button_isconnected;

    private Button button_sendmessage;
    private Button button_registerlistener;
    private Button button_unregisterlistener;
    private Button button_sendByMessenger;

    private IConnectionService connectionServiceProxy;
    private IMessageService messageServiceProxy;
    private IServiceManager serviceManagerProxy;
    private Messenger messengerProxy;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            bundle.setClassLoader(Message.class.getClassLoader());
            final Message message = bundle.getParcelable("message");

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, message.getContent(), Toast.LENGTH_SHORT).show();
                }
            }, 3000);
        }
    };

    private Messenger clientMessenger = new Messenger(handler);

    private MessageReceiveListener messageReceiveListener = new MessageReceiveListener.Stub() {
        @Override
        public void onReceiveMessage(Message message) throws RemoteException {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(MainActivity.this,message.getContent(),Toast.LENGTH_LONG).show();
                    Log.d("MXRMXR",message.getContent());
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_connect = findViewById(R.id.btn_connect);
        button_connect_oneway = findViewById(R.id.btn_connect_oneway);
        button_disconnect = findViewById(R.id.btn_disconnect);
        button_isconnected = findViewById(R.id.btn_is_connected);

        button_sendmessage = findViewById(R.id.btn_send_message);
        button_registerlistener = findViewById(R.id.btn_registerlistener);
        button_unregisterlistener = findViewById(R.id.btn_unregisterlistener);
        button_sendByMessenger = findViewById(R.id.btn_messenger);

        button_connect.setOnClickListener(this);
        button_connect_oneway.setOnClickListener(this);
        button_disconnect.setOnClickListener(this);
        button_isconnected.setOnClickListener(this);

        button_sendmessage.setOnClickListener(this);
        button_registerlistener.setOnClickListener(this);
        button_unregisterlistener.setOnClickListener(this);
        button_sendByMessenger.setOnClickListener(this);

        Intent intent = new Intent(this,RemoteService.class);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    serviceManagerProxy = IServiceManager.Stub.asInterface(service);
                    connectionServiceProxy = IConnectionService.Stub.asInterface(serviceManagerProxy.getService(IConnectionService.class.getSimpleName()));
                    messageServiceProxy = IMessageService.Stub.asInterface(serviceManagerProxy.getService(IMessageService.class.getSimpleName()));
                    messengerProxy = new Messenger(serviceManagerProxy.getService(Messenger.class.getSimpleName()));
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
                    Log.d("MXRMXR","isconnect is :"+ isconnect);
                    //Toast.makeText(this,String.valueOf(isconnect),Toast.LENGTH_LONG).show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_send_message:
                try {
                    Message message = new Message();
                    message.setContent("message send form main");
                    messageServiceProxy.sendMessage(message);
                    Log.d(MainActivity.class.getSimpleName(), String.valueOf(message.isSendSuccess()));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_registerlistener:
                try {
                    messageServiceProxy.registerMessageReceiveListener(messageReceiveListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_unregisterlistener:
                try {
                    messageServiceProxy.unRegisterMessageReceiveListener(messageReceiveListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_messenger:
                try {
                    Message message = new Message();
                    message.setContent("send message from main by Messenger");

                    android.os.Message data = new android.os.Message();
                    data.replyTo = clientMessenger;
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("message", message);
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