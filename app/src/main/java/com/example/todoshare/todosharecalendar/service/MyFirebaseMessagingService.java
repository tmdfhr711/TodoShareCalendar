package com.example.todoshare.todosharecalendar.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.todoshare.todosharecalendar.R;
import com.example.todoshare.todosharecalendar.activity.MainActivity;
import com.example.todoshare.todosharecalendar.utils.AlarmReceive;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private int year, month, day, hour, minute;
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        PowerManager pm = ((PowerManager) getSystemService(Context.POWER_SERVICE));
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "알람");
        wakeLock.acquire();


        Log.i(TAG, "onMessageReceived");

        Map<String, String> data = remoteMessage.getData();
        Log.e(TAG, data.toString());
        String[] message = data.get("message").split(",");
        year = Integer.parseInt(message[2]);
        month = Integer.parseInt(message[3]);
        day = Integer.parseInt(message[4]);
        hour = Integer.parseInt(message[5]);
        minute = Integer.parseInt(message[6]);
        sendNotification(message[0], message[1]);
        //공유상태가 0일 경우 알림이 가지 않도록 설정해야함
        wakeLock.release();
    }

    private void sendNotification(String title, String content) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("새 글이 등록되었습니다")
                .setContentText(title)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        int nYear, nMonth, nDay, nHour, nMinute;

        Calendar calendar = Calendar.getInstance();
        nYear = calendar.get(Calendar.YEAR);
        nMonth = calendar.get(Calendar.MONTH);
        nDay = calendar.get(Calendar.DAY_OF_MONTH);
        nHour = calendar.get(Calendar.HOUR_OF_DAY);
        nMinute = calendar.get(Calendar.MINUTE);

        //값을 가져와서 현재 시간이랑 비교
        //현재 시간보다 일정이 뒤일 경우 알람등록
        Log.e("NOW DATE : " , String.valueOf(nYear) + "," + String.valueOf(nMonth) + "," +String.valueOf(nDay) + "," +
                String.valueOf(nHour) + "," +String.valueOf(nMinute));
        if (nYear <= year && nMonth <= month && nDay <= day) {
            if(nHour <= hour && nMinute < minute) {
                registerAlarm();
            }
        }

    }

    private void registerAlarm(){



        Log.e("RegisterAlarm", "add");
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceive.class);

        PendingIntent pender = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);

        //알림을 한 시간 전에 설정하기
        if(hour == 0){
            //hour를 23(오후 11시)로 설정 한뒤 calendar.set(hour - 1)
        } else {
            //그냥 그대로 진행 calendar.set(hour)
        }
        alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pender);
    }

}
