// IServiceManager.aidl
package com.example.ipc_demo;

interface IServiceManager {
    IBinder getService(String serviceName);
}