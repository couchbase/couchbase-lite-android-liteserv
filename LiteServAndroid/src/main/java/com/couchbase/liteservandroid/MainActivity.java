package com.couchbase.liteservandroid;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

    public static String TAG = "LiteServ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showListenPort();
    }

    private void showListenPort() {
        int listenPort = getIntent().getIntExtra("listen_port", 5986);
        Log.d(TAG, "listenPort: " + listenPort);
        TextView listenPortTextView = (TextView)findViewById(R.id.listen_port_textview);
        listenPortTextView.setText("Listening on port: " + listenPort);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
