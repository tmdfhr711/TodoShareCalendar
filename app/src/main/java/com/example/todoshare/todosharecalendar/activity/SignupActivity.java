package com.example.todoshare.todosharecalendar.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.todoshare.todosharecalendar.R;
import com.example.todoshare.todosharecalendar.utils.AsyncSignupApi;
import com.example.todoshare.todosharecalendar.utils.CreateGroupRequest;
import com.example.todoshare.todosharecalendar.utils.GroupValidateRequest;
import com.example.todoshare.todosharecalendar.utils.IdValidateRequest;
import com.example.todoshare.todosharecalendar.utils.RbPreference;
import com.example.todoshare.todosharecalendar.utils.RegisterRequest;
import com.example.todoshare.todosharecalendar.utils.RequestHandler;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText user_id_edt;
    private EditText user_password_edt;
    private EditText user_pass_check_edt;
    private EditText user_nick_edt;
    private EditText user_group_edt;
    private Button group_validate_button;
    private Button id_validate_button;

    private Button sign_up_btn;
    private boolean id_validate = false;
    private boolean group_validate = false;

    private AlertDialog dialog;
    //public static RbPreference mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        init();
    }

    private void init(){
        user_id_edt = (EditText) findViewById(R.id.signup_user_id);
        user_password_edt = (EditText) findViewById(R.id.signup_user_password);
        user_nick_edt = (EditText) findViewById(R.id.signup_user_nick);
        user_group_edt = (EditText) findViewById(R.id.signup_user_group);

        sign_up_btn = (Button) findViewById(R.id.signup_button);
        id_validate_button = (Button) findViewById(R.id.signup_button_idvalidate);
        group_validate_button = (Button) findViewById(R.id.signup_button_groupvalidate);

        id_validate_button.setOnClickListener(this);
        group_validate_button.setOnClickListener(this);
        sign_up_btn.setOnClickListener(this);

    }

    private void signupModule(String userId, String userPass, String userNick, String userGroup){
        //mPref = new RbPreference(SignupActivity.this);
        if (userId != null && userPass != null && userNick != null && userGroup != null){
            AsyncSignupApi mSignup = new AsyncSignupApi(SignupActivity.this, userId,userPass,userNick,userGroup);
            mSignup.execute();

        } else {
            Toast.makeText(SignupActivity.this, "입력 실패", Toast.LENGTH_SHORT);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_button :
                //회원가입 버튼
                sign_up_btn.setEnabled(false);
                String userId = user_id_edt.getText().toString();
                String userPassword = user_password_edt.getText().toString();
                String userName = user_nick_edt.getText().toString();
                String userGroup = user_group_edt.getText().toString();
                String userAuth = FirebaseInstanceId.getInstance().getToken();
                if (!id_validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    dialog = builder.setMessage("아이디 중복 체크를 해주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }


                if(userId.equals("") || userPassword.equals("") || userName.equals("") || userGroup.equals("")){
                    sign_up_btn.setEnabled(true);
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    dialog = builder.setMessage("빈 칸 없이 입력해주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> resonseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                                dialog = builder.setMessage("회원 등록에 성공했습니다")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                SignupActivity.this.startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .create();
                                dialog.show();

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                                dialog = builder.setMessage("회원 등록에 실패했습니다")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(userId, userPassword, userName, userGroup, userAuth, resonseListener);
                RequestQueue queue = Volley.newRequestQueue(SignupActivity.this);
                queue.add(registerRequest);
                break;

            case R.id.signup_button_idvalidate:
                //ID 중복확인 버튼
                String userID = user_id_edt.getText().toString();
                if (id_validate) {
                    return;
                }
                if (userID.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    dialog = builder.setMessage("아이디는 빈 칸일 수 없습니다")
                            .setPositiveButton("확인", null)
                            .create();

                    dialog.show();
                    return;
                }

                Response.Listener<String> idResonseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                user_id_edt.setEnabled(false);
                                id_validate = true;
                                user_id_edt.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                id_validate_button.setBackgroundColor(getResources().getColor(R.color.colorGray));
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                                dialog = builder.setMessage("사용할 수 없는 아이디입니다")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                IdValidateRequest validateRequest = new IdValidateRequest(userID, idResonseListener);
                RequestQueue idQuere = Volley.newRequestQueue(SignupActivity.this);
                idQuere.add(validateRequest);
                break;

            case R.id.signup_button_groupvalidate:
                //그룹명 중복확인 버튼

                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    class SignupTask extends AsyncTask<String, Void, String> {

        final String SERVER_URL = "http://plplim.ipdisk.co.kr:8000/todosharecalendar/_todo_signup.php";
        RequestHandler rh = new RequestHandler();
        private ProgressDialog loading;
        private RbPreference mPref = new RbPreference(SignupActivity.this);
        private String getUserId, getUserNick;
        String resultVal;
        private String token;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(SignupActivity.this);
            loading.setMessage("회원가입중...");
            loading.setCancelable(false);
            loading.show();

            token = FirebaseInstanceId.getInstance().getToken();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("success")) {
                Toast.makeText(SignupActivity.this, "회원가입 완료", Toast.LENGTH_SHORT).show();
                //mPref.put("user_id",getUserId);
                //mPref.put("user_nick", getUserNick);
                //mPref.put("login","login");


                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SignupActivity.this, "회원가입중 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            }
            loading.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {


            HashMap<String,String> data = new HashMap<>();

            getUserId = params[0];
            getUserNick = params[2];

            data.put("userid", params[0]);
            data.put("pass", getMD5Hash(params[1]));
            data.put("nick", params[2]);
            data.put("group", user_group_edt.getText().toString());
            data.put("token", token);
            Log.e("TOKEN", token);
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