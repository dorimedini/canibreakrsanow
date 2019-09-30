package com.dorimedini.canibreakrsanow;


import android.content.Context;
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

    static void getTmpString(final Context context, final TextView textView) {
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, host,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Got response: " + response);
                        textView.setText(String.format("Response is: %s", response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Got error: " + error.toString());
                        textView.setText("That didn't work!");
                    }
                });
        queue.add(stringRequest);
    }
}
