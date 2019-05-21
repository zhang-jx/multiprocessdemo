package com.example.multiprocessdemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.TextView;


public class MainActivity extends Activity implements View.OnClickListener {

    final static String TAG = "multiProcess";

    private static final String msg = "test";

    private Button mSendBtn;
    private Button mRemoveBtn;
    private Button mBindBtn;
    private Button mUnbindBtn;
    private TextView mTextView;

    private IBinder mBinder = new Binder();

    private MultiProcess mService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected...");
            mService = MultiProcess.Stub.asInterface(service);
            try {
                mService.setBinder(mBinder);
                mService.registerCallBack(mNotifyCallBack);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected...");
            try {
                mService.unregisterCallBack(mNotifyCallBack);
                mService = null;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private NotifyCallBack mNotifyCallBack = new NotifyCallBack.Stub() {

        @Override
        public void onMessageReceived(String msg) throws RemoteException {
            Log.d(TAG,"onMessageReceived... msg == " + msg);
            mTextView.setText(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSendBtn = (Button) findViewById(R.id.send_msg);
        mRemoveBtn = (Button) findViewById(R.id.remove_msg);
        mBindBtn = (Button) findViewById(R.id.bind);
        mUnbindBtn = (Button) findViewById(R.id.unbind);
        mTextView = (TextView)findViewById(R.id.textView);

        mSendBtn.setOnClickListener(this);
        mRemoveBtn.setOnClickListener(this);
        mBindBtn.setOnClickListener(this);
        mUnbindBtn.setOnClickListener(this);

        mSendBtn.setVisibility(View.GONE);
        mRemoveBtn.setVisibility(View.GONE);
        mBindBtn.setVisibility(View.GONE);
        mUnbindBtn.setVisibility(View.GONE);

        bindService(new Intent(this, MyService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_msg:
                sendMessage();
                break;
            case R.id.remove_msg:
                removeMessage();
                break;
            case R.id.bind:
                bindService(new Intent(this, MyService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
                break;
            case R.id.unbind:
                unbindService(mServiceConnection);
                break;
            default:
                break;
        }
    }

    private void sendMessage() {
        try {
            mService.sendMessage(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void removeMessage(){
        try {
            mService.removeMessage("remove " + msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
