//MultiProcess.aidl
package com.example.multiprocessdemo;

// Declare any non-default types here with import statements
import com.example.multiprocessdemo.NotifyCallBack;
interface MultiProcess{

    int getPid();
    void setBinder(IBinder client);
    void registerCallBack(NotifyCallBack cb);
    void unregisterCallBack(NotifyCallBack cb);

    void sendMessage(String msg);
    void removeMessage(String msg);
}
