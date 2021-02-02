// IConnectionService.aidl
package com.example.ipc_demo;

// 连接服务
interface IConnectionService {

    void connect();
    // 使用oneway时返回值必须为void
    oneway void connect_oneway();
    void disconnect();
    boolean isConnected();
}