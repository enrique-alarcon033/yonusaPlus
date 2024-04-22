package com.yonusa.cercasyonusaplus.utilities.firebaseService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.yonusa.cercasyonusaplus.R;
/*
public class NotificationService extends Service {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel("my_channel_id", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("This is my channel description");
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_channel_id");
        builder.setContentTitle("My notification title");
        builder.setContentText("My notification text");
        builder.setSmallIcon(R.drawable.icon_panel);
        builder.setSound(Uri.parse("android.resource://com.example.myapp/raw/my_sound"));

        notificationManager.notify(1, builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
*/