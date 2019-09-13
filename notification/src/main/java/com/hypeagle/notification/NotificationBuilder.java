package com.hypeagle.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.List;

public class NotificationBuilder implements NotificationChannelBuilder.CreateChannelCallBack {
    public static final String IMPORTANT_CHANNEL_ID = "IMPORTANT_CHANNEL_ID";
    public static final String NORMAL_CHANNEL_ID = "NORMAL_CHANNEL_ID";
    public static final String LOW_CHANNEL_ID = "LOW_CHANNEL_ID";

    private List<String> mChannelIds;

    private Context mContext;
    private NotificationManagerCompat mNotificationManager;
    private NotificationChannelBuilder mNotificationChannelBuilder;

    public NotificationBuilder(Context context) {
        mContext = context;
        mNotificationManager = NotificationManagerCompat.from(context);

        mChannelIds = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannelIds.add(IMPORTANT_CHANNEL_ID);
            mChannelIds.add(NORMAL_CHANNEL_ID);
            mChannelIds.add(LOW_CHANNEL_ID);
        } else {
            mChannelIds.add(NORMAL_CHANNEL_ID);
        }
        mNotificationChannelBuilder = new NotificationChannelBuilder(context, mChannelIds);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public NotificationChannel createChannel(String channelId) {
        if (IMPORTANT_CHANNEL_ID.equals(channelId)) {
            NotificationChannel channel = new NotificationChannel(channelId, "重要", NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(true);
            channel.setDescription("这是很重要的通知");
            return channel;
        } else if (NORMAL_CHANNEL_ID.equals(channelId)) {
            NotificationChannel channel = new NotificationChannel(channelId, "一般", NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(true);
            channel.setDescription("这是很一般的通知");
            return channel;
        } else if (LOW_CHANNEL_ID.equals(channelId)) {
            NotificationChannel channel = new NotificationChannel(channelId, "低级", NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(true);
            channel.setDescription("这是很低级的通知");
            return channel;
        }

        return null;
    }

    public void sendNotification(String channelId, String title, String desc) {
        mNotificationChannelBuilder.ensureChannelsExist(this);
        Notification notification = buildNotification(channelId, title, desc);
        mNotificationManager.notify(1, notification);
    }

    private Notification buildNotification(String channelId, String title, String desc) {
        return new NotificationCompat.Builder(mContext, channelId)
                .setContentTitle(title)
                .setContentText(desc)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setSmallIcon(R.mipmap.ic_notification)
                .setNumber(2)
                .build();
    }
}
