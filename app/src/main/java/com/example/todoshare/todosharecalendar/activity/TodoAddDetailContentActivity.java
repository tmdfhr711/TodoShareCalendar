package com.example.todoshare.todosharecalendar.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.todoshare.todosharecalendar.R;
import com.example.todoshare.todosharecalendar.utils.AsyncTodoAddApi;
import com.example.todoshare.todosharecalendar.utils.RbPreference;
import com.example.todoshare.todosharecalendar.utils.RequestHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class TodoAddDetailContentActivity extends AppCompatActivity implements View.OnClickListener, TimePicker.OnTimeChangedListener {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    private RbPreference mPref = new RbPreference(TodoAddDetailContentActivity.this);
    Intent intent;

    TextView selected_date;
    TimePicker todo_time;
    EditText todo_title;
    EditText todo_content;
    CheckBox todo_share;
    Button todo_submit;

    String day, month, year;
    String mHour, mMinute;
    String mShare;
    String mTitle, mContent, mUserId,mUserGroup;


    private void init(){
        mUserId = mPref.getValue("user_id", "default");
        mUserGroup = mPref.getValue("user_group", "default");

        selected_date = (TextView) findViewById(R.id.detail_dateview);
        todo_time = (TimePicker) findViewById(R.id.detail_picker);
        todo_title = (EditText) findViewById(R.id.detail_title);
        todo_content = (EditText) findViewById(R.id.detail_content);
        todo_share = (CheckBox) findViewById(R.id.detail_checkshare);
        todo_submit = (Button) findViewById(R.id.detail_submitbutton);

        todo_time.setIs24HourView(false);
        todo_time.setOnTimeChangedListener(this);
        Calendar calendar = Calendar.getInstance();

        mHour = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
        mMinute = Integer.toString(calendar.get(Calendar.MINUTE));
        todo_time.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        todo_time.setCurrentMinute(calendar.get(Calendar.MINUTE));

        todo_submit.setOnClickListener(this);
        intent = getIntent();

        //선택된 calendar 날짜정보
        day = Integer.toString(intent.getExtras().getInt("day"));
        month = Integer.toString(intent.getExtras().getInt("month"));
        year = Integer.toString(intent.getExtras().getInt("year"));

        //선택된 날짜정보 출력
        //selected_date.setText(day + " / " + month + " / " + year);

        selected_date.setText("선택한 날짜 : " + year + " / " + Integer.toString(intent.getExtras().getInt("month") + 1) + " / " + day);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_add_detail_content);

        init();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detail_submitbutton:
                shareChecked();
                mTitle = todo_title.getText().toString();
                mContent = todo_content.getText().toString();

                AsyncTodoAddApi todoAddTask = new AsyncTodoAddApi(TodoAddDetailContentActivity.this, day + "/" + month + "/" + year,
                        mHour + ":" + mMinute, mTitle, mContent, mUserId, mUserGroup, mShare);
                todoAddTask.execute();
            break;
        }
    }

    //CheckBox의 값 상태확인
    private void shareChecked(){
        if (todo_share.isChecked()) {
            mShare = "1";
        } else {
            mShare = "0";
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(TodoAddDetailContentActivity.this, TodoAddCalendarActivity.class));
        finish();
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
        mHour = Integer.toString(hourOfDay);
        mMinute = Integer.toString(minute);
    }


    class SignupTask extends AsyncTask<String, Void, String> {

        //final String SERVER_URL = "http://210.117.181.66:8080/BShop/_bshop_signup.php";
        final String SERVER_URL = "http://plplim.ipdisk.co.kr:8000/todosharecalendar/_todo_add_calendar.php";
        RequestHandler rh = new RequestHandler();
        private ProgressDialog loading;

        String resultVal;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(TodoAddDetailContentActivity.this);
            loading.setMessage("일정을 등록중입니다...");
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            /*Log.e("result", result);
            if (result.equals("success")) {
                Toast.makeText(TodoAddDetailContentActivity.this, "일정등록 완료", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TodoAddDetailContentActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(TodoAddDetailContentActivity.this, "일정등록중 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            }*/
            Intent intent = new Intent(TodoAddDetailContentActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            loading.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {



            HashMap<String,String> data = new HashMap<>();

            data.put("date", day+"/"+month+"/"+year);
            data.put("time", mHour + "/" + mMinute);
            data.put("title", mTitle);
            data.put("content", mContent);
            data.put("userid",mUserId);
            data.put("group", mPref.getValue("user_group", ""));
            data.put("share", mShare);
            data.put("year", year);
            data.put("month", month);
            data.put("day", day);
            data.put("hour", mHour);
            data.put("minute", mMinute);

            String result = rh.sendPostRequest(SERVER_URL,data);
            Log.e("result Data", result.toString());

            try {
                JSONArray ja = new JSONArray(result);
                Log.e("ja.length()", String.valueOf(ja.length()));
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject order = ja.getJSONObject(i);

                    resultVal = order.get("result").toString();
                    //Log.e("RegiCouponTask", order.get("shopphoto").toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultVal;
        }
    }
}
