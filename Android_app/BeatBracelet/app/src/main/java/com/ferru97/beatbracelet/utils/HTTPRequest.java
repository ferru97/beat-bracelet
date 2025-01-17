package com.ferru97.beatbracelet.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class HTTPRequest {

    public static void POST_Request(final String reqName,Context context, String url, final HashMap<String,String> params,final HTTPResponseHandler handler){
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handler.handleResponse(reqName,response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("POST_ERROR",error.toString());
                    }
        }){
            @Override
            public Map<String, String> getParams()  {
                return params;
            }
        };

        queue.add(stringRequest);
    }
}
