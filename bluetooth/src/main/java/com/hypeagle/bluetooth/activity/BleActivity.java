package com.hypeagle.bluetooth.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.hypeagle.bluetooth.R;
import com.hypeagle.bluetooth.sevice.BleService;

import java.lang.ref.WeakReference;

public class BleActivity extends AppCompatActivity {
    private static final String TAG = "BleActivity";

    private static final int ENABLE_BT = 1;

    private BleService.State mState = BleService.State.UNKNOWN;

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

        TextView tv = (TextView) findViewById(R.id.ble_text);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
                BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
                if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                    enableBluetooth();
                } else {
                    startScan();
                }
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                startScan();
            } else {
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void enableBluetooth() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, ENABLE_BT);
    }

    private void startScan() {
        Message message = Message.obtain(null, BleService.MSG_START_SCAN);
        if (message != null) {
            try {
                mService.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
                unbindService(mServiceConnection);
            }
        }
    }

    private void stateChanged(BleService.State state) {
        mState = state;
        switch (mState) {
            case IDLE:
                break;

            case BLUETOOTH_OFF:
                enableBluetooth();
                break;
        }
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
                switch (msg.what) {
                    case BleService.MSG_STATE_CHANGED:
                        activity.stateChanged(BleService.State.values()[msg.arg1]);
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }
}
