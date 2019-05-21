package com.example.multiprocessdemo;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Process;

public class MyService extends Service {

    private ActivityManager mActivityManager;

    private MultiProcess mService;
    private IBinder mClient = null;
    private RemoteCallbackList<NotifyCallBack> mCallBacks = new RemoteCallbackList<NotifyCallBack>();

    private final MultiProcess.Stub mBinder = new MultiProcess.Stub() {
        @Override
        public int getPid() throws RemoteException {
            return (int) Thread.currentThread().getId();
        }

        @Override
        public void setBinder(IBinder client) throws RemoteException {
            mClient = client;
            mClient.linkToDeath(new TestDeathRecipient(), 0);
        }

        @Override
        public void registerCallBack(NotifyCallBack cb) throws RemoteException {
            mCallBacks.register(cb);
        }

        @Override
        public void unregisterCallBack(NotifyCallBack cb) throws RemoteException {
            mCallBacks.unregister(cb);
        }

        @Override
        public void sendMessage(String msg) throws RemoteException {
            notifyCallBack(msg);
        }

        @Override
        public void removeMessage(String msg) throws RemoteException {
            notifyCallBack(msg);
        }
    };

    private void notifyCallBack(String name) {
        final int len = mCallBacks.beginBroadcast();
        for (int i = 0; i < len; i++) {
            try {
                mCallBacks.getBroadcastItem(i).onMessageReceived(name);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mCallBacks.finishBroadcast();
    }

    private class TestDeathRecipient implements IBinder.DeathRecipient {
        @Override
        public void binderDied() {
            Log.d(MainActivity.TAG, "binderDied");
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            boolean isRun = isRunningProcess(mActivityManager, getPackageName());
//            Log.d(MainActivity.TAG,"isRun == " + isRun);
//            if(!isRun){
//                Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
//                Log.d(MainActivity.TAG,"intent == " + intent);
//                if(intent != null ){
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                }
//            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    }

    public static boolean isRunningProcess(ActivityManager manager, String processName) {
        if (manager == null)
            return false;
        List<ActivityManager.RunningAppProcessInfo> runnings = manager.getRunningAppProcesses();
        if (runnings != null) {
            for (ActivityManager.RunningAppProcessInfo info : runnings) {
                if (TextUtils.equals(info.processName, processName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
