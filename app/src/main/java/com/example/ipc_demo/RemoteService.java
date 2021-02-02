package com.example.ipc_demo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 管理和提供子进程的连接和消息服务
 *@desc: RemoteService
 *@Author: XiaoMaPedro
 *@Time: 2021/2/2 10:32
 */
public class RemoteService extends Service {
    public RemoteService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}