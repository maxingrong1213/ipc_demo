package com.example.ipc_demo;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.example.ipc_demo.entity.Message;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 管理和提供子进程的连接和消息服务
 *@desc: RemoteService
 *@Author: XiaoMaPedro
 *@Time: 2021/2/2 10:32
 */
public class RemoteService extends Service {

    private boolean isconnected = false;
    //用于切回主线程，去显示Toast
    private Handler handler = new Handler(Looper.getMainLooper());

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private ScheduledFuture scheduledFuture;
    // 其中connect()、disconnect()、isConnected()都是在子进程的线程池中的，Toast要在主线程中才会显示出来。
    private IConnectionService connectionService = new IConnectionService.Stub() {
        @Override
        public void connect() throws RemoteException {
            try {
                Thread.sleep(5000);
                isconnected = true;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("MXRMXR","connect");
                        //Toast.makeText(RemoteService.this,"connect",Toast.LENGTH_LONG).show();
                    }
                });
                scheduledFuture = scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        for (MessageReceiveListener messageReceiveListener : messageReceiveListenerArrayList) {
                            Message message = new Message();
                            message.setContent("this message form remote");
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
        public void connect_oneway() throws RemoteException {
            try {
                Thread.sleep(5000);
                isconnected = true;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("MXRMXR","connect_oneway");
                        //Toast.makeText(RemoteService.this,"connect_oneway",Toast.LENGTH_LONG).show();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void disconnect() throws RemoteException {
            isconnected = false;
            scheduledFuture.cancel(true);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("MXRMXR","disconnect");
                    //Toast.makeText(RemoteService.this,"disconnect",Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public boolean isConnected() throws RemoteException {
            if (isconnected) {
                Log.d("MXRMXR","has connected");
            } else {
                Log.d("MXRMXR","has disconnected");
            }
            return isconnected;
        }
    };


    private ArrayList<MessageReceiveListener> messageReceiveListenerArrayList = new ArrayList<>();

    private IMessageService messageService = new IMessageService.Stub() {
        @Override
        public void sendMessage(Message message) throws RemoteException {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("MXRMXR","connect");
                    //Toast.makeText(RemoteService.this,"  ",Toast.LENGTH_LONG).show();
                }
            });
            if (isconnected) {
                message.setSendSuccess(true);
            } else {
                message.setSendSuccess(false);
            }
        }

        @Override
        public void registerMessageReceiveListener(MessageReceiveListener messageReceiveListener) throws RemoteException {
            if(messageReceiveListener != null) {
                messageReceiveListenerArrayList.add(messageReceiveListener);
            }
        }

        @Override
        public void unRegisterMessageReceiveListener(MessageReceiveListener messageReceiveListener) throws RemoteException {
            if(messageReceiveListener != null) {
                messageReceiveListenerArrayList.remove(messageReceiveListener);
            }
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    };

    private IServiceManager serviceManager = new IServiceManager.Stub(){
        @Override
        public IBinder getService(String serviceName) throws RemoteException {
            if (IConnectionService.class.getSimpleName().equals(serviceName)){
                return connectionService.asBinder();
            }else if (IMessageService.class.getSimpleName().equals(serviceName)){
                return messageService.asBinder();
            }else {
                return null;
            }
        }

    };


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return serviceManager.asBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    }
}