package com.couchbase.liteservandroid;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.javascript.JavaScriptReplicationFilterCompiler;
import com.couchbase.lite.javascript.JavaScriptViewCompiler;
import com.couchbase.lite.listener.Credentials;
import com.couchbase.lite.listener.LiteListener;

import java.io.IOException;

public class MainActivity extends Activity {
    private static final int DEFAULT_LISTEN_PORT = 5984;
    private static final String DATABASE_NAME = "cblite-test";
    private static final String LISTEN_PORT_PARAM_NAME = "listen_port";
    private static final String LISTEN_LOGIN_PARAM_NAME = "username";
    private static final String LISTEN_PASSWORD_PARAM_NAME = "password";
    public static String TAG = "LiteServ";

    private Credentials allowedCredentials;
    private String storageType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // storage
        ApplicationInfo info = null;
        try {
            info = getApplicationContext().getPackageManager().getApplicationInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
            storageType = (String)info.metaData.get("storage");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "failed to obtain meta data from AndroidManifest.xml", e);
            storageType = "sqlite";
        }
        Log.i(TAG, "storageType=" + storageType);

        // Register the JavaScript view compiler
        View.setCompiler(new JavaScriptViewCompiler());
        Database.setFilterCompiler(new JavaScriptReplicationFilterCompiler());

        try {
            int port = startCBLListener(getListenPort());
            showListenPort(port);
            showListenCredentials();
        } catch (Exception e) {
            TextView listenPortTextView = (TextView) findViewById(R.id.listen_port_textview);
            listenPortTextView.setText(String.format("Error starting LiteServ"));
            Log.e(TAG, "Error starting LiteServ", e);
        }
    }

    private void showListenPort(int listenPort) {
        Log.d(TAG, "listenPort: " + listenPort);
        TextView listenPortTextView = (TextView) findViewById(R.id.listen_port_textview);
        listenPortTextView.setText(String.format("Listening on port: %d.  Db: %s Storage: %s", listenPort, DATABASE_NAME, storageType));
    }

    private void showListenCredentials() {
        TextView listenCredentialsTextView = (TextView) findViewById(R.id.listen_credentials_textview);
        String credentialsDisplay = String.format(
                "login: %s password: %s",
                allowedCredentials.getLogin(),
                allowedCredentials.getPassword()
        );
        Log.v(TAG, credentialsDisplay);
        listenCredentialsTextView.setText(credentialsDisplay);
    }

    private int startCBLListener(int suggestedListenPort) throws IOException, CouchbaseLiteException {
        Manager.enableLogging(TAG, com.couchbase.lite.util.Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG, com.couchbase.lite.util.Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_SYNC_ASYNC_TASK, com.couchbase.lite.util.Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_SYNC, com.couchbase.lite.util.Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_QUERY, com.couchbase.lite.util.Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_VIEW, com.couchbase.lite.util.Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_DATABASE, com.couchbase.lite.util.Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_BATCHER, com.couchbase.lite.util.Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_ROUTER, com.couchbase.lite.util.Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_CHANGE_TRACKER, com.couchbase.lite.util.Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_LISTENER, com.couchbase.lite.util.Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_REMOTE_REQUEST, com.couchbase.lite.util.Log.VERBOSE);

        Manager manager = startCBLite();
        startDatabase(manager, DATABASE_NAME);

        if (getLogin() != null && getPassword() != null) {
            if (getLogin().equals("none") && getPassword().equals("none")) {
                allowedCredentials = new Credentials("", "");
            } else {
                allowedCredentials = new Credentials(getLogin(), getPassword());
            }
        } else {
            allowedCredentials = new Credentials();
        }

        LiteListener listener = new LiteListener(manager, suggestedListenPort, allowedCredentials);
        int port = listener.getListenPort();
        Thread thread = new Thread(listener);
        thread.start();
        return port;
    }

    protected Manager startCBLite() throws IOException {
        Manager manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
        manager.setStorageType(storageType);
        return manager;
    }

    protected void startDatabase(Manager manager, String databaseName) throws CouchbaseLiteException {
        Database database = manager.getDatabase(databaseName);
        database.open();
    }

    private int getListenPort() {
        return getIntent().getIntExtra(LISTEN_PORT_PARAM_NAME, DEFAULT_LISTEN_PORT);
    }

    private String getLogin() {
        return getIntent().getStringExtra(LISTEN_LOGIN_PARAM_NAME);
    }

    private String getPassword() {
        return getIntent().getStringExtra(LISTEN_PASSWORD_PARAM_NAME);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
