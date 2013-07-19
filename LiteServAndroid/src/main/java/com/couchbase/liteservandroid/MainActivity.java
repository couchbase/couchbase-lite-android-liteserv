package com.couchbase.liteservandroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.couchbase.cblite.CBLDatabase;
import com.couchbase.cblite.CBLServer;
import com.couchbase.cblite.CBLView;
import com.couchbase.cblite.javascript.CBLJavaScriptViewCompiler;
import com.couchbase.cblite.listener.CBLListener;

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
        CBLView.setCompiler(new CBLJavaScriptViewCompiler());

        int port = startCBLListener(getListenPort());

        showListenPort(port);

    }

    private void showListenPort(int listenPort) {
        Log.d(TAG, "listenPort: " + listenPort);
        TextView listenPortTextView = (TextView)findViewById(R.id.listen_port_textview);
        listenPortTextView.setText(String.format("Listening on port: %d.  Db: %s", listenPort, DATABASE_NAME));
    }

    private int startCBLListener(int suggestedListenPort) {

        CBLServer server = startCBLite();
        startDatabase(server, DATABASE_NAME);

        CBLListener listener = new CBLListener(server, suggestedListenPort);
        int port = listener.getListenPort();
        Thread thread = new Thread(listener);
        thread.start();

        return port;

    }

    protected CBLServer startCBLite() {
        CBLServer server;
        try {
            server = new CBLServer(getFilesDir().getAbsolutePath());
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
        return server;
    }

    protected void startDatabase(CBLServer server, String databaseName) {
        CBLDatabase database = server.getDatabaseNamed(databaseName, true);
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
