package com.example.todoshare.todosharecalendar.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("BOOT RECEIVER", "Boot");
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //전달 받은 Broadcast 값을 가져오기
        //androidmanifest.xml에 정의한 인텐트 필터를 받아 올 수 있습니다
        String action = intent.getAction();
        //전달된 값이 '부팅완료' 인 경우에만 동작하도록 조건문을 설정해줌
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            //TODO
            //부팅 이후 처리해야 할 코드 작성
            //Ex. 서비스 호출, 특정 액티비티 호출 등
            //알람들을 모두 저장하는 쓰레드
            AsyncAlarmApi api = new AsyncAlarmApi(context);
            api.execute();
        }
    }
}
