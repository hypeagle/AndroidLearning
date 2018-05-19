package com.hypeagle.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.hypeagle.runtimepermission.RPUtils;

public class GPSActivity extends AppCompatActivity {
    static GPSActivity sGPSActivity;

    private TextView mTVLocation;

    private GPSUtils mGPSUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        mGPSUtils = new GPSUtils();
        if (!RPUtils.checkAndRequestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, 0)) {
            mGPSUtils.startLocate(this);
        }

        mTVLocation = findViewById(R.id.textView);

        sGPSActivity = this;

        @SuppressLint("HandlerLeak") final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    if (!RPUtils.checkAndRequestPermission(sGPSActivity, Manifest.permission.ACCESS_FINE_LOCATION, 0)) {
                        mGPSUtils.startLocate(sGPSActivity);
                    }
                } else {
                    super.handleMessage(msg);
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        mHandler.obtainMessage(1).sendToTarget();

                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mGPSUtils.startLocate(this);
            } else {
                Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void updateTV(Location location) {
        mTVLocation.setText("[经度]：" + location.getLongitude() + "\n[纬度]：" + location.getLatitude());
    }
}
