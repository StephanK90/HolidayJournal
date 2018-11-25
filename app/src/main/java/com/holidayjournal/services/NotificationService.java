package com.holidayjournal.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.holidayjournal.R;
import com.holidayjournal.ui.splash.SplashActivity;
import com.holidayjournal.utils.DateFormatter;

import org.joda.time.DateTime;

public class NotificationService extends Service {

    private static BroadcastReceiver mReceiver;
    private static String CHANNEL_ID = "holiday_notification";
    private static DateTime holidayDate;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("notification_service", "service started!");
        if (intent != null) {
            long dateInMillis = intent.getLongExtra("date", 0);

            if (dateInMillis != 0) {
                try {
                    holidayDate = DateFormatter.toDate(dateInMillis);
                    registerBroadcastReceiver();
                } catch (Exception e) {
                    e.printStackTrace();
                    stopService();
                }

            } else {
                stopService();
            }
        }

        return START_REDELIVER_INTENT;
    }

    private void registerBroadcastReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("notification_service", "broadcast received!");
                if (intent.getAction() != null)
                    if (intent.getAction().equalsIgnoreCase(Intent.ACTION_DATE_CHANGED)) {
                        DateTime currentDate = DateFormatter.toDate(System.currentTimeMillis());

                        if (currentDate.toLocalDate().equals(holidayDate.toLocalDate())) {
                            sendNotification();
                            stopService();
                        }
                    }
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_DATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    private void unRegisterBroadcastReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private void sendNotification() {
        Log.d("notification_service", "notification sent!");
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.app_notif)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, mBuilder.build());
    }

    private void stopService() {
        Log.d("notification_service", "service stopped!");
        unRegisterBroadcastReceiver();
        stopSelf();
    }

    @Override
    public void onDestroy() {
        Log.d("notification_service", "service stopped!");
        unRegisterBroadcastReceiver();
    }

}