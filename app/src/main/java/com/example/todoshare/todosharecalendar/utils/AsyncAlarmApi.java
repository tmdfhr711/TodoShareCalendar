package com.example.todoshare.todosharecalendar.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.todoshare.todosharecalendar.model.Todo;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by OHRok on 2018-02-07.
 */

public class AsyncAlarmApi extends AsyncTask<String, Void, String> {

    private ArrayList<Todo> todolist;
    final String SERVER_URL = "http://plplim.ipdisk.co.kr:8000/todosharecalendar/_calendar_get_todolist.php";
    RequestHandler rh = new RequestHandler();
    private RbPreference mPref;
    private Context mContext;
    int nYear, nMonth, nDay, nHour, nMinute;

    public AsyncAlarmApi(Context context) {
        this.mContext = context;
        mPref = new RbPreference(mContext);
    }

    public void registerAlarm(int year, int month, int day, int hour, int minute){
        AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(mContext , AlarmReceive.class);

        PendingIntent pender = PendingIntent.getBroadcast(mContext, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pender);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        todolist = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        nYear = calendar.get(Calendar.YEAR);
        nMonth = calendar.get(Calendar.MONTH);
        nDay = calendar.get(Calendar.DAY_OF_MONTH);
        nHour = calendar.get(Calendar.HOUR_OF_DAY);
        nMinute = calendar.get(Calendar.MINUTE);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //가져온 데이터를 가지고 알람을 등록하는 코드작성
        for (Todo array : todolist) {
            String[] getDate = array.getmDate().split("/");
            String[] getTime = array.getmTime().split(":");
            int day = Integer.parseInt(getDate[0]);
            int month = Integer.parseInt(getDate[1]);
            int year = Integer.parseInt(getDate[2]);
            int hour = Integer.parseInt(getTime[0]);
            int minute = Integer.parseInt(getTime[1]);

            //현재 시간보다 뒤일 경우만 알람 등록
            if (nYear <= year && nMonth <= month && nDay <= day) {
                if(nHour <= hour && nMinute < minute) {
                    Log.e("AsyncAlarmApi : " , String.valueOf(nYear) + "," + String.valueOf(nMonth) + "," +String.valueOf(nDay) + "," +
                            String.valueOf(nHour) + "," +String.valueOf(nMinute));
                    registerAlarm(year, month, day,hour,minute);
                }
            }
        }
    }

    @Override
    protected String doInBackground(String... strings) {

        HashMap<String,String> data = new HashMap<>();

        String query = mPref.getValue("user_group", "");

        data.put("group", query);

        String result = rh.sendPostRequest(SERVER_URL,data);
        Log.e("result Data", result.toString());

        todolist.clear();
        try {
            JSONArray ja = new JSONArray(result);
            Log.e("ja.length()", String.valueOf(ja.length()));
            for (int i = 0; i < ja.length(); i++) {
                JSONObject order = ja.getJSONObject(i);

                Log.e("MY TODO LIST", String.valueOf(order));
                String todonum = order.get("todonum").toString();
                String tododate = order.get("tododate").toString();
                String todotime = order.get("todotime").toString();
                String todotitle = order.get("todotitle").toString();
                String todocontent = order.get("todocontent").toString();
                String userid = order.get("userid").toString();
                String share = order.get("share").toString();

                todolist.add(new Todo(todonum, tododate, todotime, todotitle, todocontent, userid, share));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
