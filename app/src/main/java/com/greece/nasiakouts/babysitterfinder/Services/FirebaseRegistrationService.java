package com.greece.nasiakouts.babysitterfinder.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.google.firebase.iid.FirebaseInstanceId;
import com.greece.nasiakouts.babysitterfinder.R;

public class FirebaseRegistrationService {
    public static void RegisterToPushService(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        String channelId = context.getResources().getString(R.string.default_notification_channel_id);
        String channelName = context.getResources().getString(R.string.default_notification_channel_name);

        notificationManager.createNotificationChannel(
                new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT));

        FirebaseInstanceId.getInstance().getInstanceId();
    }
}
