package com.couchbase.liteservandroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.javascript.JavaScriptViewCompiler;
import com.couchbase.lite.listener.LiteListener;

import java.io.IOException;

public class MainActivity extends Activity {

    private static final int DEFAULT_LISTEN_PORT = 5984;
    private static final String DATABASE_NAME = "cblite-test";
    private static final String LISTEN_PORT_PARAM_NAME = "listen_port";
    public static String TAG = "LiteServ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register the JavaScript view compiler
        View.setCompiler(new JavaScriptViewCompiler());

        try {
            int port = startCBLListener(getListenPort());
            showListenPort(port);
        } catch (Exception e) {
            TextView listenPortTextView = (TextView)findViewById(R.id.listen_port_textview);
            listenPortTextView.setText(String.format("Error starting LiteServ"));
            Log.e(TAG, "Error starting LiteServ", e);
        }

    }

    private void showListenPort(int listenPort) {
        Log.d(TAG, "listenPort: " + listenPort);
        TextView listenPortTextView = (TextView)findViewById(R.id.listen_port_textview);
        listenPortTextView.setText(String.format("Listening on port: %d.  Db: %s", listenPort, DATABASE_NAME));
    }

    private int startCBLListener(int suggestedListenPort) throws IOException, CouchbaseLiteException {

        Manager manager = startCBLite();
        startDatabase(manager, DATABASE_NAME);

        LiteListener listener = new LiteListener(manager, suggestedListenPort);
        int port = listener.getListenPort();
        Thread thread = new Thread(listener);
        thread.start();

        return port;

    }

    protected Manager startCBLite() throws IOException {
        Manager manager;
        manager = new Manager(new AndroidContext(getApplicationContext()), Manager.DEFAULT_OPTIONS);
        return manager;
    }

    protected void startDatabase(Manager manager, String databaseName) throws CouchbaseLiteException {
        Database database = manager.getDatabase(databaseName);
        database.open();
    }

    private int getListenPort() {
        return getIntent().getIntExtra(LISTEN_PORT_PARAM_NAME, DEFAULT_LISTEN_PORT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
