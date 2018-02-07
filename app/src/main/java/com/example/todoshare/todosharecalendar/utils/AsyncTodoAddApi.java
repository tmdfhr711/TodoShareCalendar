package com.example.todoshare.todosharecalendar.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.todoshare.todosharecalendar.activity.MainActivity;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

/**
 * Created by OHRok on 2018-01-24.
 */

public class AsyncTodoAddApi extends AsyncTask<String, Void, String> {

    private Context mContext;
    private RequestHandler mRequestHandler;
    private ProgressDialog loading;
    private RbPreference mPref;
    private String mDate, mTime, mTitle, mContent, mUserId, mGroup, mShare, mToken;
    private String year, month, day, hour, minute;
    public AsyncTodoAddApi(Context mContext, String mDate, String mTime, String mTitle, String mContent, String mUserId, String mGroup, String mShare) {
        this.mContext = mContext;
        this.mDate = mDate;
        this.mTime = mTime;
        this.mTitle = mTitle;
        this.mContent = mContent;
        this.mUserId = mUserId;
        this.mGroup = mGroup;
        this.mShare = mShare;
        this.mToken = FirebaseInstanceId.getInstance().getToken();
        mRequestHandler = new RequestHandler();
        mPref = new RbPreference(mContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loading = new ProgressDialog(this.mContext);
        loading.setMessage("일정을 등록중입니다...");
        loading.setCancelable(false);
        loading.show();
        String[] message = mDate.split("/");
        day = message[0];
        month = message[1];
        year = message[2];
        message = mTime.split(":");
        hour = message[0];
        minute = message[1];
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Intent intent = new Intent(mContext, MainActivity.class);
        mContext.startActivity(intent);
        ((Activity) mContext).finish();
        loading.dismiss();
    }

    @Override
    protected String doInBackground(String... strings) {
        HashMap<String,String> data = new HashMap<>();

        data.put("date", mDate);
        data.put("time", mTime);
        data.put("title", mTitle);
        data.put("content", mContent);
        data.put("userid",mUserId);
        data.put("group", mGroup);
        data.put("share", mShare);
        data.put("token", mToken);
        data.put("year", year);
        data.put("month", month);
        data.put("day", day);
        data.put("hour", hour);
        data.put("minute", minute);

        Log.e("AsyncTodoAdd group", mGroup);
        String result = mRequestHandler.sendPostRequest(GlobalVar.TODO_ADD,data);
        Log.e("result Data", result.toString());

        return result;
    }
}
