package com.dorimedini.canibreakrsanow;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Consumer;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dorimedini.canibreakrsanow.models.Backend;
import com.dorimedini.canibreakrsanow.models.QResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("hh:mm:ss");

    private EditText mEditN;
    private EditText mEditA;
    private Button mGoBtn;
    private Button mBackendsBtn;
    private TextView mLogText;
    private TextView mPeriodCandidates;

    private Q mQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEditN = findViewById(R.id.edittext_N);
        mEditA = findViewById(R.id.edittext_A);
        mGoBtn = findViewById(R.id.go_btn);
        mBackendsBtn = findViewById(R.id.backends_btn);
        mLogText = findViewById(R.id.textview_log);
        mPeriodCandidates = findViewById(R.id.period_candidates);

        mQ = new Q(this);
    }

    private void disableUi() {
        mGoBtn.setEnabled(false);
        mEditN.setEnabled(false);
        mEditA.setEnabled(false);
        mBackendsBtn.setEnabled(false);
    }

    private void enableUi() {
        mGoBtn.setEnabled(true);
        mEditN.setEnabled(true);
        mEditA.setEnabled(true);
        mBackendsBtn.setEnabled(true);
    }

    public void onClickGoBtn(View btn) {
        disableUi();
        mPeriodCandidates.setText("");
        final int n = Integer.parseInt(mEditN.getText().toString());
        final int a = Integer.parseInt(mEditA.getText().toString());
        mQ.requestJob(n, a, new Consumer<QResponse>() {
            @Override
            public void accept(QResponse qResponse) {
                mLogText.setText(String.format("Received response at %s:\n%s",
                        dateFormatter.format(Calendar.getInstance().getTime()),
                        qResponse.getServerResponse()));
                if (qResponse.isInFinalState()) {
                    enableUi();
                }
                if (qResponse.isDone() && qResponse.getResult() != null) {
                    ArrayList<Map.Entry<Integer, Integer>> entries = qResponse.resultsToEntries(5);
                    mPeriodCandidates.setText("Possible periods: ");
                    boolean first = true;
                    for (Map.Entry<Integer, Integer> entry: entries) {
                        if (first) {
                            mPeriodCandidates.append(String.format("%d", entry.getKey()));
                            first = false;
                        } else {
                            mPeriodCandidates.append(String.format(", %d", entry.getKey()));
                        }
                    }
                }
            }
        });
    }

    public void onClickBackendsBtn(View btn) {
        disableUi();
        mQ.getBackends(new Consumer<ArrayList<Backend>>() {
            @Override
            public void accept(ArrayList<Backend> backends) {
                String backendsStr = "Available backends:";
                for (Backend backend: backends) {
                    backendsStr = backendsStr.concat(String.format(
                            "\n<name=%s, sim=%s, qubits=%d, pending_jobs=%d>",
                            backend.getName(),
                            backend.isSimulator() ? "true" : "false",
                            backend.getnQubits(),
                            backend.getPendingJobs()
                        ));
                }
                mLogText.setText(backendsStr);
                enableUi();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
