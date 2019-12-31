// IMessageService.aidl
package com.chen.ipcdemo;
import com.chen.ipcdemo.entity.Message;
import com.chen.ipcdemo.MessageReceiveListener;

//消息服务
interface IMessageService {
    void sendMessage(in Message message);

    void registerMessageReceiveListener(MessageReceiveListener messageReceiveListener);
    void unRegisterMessageReceiveListener(MessageReceiveListener messageReceiveListener);
}
