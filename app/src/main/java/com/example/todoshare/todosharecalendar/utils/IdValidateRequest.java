package com.example.todoshare.todosharecalendar.utils;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by OHRok on 2018-02-21.
 */

public class IdValidateRequest extends StringRequest {

    final static private String URL = "http://plplim.ipdisk.co.kr:8000/todosharecalendar/UserValidate.php";
    private Map<String, String> parameters;

    public IdValidateRequest(String userID, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
