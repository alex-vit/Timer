package com.alexvit.timer.timer;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.alexvit.timer.R;

public class TimerForegroundService extends IntentService {

    private static final String NAME = "com.alexvit.timer.timer.TimerForegroundService";
    private static final int ID_FOREGROUND = 123;

    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_STOP = "ACTION_STOP";

    public TimerForegroundService() {
        this(NAME);
    }

    public TimerForegroundService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        String action = intent.getAction();
        assert action != null;
        switch (action) {
            case ACTION_START:
                startForeground(ID_FOREGROUND, buildNotification());
                break;
            default:
                stopForeground(true);
                break;

        }
    }

    static void start(Context context) {
        send(context, ACTION_START);
    }

    static void stop(Context context) {
        send(context, ACTION_STOP);
    }

    private static void send(Context context, String action) {
        Intent intent = new Intent(context, TimerForegroundService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    private Notification buildNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationCompat.CATEGORY_SERVICE);
        builder.setOngoing(true).setContentTitle(getString(R.string.app_name));
        String channelId = createChannel();
        if (channelId != null) {
            builder.setChannelId(channelId);
        }
        return builder.build();
    }

    @Nullable
    private String createChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String id = NAME;
            String name = "TimerForegroundService";
            NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(channel);
            return id;
        }
        return null;
    }

}
