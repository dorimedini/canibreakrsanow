package com.dorimedini.canibreakrsanow;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText mEditN;
    private EditText mEditA;
    private Button mGoBtn;

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

        mQ = new Q(this);
    }

    public void onClickGoBtn(View btn) {
        mGoBtn.setEnabled(false);
        mEditN.setEnabled(false);
        mEditA.setEnabled(false);
        final int n = Integer.parseInt(mEditN.getText().toString());
        final int a = Integer.parseInt(mEditA.getText().toString());
        mQ.requestJob(n, a);
    }

    public void onResponseArrived(final String response) {
        mGoBtn.setEnabled(true);
        mEditN.setEnabled(true);
        mEditA.setEnabled(true);
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
