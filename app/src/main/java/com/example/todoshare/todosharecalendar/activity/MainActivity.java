package com.example.todoshare.todosharecalendar.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;

import com.example.todoshare.todosharecalendar.R;
import com.example.todoshare.todosharecalendar.adapter.TodoListAdapter;
import com.example.todoshare.todosharecalendar.model.Todo;
import com.example.todoshare.todosharecalendar.utils.AsyncAlarmApi;
import com.example.todoshare.todosharecalendar.utils.EventDecorator;
import com.example.todoshare.todosharecalendar.utils.OneDayDecorator;
import com.example.todoshare.todosharecalendar.utils.RbPreference;
import com.example.todoshare.todosharecalendar.utils.RequestHandler;
import com.example.todoshare.todosharecalendar.utils.SaturdayDecorator;
import com.example.todoshare.todosharecalendar.utils.SundayDecorator;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , View.OnClickListener, OnDateSelectedListener, OnMonthChangedListener {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    //material calendar 생성
    MaterialCalendarView materialCalendarView;
    String getData;
    private RbPreference mPref = new RbPreference(this);

    private TodoListAdapter mAdapter;
    private ArrayList<Todo> todolist;

    private ListView todo_listview;

    Menu menu;
    MenuItem nav_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        //일정 등록 버튼으로 활용
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TodoAddCalendarActivity.class);
                intent.putExtra("date", getData);
                startActivity(intent);
                finish();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        menu = navigationView.getMenu();
        nav_logout = menu.findItem(R.id.nav_logout);

        init();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mytodolist) {
            // Handle the camera action
            Intent intent = new Intent(MainActivity.this, MyTodoListActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_gallery) {
            /*Intent intent = new Intent(MainActivity.this, SigninActivity.class);
            startActivity(intent);
            finish();*/
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            //AsyncAlarmApi api = new AsyncAlarmApi(MainActivity.this);
            //api.execute();
        } else if (id == R.id.nav_logout) {
            mPref.removeAllValue();
            mPref.put("login","logout");
            finish();
            checkForLogin();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void init(){
        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        //dateText = (TextView) findViewById(R.id.date_textview);
        //button = (Button) findViewById(R.id.button);
        todo_listview = (ListView) findViewById(R.id.todo_listview);
        todolist = new ArrayList<>();

        materialCalendarView.setOnDateChangedListener(this);
        materialCalendarView.setOnMonthChangedListener(this);
        materialCalendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);

        //button.setOnClickListener(this);

        //Setup initial text
        //dateText.setText(getSelectedDatesString());

        //material calendar 초기화
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(1992, 1, 1))
                .setMaximumDate(CalendarDay.from(2100, 12, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new OneDayDecorator());


        checkForLogin();
        GetTodoListTask api = new GetTodoListTask();
        api.execute();

        //FirebaseMessaging.getInstance().subscribeToTopic("news");
        //FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        GetTodoListTask api = new GetTodoListTask();
        api.execute();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        getSelectDayList(getSelectedDatesString());
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

    }

    private String getSelectedDatesString(){
        CalendarDay date = materialCalendarView.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        getData = FORMATTER.format(date.getDate());
        //day = date.getDay();
        String getDate = date.getDay() + "/" + date.getMonth() + "/" + date.getYear();
        Log.e("getDate", getDate);
        return getDate;
    }
    private void getSelectDayList(String getDate) {
        GetSelectedDayListTask task = new GetSelectedDayListTask();
        task.execute(getDate);
    }

    private void checkForLogin(){

        /*
         * 회원가입 유무에 따른 action 설정 함수
         * 로그인 상태 ("login","login")
         * 로그아웃 상태 ("login","logout")
         * 미가입 회원 ("login","")
         *
         * 각 상태를 확인 후 dialog를 띄워 해당 action을 수행
         */
        String getLoginCheck = mPref.getValue("login","");
        if(getLoginCheck.equals("")){
            //기존 회원이 아닌경우
            Intent intent = new Intent(MainActivity.this, SigninActivity.class);
            startActivity(intent);
            finish();
            //openBottomSheet(R.string.bottom_sheet_title_member, R.string.bottom_sheet_login, R.string.bottom_sheet_signup,BOTTOM_CASEVAL2);

        } else if (getLoginCheck.equals("logout")) {
            Intent intent = new Intent(MainActivity.this, SigninActivity.class);
            startActivity(intent);
            finish();
            //openBottomSheet(R.string.bottom_sheet_title_member, R.string.bottom_sheet_login, R.string.bottom_sheet_signup,BOTTOM_CASEVAL2);

        } else if (getLoginCheck.equals("login")){
            //현재 로그인 되어있는 경우
            //new CreateAuthUtil(getApplicationContext()).execute(mPref.getValue("user_num", ""), mPref.getValue("device_id", ""), mPref.getValue("gcm_reg_id", ""));
            //user_login_tv.setText(mPref.getValue("user_id", ""));
            //user_nick_tv.setText(mPref.getValue("user_nick", ""));
        }
    }



    private class GetTodoListTask extends AsyncTask<Void, Void, List<CalendarDay>> {

        final String SERVER_URL = "http://plplim.ipdisk.co.kr:8000/todosharecalendar/_calendar_get_todolist.php";
        RequestHandler rh = new RequestHandler();
        ArrayList<CalendarDay> days = new ArrayList<>();

        String resultVal;
        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {


            //String getGroup = mPref.getValue("user_group", "");
            // month 시작 : 0(1월) ~ 11(12월)
            //Log.e("MainActivity", getGroup);


            HashMap<String,String> data = new HashMap<>();
            data.put("group", mPref.getValue("user_group", ""));
            Log.e("MainResultString", mPref.getValue("user_group", ""));
            String result = rh.sendPostRequest(SERVER_URL,data);
            Log.e("MainResultString", result.toString());

            try {
                JSONArray ja = new JSONArray(result);
                Log.e("ja.length()", String.valueOf(ja.length()));
                Log.e("GET", String.valueOf(ja));
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject order = ja.getJSONObject(i);


                    String todonum = order.get("todonum").toString();
                    String tododate = order.get("tododate").toString();
                    String todotime = order.get("todotime").toString();
                    String todotitle = order.get("todotitle").toString();
                    String todocontent = order.get("todocontent").toString();
                    String userid = order.get("userid").toString();
                    String share = order.get("share").toString();

                    String[] date = tododate.split("/");
                    CalendarDay day = CalendarDay.from(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]));
                    days.add(day);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return days;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }

            materialCalendarView.addDecorator(new EventDecorator(Color.RED, calendarDays));
            //Log.e("Listview", String.valueOf(todolist));

        }
    }

    class GetSelectedDayListTask extends AsyncTask<String, Void, Void> {

        final String SERVER_URL = "http://plplim.ipdisk.co.kr:8000/todosharecalendar/_calendar_get_selected_day_list.php";
        RequestHandler rh = new RequestHandler();
        private ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*loading = new ProgressDialog(MyCouponListActivity.this);
            loading.setMessage("목록 불러오는중...");
            loading.setCancelable(false);
            loading.show();*/
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter = new TodoListAdapter(MainActivity.this, todolist);
            Log.e("todolist", String.valueOf(todolist));
            todo_listview.setAdapter(mAdapter);
        }

        @Override
        protected Void doInBackground(String... params) {


            HashMap<String,String> data = new HashMap<>();

            String query = params[0];

            data.put("date", query);
            data.put("group", mPref.getValue("user_group", ""));

            Log.e("thread get date", query);
            Log.e("thread get group", mPref.getValue("user_group", ""));

            String result = rh.sendPostRequest(SERVER_URL,data);
            Log.e("result Data", result.toString());

            todolist.clear();
            try {
                JSONArray ja = new JSONArray(result);
                Log.e("ja.length()", String.valueOf(ja.length()));
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject order = ja.getJSONObject(i);

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
}
