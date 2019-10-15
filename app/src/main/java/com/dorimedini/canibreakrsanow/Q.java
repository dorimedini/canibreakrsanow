package com.dorimedini.canibreakrsanow;


import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dorimedini.canibreakrsanow.models.Backend;
import com.dorimedini.canibreakrsanow.models.QResponse;

import java.util.ArrayList;

import androidx.core.util.Consumer;

class Q {
    private static final String TAG = "Q";
    // FIXME: Remove the host from source!
    private static final String scheme = "http";
    private static final String authority = "34.70.178.98:5000";


    private Handler mJobUpdateHandler;
    private final MainActivity mActivity;

    Q(final MainActivity activity) {
        mActivity = activity;
        mJobUpdateHandler = new Handler();
    }

    public void getBackends(final Consumer<ArrayList<Backend>> onResult) {
        getPathForUiThread("get_backends",
                new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        onResult.accept(Backend.fromJSON(s));
                    }
                });
    }

    private String cleanURL(final String url) {
        return url;  // YOLO
    }

    private void getPathForUiThread(final String path,
                                    final Consumer<String> onResponse) {
        getPathForUiThread(path, onResponse, null);
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
                        if (onError != null) {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onError.accept(error);
                                }
                            });
                        }
                    }
                });
        queue.add(stringRequest);
    }

    void requestJob(final int n, final int a, final Consumer<QResponse> onUpdate) {
        getPathForUiThread(String.format("new_job/%d/%d", n, a),
                new Consumer<String>() {
                    @Override
                    public void accept(String response) {
                        QResponse qResponse = QResponse.fromJSON(response);
                        if (onUpdate != null) {
                            onUpdate.accept(qResponse);
                        }
                        if (qResponse == null) {
                            Log.e(TAG, "Got null response from server");
                            return;
                        }
                        String status = qResponse.getStatus();
                        if (qResponse.isInFinalState()) {
                            Log.i(TAG, String.format("Job (N=%d,a=%d) done, final state: %s",
                                                     n, a, status));
                            return;
                        }
                        Log.i(TAG, String.format("Job (N=%d,a=%d) updated, state: %s",
                                                 n, a, status));
                        mJobUpdateHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                requestJob(n, a, onUpdate);
                            }
                        }, 3000);
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
                        mJobUpdateHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                requestJob(n, a, onUpdate);
                            }
                        }, 3000);
                    }
                });
    }
}
