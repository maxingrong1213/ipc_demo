package com.example.ipc_demo;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

/**
 * 管理和提供子进程的连接和消息服务
 *@desc: RemoteService
 *@Author: XiaoMaPedro
 *@Time: 2021/2/2 10:32
 */
public class RemoteService extends Service {

    private boolean isConnected = false;
    //用于切回主线程，去显示Toast
    private Handler handler = new Handler(Looper.getMainLooper());

    // 其中connect()、disconnect()、isConnected()都是在子进程的线程池中的，Toast要在主线程中才会显示出来。
    private IConnectionService connectionService = new IConnectionService.Stub() {
        @Override
        public void connect() throws RemoteException {
            try {
                Thread.sleep(5000);
                isConnected = true;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("MXRMXR","connect");
                        //Toast.makeText(RemoteService.this,"connect",Toast.LENGTH_LONG).show();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void connect_oneway() throws RemoteException {
            try {
                Thread.sleep(5000);
                isConnected = true;
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
            isConnected = false;
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
            return isConnected;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return connectionService.asBinder();
    }
}