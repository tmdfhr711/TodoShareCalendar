package com.example.todoshare.todosharecalendar.utils;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by OHRok on 2018-02-21.
 */

public class RegisterRequest extends StringRequest {
    final static private String URL = "http://plplim.ipdisk.co.kr:8000/todosharecalendar/UserRegister.php";
    private Map<String, String> parameters;

    public RegisterRequest(String userID, String userPassword, String userName, String userGroup, String userAuth, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPassword", userPassword);
        parameters.put("userName", userName);
        parameters.put("userGroup", userGroup);
        parameters.put("userAuth", userAuth);

    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
