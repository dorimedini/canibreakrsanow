package com.dorimedini.canibreakrsanow.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import androidx.annotation.Nullable;

public class QResponse {
    private static final String TAG = "QResponse";

    public static final ArrayList<String> FINAL_STATES = new ArrayList<>(Arrays.asList(
            "DONE", "CANCELLED", "ERROR", "JOB_NOT_FOUND"
        ));

    private final String key;
    private final String status;
    private final String backendName;
    private final boolean isSimulator;
    private final int nQubits;
    private final String serverResponse;
    private final int queuePosition;
    private final ShorResult result;
    private final String error;

    private QResponse(final String key,
                      final String status,
                      final String backendName,
                      final boolean isSimulator,
                      final int nQubits,
                      final String serverResponse,
                      final int queuePosition,
                      final @Nullable ShorResult result,
                      final @Nullable String error) {
        this.key = key;
        this.status = status;
        this.backendName = backendName;
        this.isSimulator = isSimulator;
        this.nQubits = nQubits;
        this.serverResponse = serverResponse;
        this.queuePosition = queuePosition;
        this.result = result;
        this.error = error;
    }

    public static QResponse fromJSON(final String json) {
        QResponse qResponse = null;
        try {
            JSONObject obj = new JSONObject(json);
            qResponse = new QResponse(
                    obj.getString("key"),
                    obj.getString("status"),
                    obj.getString("backend"),
                    obj.getBoolean("is_simulator"),
                    obj.getInt("n_qubits"),
                    obj.getString("server_response"),
                    obj.getInt("queue_position"),
                    obj.isNull("result") ? null : new ShorResult(obj.getJSONObject("result")),
                    obj.isNull("error") ? null : obj.getString("error")
            );
        } catch (JSONException e) {
            Log.e(TAG, String.format("Exception thrown while parsing JSON string '%s': %s", json, e));
        }
        return qResponse;
    }

    public ShorResult.EntryList resultsToEntries() {
        return resultsToEntries(0);
    }

    public ShorResult.EntryList resultsToEntries(final int topResults) {
        return result == null ? null : result.topResults(topResults);
    }

    public boolean isInFinalState() {
        return FINAL_STATES.contains(status);
    }

    public boolean isDone() {
        return status.equals("DONE");
    }

    public String getKey() {
        return key;
    }

    public String getStatus() {
        return status;
    }

    public String getBackendName() {
        return backendName;
    }

    public boolean isSimulator() {
        return isSimulator;
    }

    public int getnQubits() {
        return nQubits;
    }

    public String getServerResponse() {
        return serverResponse;
    }

    public int getQueuePosition() {
        return queuePosition;
    }

    public ShorResult getResult() {
        return result;
    }

    public String getError() {
        return error;
    }
}
