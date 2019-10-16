package com.dorimedini.canibreakrsanow.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ShorResult {
    private static final String TAG = "ShortResult";
    private HashMap<Integer, Integer> mPeriodScores;

    class EntryList extends ArrayList<Map.Entry<Integer, Integer>> {}

    ShorResult(final JSONObject result) {
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

    EntryList topResults() {
        return topResults(0);
    }

    EntryList topResults(final int count) {
        EntryList list = new EntryList();
        list.addAll(mPeriodScores.entrySet());
        Collections.sort(list,
                new Comparator<Map.Entry>() {
                    public int compare(Map.Entry lhs, Map.Entry rhs) {
                        // Descending order: compare rhs to lhs
                        return Integer.compare((Integer)rhs.getValue(), (Integer)lhs.getValue());
                    }
                }
        );
        if (count > 0 && count < list.size()) {
            list.subList(count, list.size()).clear();
        }
        Log.e(TAG, String.format("Top %d results: %s", count, list.toString()));
        return list;
    }

    public int tryFactor(final int n, final int a) {
        EntryList list = topResults();
        BigInteger N = BigInteger.valueOf(n);
        for (Map.Entry entry: list) {
            int r = (Integer)entry.getValue();
            if (r % 2 == 1 || r == 0) {
                Log.e(TAG, String.format("Exponent %d is odd or zero", r));
                continue;
            }
            int candidate = powerMod(a, r/2, n);
            if (candidate == 1 || candidate == n - 1) {
                Log.e(TAG, String.format("Candidate %d is +-1 mod %d", candidate, n));
                continue;
            }
            ArrayList<BigInteger> possibleFactors = new ArrayList<>(Arrays.asList(
                    N.gcd(BigInteger.valueOf(candidate - 1)),
                    N.gcd(BigInteger.valueOf(candidate + 1))
            ));
            for (BigInteger factor: possibleFactors) {
                int intFactor = factor.intValue();
                if (intFactor > 1 && intFactor < n && n % intFactor == 0) {
                    Log.e(TAG, String.format("%d factors %d!", factor, n));
                    return factor.intValue();
                }
            }
            Log.e(TAG, String.format("Two candidates for r=%d have no good GCD (candidates are %s)",
                    r, possibleFactors.toString()));
        }
        Log.e(TAG, String.format("Couldn't factor %d using %d and results %s", N, a, list.toString()));
        return 1;  // Don't return 0 to prevent possible ArithmeticException on caller side
    }

    private int powerMod(int a, int pow, int mod) throws IllegalArgumentException {
        if (pow < 0) {
            throw new IllegalArgumentException(String.format("Can't send negative values to powerMod (got %d)", pow));
        }
        if (mod < 2) {
            throw new IllegalArgumentException(String.format("Can't send modulo<2 (got %d)", mod));
        }
        if (pow == 0) {
            return 1;
        }
        int aPowers = a;  // Will be a, a^2, a^4, a^8... etc
        int result = 1;   // If pow is 13, for example, result will be computed as a*a^4*a^8
        while (pow > 0) {
            if (pow % 2 == 1) {
                result = (result * aPowers) % mod;
            }
            aPowers = (aPowers * aPowers) % mod;
            pow /= 2;
        }
        return result;
    }
}
