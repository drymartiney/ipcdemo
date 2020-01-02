// IMessageService.aidl
package com.chen.ipcdemo;
import com.chen.ipcdemo.entity.Message;
import com.chen.ipcdemo.MessageReceiveListener;

//消息服务
interface IMessageService {
    //inout关键字会将数据写入数据流，并正常读取出来
    void sendMessage(inout Message message);

    void registerMessageReceiveListener(MessageReceiveListener messageReceiveListener);
    void unRegisterMessageReceiveListener(MessageReceiveListener messageReceiveListener);
}
