// MessageReceiveListener.aidl
package com.example.ipc_demo;
import com.example.ipc_demo.entity.Message;

interface MessageReceiveListener {
    void onReceiveMessage(in Message message);
}