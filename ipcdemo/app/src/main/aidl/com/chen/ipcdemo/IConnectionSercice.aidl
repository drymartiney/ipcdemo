// IConnectionSercice.aidl
package com.chen.ipcdemo;

interface IConnectionSercice {
    oneway void connect();
    void disconnect();
    boolean isConnected();
}
