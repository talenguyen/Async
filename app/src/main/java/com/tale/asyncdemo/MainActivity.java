package com.tale.asyncdemo;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.tale.async.Action0;
import com.tale.async.Action1;
import com.tale.async.Async;
import com.tale.async.Result;
import com.tale.async.Task;

public class MainActivity extends AppCompatActivity {

    TextView tvResult;
    TextView lbResult;
    Async<String, String> async;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lbResult = ((TextView) findViewById(R.id.lbResult));
        tvResult = ((TextView) findViewById(R.id.tvResult));

        findViewById(R.id.btExecuteSuccess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeSuccess();
            }
        });

        findViewById(R.id.btExecuteError).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeError();
            }
        });

        async = Async.<String, String>newInstance(new Task() {
            @Override
            public Result call(Object... params) {
                SystemClock.sleep(2000);
                final String param = (String) params[0];
                return success(param);
            }
        }).onSuccess(new Action1<String>() {
            @Override
            public void call(String s) {
                tvResult.setText(s);
            }
        }).onError(new Action1<String>() {
            @Override
            public void call(String s) {
                tvResult.setText(s);
            }
        }).onComplete(new Action0() {
            @Override
            public void call() {
                hideLoading();
            }
        });
    }

    private void executeSuccess() {
        showLoading();
        async.executeOnThreadPool("Successful!");
    }

    private void executeError() {
        showLoading();
        async.executeOnThreadPool("Error!");
    }

    private void showLoading() {
        lbResult.setText("Executing...");
    }

    private void hideLoading() {
        lbResult.setText("Result:");
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
