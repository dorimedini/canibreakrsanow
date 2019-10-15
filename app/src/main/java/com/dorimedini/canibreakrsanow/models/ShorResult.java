package com.dorimedini.canibreakrsanow.models;

import android.util.Log;

import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ShorResult {
    private static final String TAG = "ShortResult";
    private HashMap<Integer, Integer> mPeriodScores;

    public ShorResult(final JSONObject result) {
        mPeriodScores = new HashMap<>();
        try {
            Iterator<String> keys = result.keys();
            while (keys.hasNext()) {
                String binaryNum = keys.next();
                int score = result.getInt(binaryNum);
                int num = Integer.parseInt(binaryNum, 2);
                mPeriodScores.put(num, score);
            }
        } catch (JSONException e) {
            Log.e(TAG, String.format("Couldn't parse JSON Shor result object: %s", result.toString()));
        }
    }

    public ArrayList<Map.Entry<Integer, Integer>> topResults(final int count) {
        ArrayList<Map.Entry<Integer, Integer>> list = new ArrayList<>(mPeriodScores.entrySet());
        Collections.sort(list,
                new Comparator<Map.Entry>() {
                    public int compare(Map.Entry lhs, Map.Entry rhs) {
                        // Descending order: compare rhs to lhs
                        return Integer.compare((Integer)rhs.getValue(), (Integer)lhs.getValue());
                    }
                }
        );
        if (count < list.size()) {
            list.subList(count, list.size()).clear();
        }
        Log.e(TAG, String.format("Top %d results: %s", count, list.toString()));
        return list;
    }
}
