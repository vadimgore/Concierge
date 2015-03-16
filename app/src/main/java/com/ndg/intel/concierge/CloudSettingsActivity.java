package com.ndg.intel.concierge;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class CloudSettingsActivity extends ActionBarActivity {

    private TextView mGcmStatus;
    private TextView mGcmRegid;
    private EditText mBackendIP;
    private EditText mBackendPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String gcm_regid = getIntent().getExtras().getString("@string/gcm_regid");
        mGcmStatus = (TextView) findViewById(R.id.gcm_status);
        mGcmRegid = (TextView) findViewById(R.id.gcm_regid);
        mBackendIP = (EditText) findViewById(R.id.ip_address);
        mBackendPort = (EditText) findViewById(R.id.port);

        if (gcm_regid != null && !gcm_regid.equals("")) {
            mGcmStatus.setText("Connected");
            mGcmRegid.setText(gcm_regid);
        } else {
            mGcmStatus.setText("Disconnected");
        }

        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String ip_address = sharedPref.getString("@string/ip_address", "");
        if (!ip_address.equals(""))
            mBackendIP.setText(ip_address);

        String port = sharedPref.getString("@string/port", "");
        if (!port.equals(""))
            mBackendPort.setText(port);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cloud_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save_cloud_settings) {
            saveCloudSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    private void saveCloudSettings() {
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("@string/ip_address", mBackendIP.getText().toString());
        editor.putString("@string/port", mBackendPort.getText().toString());
        editor.commit();

        Toast.makeText(getApplicationContext(), "Cloud Settings saved!", Toast.LENGTH_LONG).show();
    }
}
