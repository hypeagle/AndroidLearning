package com.hypeagle.bluetooth.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.hypeagle.bluetooth.R;
import com.hypeagle.bluetooth.sevice.BleService;

import java.lang.ref.WeakReference;

public class BleActivity extends AppCompatActivity {
    private static final String TAG = "BleActivity";

    private final Messenger mMessenger;

    private Intent mServiceIntent;
    private Messenger mService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            try {
                Message message = Message.obtain(null, BleService.MSG_REGISTER);
                if (message != null) {
                    message.replyTo = mMessenger;
                    mService.send(message);
                } else {
                    mService = null;
                }

            } catch (RemoteException e) {
                e.printStackTrace();
                mService = null;
            } finally {
                unbindService(mServiceConnection);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    public BleActivity() {
        super();

        mMessenger = new Messenger(new IncomingHandler(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);

        mServiceIntent = new Intent(this, BleService.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        bindService(mServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        if (mService != null) {
            try {
                Message message = Message.obtain(null, BleService.MSG_UNREGISTER);
                if (message != null) {
                    message.replyTo = mMessenger;
                    mService.send(message);
                } else {
                    mService = null;
                }

            } catch (RemoteException e) {
                e.printStackTrace();
                mService = null;
            }
        }

        super.onStop();
    }

    private static class IncomingHandler extends Handler {
        private final WeakReference<BleActivity> mActivity;

        IncomingHandler(BleActivity bleActivity) {
            mActivity = new WeakReference<BleActivity>(bleActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            BleActivity activity = mActivity.get();
            if (activity != null) {
//                TODO: do something
            }
            super.handleMessage(msg);
        }
    }
}
