package com.anyhunter.app.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GPSUtils {
    private static final String TAG = "GPSUtils";

    private LocationManager mLocationManager = null;

    private LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged: [位置发生变化]");
            if (location != null) {
                GPSActivity.sGPSActivity.updateTV(location);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d(TAG, "onStatusChanged: [当前GPS状态为可见状态]");
                    break;

                case LocationProvider.OUT_OF_SERVICE:
                    Log.d(TAG, "onStatusChanged: [当前GPS状态为服务区外状态]");
                    break;

                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d(TAG, "onStatusChanged: [当前GPS状态为暂停服务状态]");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            if (mLocationManager != null) {
                @SuppressLint("MissingPermission") Location location = mLocationManager.getLastKnownLocation(provider);
                GPSActivity.sGPSActivity.updateTV(location);
            }
            Log.d(TAG, "onProviderEnabled: [GPS 开启]");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled: [GPS 关闭]");
        }
    };

    private GpsStatus.Listener mListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.d(TAG, "onGpsStatusChanged: [第一次定位]");
                    break;

                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.d(TAG, "onGpsStatusChanged: [卫星状态]");
                    //获取当前状态
                    @SuppressLint("MissingPermission") GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    int ttff = gpsStatus.getTimeToFirstFix();
                    Log.d(TAG, "onGpsStatusChanged: [最大卫星数]：" + maxSatellites + ", [首次定位时间]：" + ttff);

                    List<GpsSatellite> gpsSatellites = new ArrayList<>();

                    //创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        gpsSatellites.add(s);
                        count++;
                    }
                    Log.d(TAG, "onGpsStatusChanged: [当前卫星数]：" + count);

                    for (GpsSatellite gpsSatellite : gpsSatellites) {
                        //卫星的方位角，浮点型数据
                        Log.d(TAG, "onGpsStatusChanged: [卫星的方位角]: " + gpsSatellite.getAzimuth());
                        //卫星的高度，浮点型数据
                        Log.d(TAG, "onGpsStatusChanged: [卫星的高度]: " + gpsSatellite.getElevation());
                        //卫星的伪随机噪声码，整形数据
                        Log.d(TAG, "onGpsStatusChanged: [卫星的伪随机噪声码]: " + gpsSatellite.getPrn());
                        //卫星的信噪比，浮点型数据
                        Log.d(TAG, "onGpsStatusChanged: [卫星的信噪比]: " + gpsSatellite.getSnr());
                        //卫星是否有年历表，布尔型数据
                        Log.d(TAG, "onGpsStatusChanged: [卫星是否有年历表]: " + gpsSatellite.hasAlmanac());
                        //卫星是否有星历表，布尔型数据
                        Log.d(TAG, "onGpsStatusChanged: [卫星是否有星历表]: " + gpsSatellite.hasEphemeris());
                        //卫星是否被用于近期的GPS修正计算
                        Log.d(TAG, "onGpsStatusChanged: [卫星是否被用于近期的GPS修正计算]: " + gpsSatellite.hasAlmanac());
                    }
                    break;

                case GpsStatus.GPS_EVENT_STARTED:
                    Log.d(TAG, "onGpsStatusChanged: [定位启动]");
                    break;

                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.d(TAG, "onGpsStatusChanged: [定位失败]");
                    break;
            }
        }
    };

    @SuppressLint("MissingPermission")
    public void startLocate(Context context) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager != null) {

            // 判断GPS是否正常启动
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(context, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
                // 返回开启GPS导航设置界面
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivityForResult(intent, 0);
                return;
            }

            mLocationManager.addGpsStatusListener(mListener);


            // 绑定监听，有4个参数
            // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
            // 参数2，位置信息更新周期，单位毫秒
            // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
            // 参数4，监听
            // 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

            // 1秒更新一次，或最小位移变化超过1米更新一次；
            // 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
            List<String> locationProviders = mLocationManager.getProviders(true);

            if (locationProviders.contains(LocationManager.GPS_PROVIDER)) {
                Log.d(TAG, "startLocate: [有GPS定位]");
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
            }
            if (locationProviders.contains(LocationManager.NETWORK_PROVIDER)) {
                Log.d(TAG, "startLocate: [有网络定位]");
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 0, mLocationListener);
            }
        }
    }

    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }
}
