package com.nasaspaceapps.codebird.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nasaspaceapps.codebird.R;
import com.nasaspaceapps.codebird.database.DatabaseHelper;
import com.nasaspaceapps.codebird.interfaces.VolleyCallback;
import com.nasaspaceapps.codebird.pojo.User;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class UserRegistration {

    private RequestQueue requestQueue;
    private Context context;
    private User user;

    private String user_login_url = "http://52.26.68.140:8080/login";

    public UserRegistration(Context context, User user) {
        requestQueue = Volley.newRequestQueue(context);
        this.context = context;
        this.user = user;
    }

    public UserRegistration(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        this.context = context;
    }

    public void sendAgainForUserId() {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                user_login_url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Prefs.putBoolean("login_status", true);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("userDetails");


                    Log.e("Response", response);

                    Prefs.putString("user_id", jsonArray.getJSONObject(0).getString("user_id"));
                    Prefs.putString("token", jsonObject.getString("token"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("VolleyError", "Error: " + error.getMessage());
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", user.getEmail());
                params.put("gid", user.getGoogle_ID());

                return params;
            }

        };

        requestQueue.add(strReq);
    }


    public void sendRequest() {

        String user_registration_url = "http://52.26.68.140:8080/register";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                user_registration_url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    sendAgainForUserId();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("VolleyError", "Error: " + error.getMessage());
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_name", user.getFullname());
                params.put("email", user.getEmail());
                params.put("gid", user.getGoogle_ID());
                params.put("dp", user.getPic());

                return params;
            }

        };

        requestQueue.add(strReq);
    }


    public void sendSightData(final String ImageUrl) throws JSONException {
        String sight_data_url = "http://52.26.68.140:8080/put_userdata";
        final JSONObject sight_data = new JSONObject();
        int count = 0;
        try {
            sight_data.put("latitude", Prefs.getString("latitude", ""));
            sight_data.put("longitude", Prefs.getString("longitude", ""));
            sight_data.put("image_url", ImageUrl);

            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            count = databaseHelper.getCount();


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        final JSONArray jsonArray = new JSONArray();
        jsonArray.put(sight_data);

        if (count > 0) {
            Log.e("Count", count + "");
            DatabaseHelper databaseHelper = new DatabaseHelper(context);

            for (int i = 0; i < count; i++) {
                jsonArray.put(databaseHelper.getJsonObject(i + 1));
            }
        }


        StringRequest strReq = new StringRequest(Request.Method.POST,
                sight_data_url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                Log.e("Response", response);

                DatabaseHelper databaseHelper = new DatabaseHelper(context);
                databaseHelper.createJson(sight_data.toString());


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("VolleyError", "Error: " + error.getMessage());
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put("sighting_data", jsonArray.toString());
                params.put("user_id", Prefs.getString("user_id", ""));
                int score = Prefs.getInt("score", 0);
                params.put("user_score", String.valueOf(score));
                params.put("token", Prefs.getString("token", context.getString(R.string.token)));

                return params;
            }

        };

        requestQueue.add(strReq);
    }


    public void getSightData(final VolleyCallback callback) {

        String user_registration_url = "http://52.26.68.140:8080/user_profile";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                user_registration_url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("VolleyError", "Error: " + error.getMessage());
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", Prefs.getString("user_id", ""));
                params.put("token", Prefs.getString("token", ""));

                return params;
            }

        };

        requestQueue.add(strReq);
    }
}
