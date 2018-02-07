package com.example.todoshare.todosharecalendar.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.todoshare.todosharecalendar.activity.MainActivity;
import com.example.todoshare.todosharecalendar.activity.SignupActivity;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Created by OHRok on 2018-01-23.
 */

public class AsyncSignupApi extends AsyncTask<String, Void, String> {

    private Context mContext;
    private RequestHandler mRequestHandler;
    private ProgressDialog loading;
    private RbPreference mPref;
    private String mUserId, mUserPass, mUserNick, mUserGroup, mUserToken;

    public AsyncSignupApi(Context mContext) {
        this.mContext = mContext;
        mRequestHandler = new RequestHandler();
        mPref = new RbPreference(mContext);

    }

    public AsyncSignupApi(Context mContext, String mUserId, String mUserPass, String mUserNick, String mUserGroup) {
        this.mContext = mContext;
        this.mUserId = mUserId;
        this.mUserPass = mUserPass;
        this.mUserNick = mUserNick;
        this.mUserGroup = mUserGroup;
        this.mUserToken = FirebaseInstanceId.getInstance().getToken();
        mRequestHandler = new RequestHandler();
        mPref = new RbPreference(mContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loading = new ProgressDialog(this.mContext);
        loading.setMessage("회원가입중...");
        loading.setCancelable(false);
        loading.show();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result.equals("success")) {
            Toast.makeText(mContext, "회원가입 완료", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(mContext, MainActivity.class);
            mContext.startActivity(intent);
            ((Activity) mContext).finish();
        } else {
            Toast.makeText(mContext, "회원가입중 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
        }
        loading.dismiss();

    }

    @Override
    protected String doInBackground(String... strings) {
        HashMap<String,String> data = new HashMap<>();

        data.put("id", mUserId);
        data.put("pass", getMD5Hash(mUserPass));
        data.put("nick", mUserNick);
        data.put("group", mUserGroup);
        data.put("token", mUserToken);

        String result = mRequestHandler.sendPostRequest(GlobalVar.SIGNUP_API,data);
        String resultVal = null;
        try {
            JSONArray ja = new JSONArray(result);
            Log.e("AsyncSignup_ja.length()", String.valueOf(ja.length()));
            for (int i = 0; i < ja.length(); i++) {
                JSONObject order = ja.getJSONObject(i);

                result = order.get("result").toString();
                Log.e("Signup resultVal", result);
                //Log.e("RegiCouponTask", order.get("shopphoto").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /*
     * Create Lai.OH
     * String 값을 받아와 MD5 형식으로 바꾼 뒤 Return 하는 함수
     */
    public static String getMD5Hash(String s) {
        MessageDigest m = null;
        String hash = null;

        try {
            m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(),0,s.length());
            hash = new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hash;
    }
}
