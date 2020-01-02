// IServiceManager.aidl
package com.chen.ipcdemo;

interface IServiceManager {
    IBinder getService(String serviceName);
}
