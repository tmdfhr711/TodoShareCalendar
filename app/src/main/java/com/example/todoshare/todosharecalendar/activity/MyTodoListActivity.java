package com.example.todoshare.todosharecalendar.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.todoshare.todosharecalendar.R;
import com.example.todoshare.todosharecalendar.adapter.TodoListAdapter;
import com.example.todoshare.todosharecalendar.model.Todo;
import com.example.todoshare.todosharecalendar.utils.RbPreference;
import com.example.todoshare.todosharecalendar.utils.RequestHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MyTodoListActivity extends AppCompatActivity {

    private RbPreference mPref = new RbPreference(this);

    private TodoListAdapter mAdapter;
    private ArrayList<Todo> todolist;
    private ListView my_listview;

    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_todo_list);

        init();
    }

    private void init(){
        my_listview = (ListView) findViewById(R.id.my_todo_listview);
        mUserId = mPref.getValue("user_id", "");

        todolist = new ArrayList<>();

        GetMyTodoListTask task = new GetMyTodoListTask();
        task.execute(mUserId);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MyTodoListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    class GetMyTodoListTask extends AsyncTask<String, Void, Void> {

        final String SERVER_URL = "http://plplim.ipdisk.co.kr:8000/todosharecalendar/_my_todo_list_get.php";
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
            mAdapter = new TodoListAdapter(MyTodoListActivity.this, todolist);
            //Log.e("todolist", String.valueOf(todolist));
            my_listview.setAdapter(mAdapter);
        }

        @Override
        protected Void doInBackground(String... params) {


            HashMap<String,String> data = new HashMap<>();

            String query = params[0];

            data.put("userid", query);
            //data.put("group", mPref.getValue("user_group", ""));

            //Log.e("thread get date", query);
            //Log.e("thread get group", mPref.getValue("user_group", ""));

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
}
