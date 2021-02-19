// IMessageService.aidl
package com.example.ipc_demo;
import com.example.ipc_demo.entity.Message;
import com.example.ipc_demo.MessageReceiveListener;
// 消息服务
interface IMessageService {
    void sendMessage(inout Message message);

    void registerMessageReceiveListener(MessageReceiveListener messageReceiveListener);

    void unRegisterMessageReceiveListener(MessageReceiveListener messageReceiveListener);
}