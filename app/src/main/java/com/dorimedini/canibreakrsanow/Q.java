package com.dorimedini.canibreakrsanow;


import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.core.util.Consumer;

class Q {
    private static final String TAG = "Q";
    // FIXME: Remove the host from source!
    private static final String scheme = "http";
    private static final String authority = "34.70.178.98:5000";


    private final MainActivity mActivity;

    Q(final MainActivity activity) {
        mActivity = activity;
    }

    private String cleanURL(final String url) {
        return url;  // YOLO
    }

    private void getPathForUiThread(final String path,
                                    final Consumer<String> onResponse,
                                    final Consumer<VolleyError> onError) {
        RequestQueue queue = Volley.newRequestQueue(mActivity);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(scheme)
                .encodedAuthority(authority)
                .appendEncodedPath(path);
        String uri = cleanURL(builder.build().toString());
        Log.e(TAG, String.format("Send GET request to URI '%s'", uri));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Log.e(TAG, "Got response: " + response);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onResponse.accept(response);
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        Log.e(TAG, "Got error: " + error.toString());
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onError.accept(error);
                            }
                        });
                    }
                });
        queue.add(stringRequest);
    }

    void requestJob(final int n, final int a) {
        getPathForUiThread(String.format("new_job/%d/%d", n, a),
                new Consumer<String>() {
                    @Override
                    public void accept(String response) {
                        TextView tv = mActivity.findViewById(R.id.textview_log);
                        if (tv != null) {
                            tv.setText(String.format("Response is: %s", response));
                        } else {
                            Log.e(TAG, "TextView is null!");
                        }
                        mActivity.onResponseArrived(response);
                    }
                },
                new Consumer<VolleyError>() {
                    @Override
                    public void accept(VolleyError volleyError) {
                        TextView tv = mActivity.findViewById(R.id.textview_log);
                        if (tv != null) {
                            tv.setText(String.format("Error is: %s", volleyError.toString()));
                        } else {
                            Log.e(TAG, "TextView is null!");
                        }
                        mActivity.onResponseArrived(volleyError.toString());
                    }
                });
    }
}
