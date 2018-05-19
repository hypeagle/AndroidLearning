package com.hypeagle.notification;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class NotificationChannelBuilder {
    private List<String> mChannelIds;
    private NotificationManager mNotificationManager;

    public NotificationChannelBuilder(Context context, List<String> channelIds) {
        mChannelIds = channelIds;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void ensureChannelsExist(@NonNull CreateChannelCallBack createChannelCallBack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ensureChannelsExistExt(createChannelCallBack);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void ensureChannelsExistExt(@NonNull CreateChannelCallBack createChannelCallBack) {
        List<NotificationChannel> existChannels = mNotificationManager.getNotificationChannels();
        List<String> channels = new ArrayList<>();
        for (NotificationChannel notificationChannel : existChannels) {
            channels.add(notificationChannel.getId());
        }

        for (String channelId : mChannelIds) {
            if (!channels.contains(channelId)) {
                NotificationChannel channel = createChannelCallBack.createChannel(channelId);
                mNotificationManager.createNotificationChannel(channel);
            }
        }
    }

    public interface CreateChannelCallBack {
        public NotificationChannel createChannel(String channelId);
    }
}
