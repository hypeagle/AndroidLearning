package com.hypeagle.bluetooth.sevice;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BleService extends Service implements BluetoothAdapter.LeScanCallback {
    private static final String TAG = "BleService";

    public final static int MSG_REGISTER = 1;
    public final static int MSG_UNREGISTER = 2;
    public final static int MSG_START_SCAN = 3;
    public final static int MSG_STATE_CHANGED = 4;

    private static final long SCAN_PERIOD = 3000;

    private final Messenger mMessenger;
    private final List<Messenger> mClients = new LinkedList<>();
    private final Map<String, BluetoothDevice> mDevices = new HashMap<>();

    private Message getMessageState() {
        Message message = Message.obtain(null, MSG_STATE_CHANGED);
        if (message != null) {
            message.arg1 = mState.ordinal();
        }
        return message;
    }

    private void setState(State state) {
        if (mState != state) {
            mState = state;
            Message message = getMessageState();
            if (message != null) {
                sendMessage(message);
            }
        }
    }

    private void sendMessage(Message message) {
        for (int i = mClients.size() - 1; i >= 0; i--) {
            Messenger messenger = mClients.get(i);
            if (!sendMessage(messenger, message)) {
                mClients.remove(messenger);
            }
        }
    }

    private boolean sendMessage(Messenger messenger, Message message) {
        boolean success = true;
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            Log.w(TAG, "Lost connection to client", e);
            success = false;
        }
        return success;
    }


    /**
     * The onLeScan() method is called each time the Bluetooth adapter receives any advertising message from a BLE device whilst it is scanning.
     * Devices will typically send out an advertising message 10 times a second while they are in advertising mode.
     */
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (device != null && device.getName() != null) {
            Log.d(TAG, "[---HYP---] " + device.getName() + ": " + device.getAddress());
        }
    }

    public enum State {
        UNKNOWN,
        IDLE,
        SCANNING,
        BLUETOOTH_OFF,
        CONNECTING,
        CONNECTED,
        DISCONNECTING
    }

    private BluetoothAdapter mBluetoothAdapter;
    private State mState = State.UNKNOWN;

    public BleService() {
        mMessenger = new Messenger(new IncomingHandler(this));
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mMessenger.getBinder();
    }

    private void startScan() {
        mDevices.clear();
        setState(State.SCANNING);
        if (mBluetoothAdapter == null) {
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            setState(State.BLUETOOTH_OFF);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mState == State.SCANNING) {
                        mBluetoothAdapter.stopLeScan(BleService.this);
                        setState(State.IDLE);
                    }
                }
            }, SCAN_PERIOD);
            mBluetoothAdapter.startLeScan(BleService.this);
        }
    }

    /**
     * Warning: non-static inner class maybe leaking memory. [Context leak]
     * A non-static inner class can hold a reference to the instance of its containing class as it has direct access to its fields and methods.
     * The Java garbage collector will not destroy objects which are referenced by other objects.
     * The way that we avoid context leaks is to always declare inner classes as static if they are declared within classes which subclass Context.
     * This then means that we have removed the main advantage of using an inner class: the ability to access is parents fields and / or methods.
     * We can easily overcome this by using a WeakReference which allows us to hold a reference to an object without preventing it from being garbage collected.
     */
    private static class IncomingHandler extends Handler {
        private final WeakReference<BleService> mService;

        IncomingHandler(BleService bleService) {
            mService = new WeakReference<BleService>(bleService);
        }

        @Override
        public void handleMessage(Message msg) {
            BleService service = mService.get();
            if (service != null) {
                switch (msg.what) {
                    case MSG_REGISTER:
                        service.mClients.add(msg.replyTo);
                        Log.d(TAG, "[---HYP---] Registered.");
                        break;

                    case MSG_UNREGISTER:
                        service.mClients.remove(msg.replyTo);
                        Log.d(TAG, "[---HYP---] Unegistered.");
                        break;

                    case MSG_START_SCAN:
                        service.startScan();
                        Log.d(TAG, "[---HYP---] Start scan.");
                        break;

                    default:
                        super.handleMessage(msg);
                        break;
                }
            }
        }
    }
}
