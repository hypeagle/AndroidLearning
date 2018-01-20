package com.hypeagle.bluetooth.sevice;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public class BleService extends Service {
    private static final String TAG = "BleService";

    public final static int MSG_REGISTER = 1;
    public final static int MSG_UNREGISTER = 2;

    private final Messenger mMessenger;
    private final List<Messenger> mClients = new LinkedList<>();

    public BleService() {
        mMessenger = new Messenger(new IncomingHandler(this));
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mMessenger.getBinder();
    }


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

                    default:
                        super.handleMessage(msg);
                        break;
                }
            }
        }
    }
}
