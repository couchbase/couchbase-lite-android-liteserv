package com.couchbase.liteservandroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.View;
import com.couchbase.lite.javascript.JavaScriptViewCompiler;
import com.couchbase.lite.listener.LiteListener;

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

        int port = startCBLListener(getListenPort());

        showListenPort(port);

    }

    private void showListenPort(int listenPort) {
        Log.d(TAG, "listenPort: " + listenPort);
        TextView listenPortTextView = (TextView)findViewById(R.id.listen_port_textview);
        listenPortTextView.setText(String.format("Listening on port: %d.  Db: %s", listenPort, DATABASE_NAME));
    }

    private int startCBLListener(int suggestedListenPort) {

        Manager manager = startCBLite();
        startDatabase(manager, DATABASE_NAME);

        LiteListener listener = new LiteListener(manager, suggestedListenPort);
        int port = listener.getListenPort();
        Thread thread = new Thread(listener);
        thread.start();

        return port;

    }

    protected Manager startCBLite() {
        Manager manager;
        manager = new Manager(getFilesDir(), Manager.DEFAULT_OPTIONS);
        return manager;
    }

    protected void startDatabase(Manager manager, String databaseName) {
        Database database = manager.getExistingDatabase(databaseName);
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
