// MessageReceiverListener.aidl
package com.chen.ipcdemo;
import com.chen.ipcdemo.entity.Message;

interface MessageReceiveListener {
    void onReceiveMessage(in Message message);
}
