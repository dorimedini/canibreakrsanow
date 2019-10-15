package com.dorimedini.canibreakrsanow.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Backend {
    private static final String TAG = "Backend";
    private final String name;
    private final boolean simulator;
    private final int pendingJobs;
    private final int nQubits;

    Backend(final String name,
            final boolean isSimulator,
            final int pendingJobs,
            final int nQubits) {
        this.name = name;
        this.simulator = isSimulator;
        this.pendingJobs = pendingJobs;
        this.nQubits = nQubits;
    }

    public static ArrayList<Backend> fromJSON(final String jsonString) {
        JSONArray backendsObject;
        try {
            backendsObject = new JSONArray(jsonString);
        } catch (JSONException e) {
            Log.e(TAG, String.format("Not valid JSON string: %s", jsonString));
            return null;
        }
        ArrayList<Backend> backends = new ArrayList<>();
        try {
            for (int i = 0; i < backendsObject.length(); ++i) {
                JSONObject backendObj = backendsObject.getJSONObject(i);
                backends.add(new Backend(
                        backendObj.getString("name"),
                        backendObj.getBoolean("simulator"),
                        backendObj.getInt("pending_jobs"),
                        backendObj.getInt("n_qubits")
                    ));
            }
        } catch(JSONException e){
            Log.e(TAG, String.format("Exception thrown parsing json string '%s': %s", jsonString, e));
            return null;
        }
        return backends;
    }

    public String getName() {
        return name;
    }

    public boolean isSimulator() {
        return simulator;
    }

    public int getPendingJobs() {
        return pendingJobs;
    }

    public int getnQubits() {
        return nQubits;
    }
}
