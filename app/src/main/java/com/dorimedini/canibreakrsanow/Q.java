package com.dorimedini.canibreakrsanow;


import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

class Q {
    private static final String TAG = "Q";
    private static String host = "http://10.0.2.2:5000";

    static void getTmpString(final Activity activity, final TextView textView) {
        RequestQueue queue = Volley.newRequestQueue(activity);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, host,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Log.e(TAG, "Got response: " + response);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(String.format("Response is: %s", response));
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        Log.e(TAG, "Got error: " + error.toString());
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText("That didn't work!");
                            }
                        });
                    }
                });
        queue.add(stringRequest);
    }
}
