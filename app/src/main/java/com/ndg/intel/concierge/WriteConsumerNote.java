package com.ndg.intel.concierge;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class WriteConsumerNote extends ActionBarActivity {

    private EditText mNote;
    private String mBackendIP;
    private String mBackendPort;
    private String mConsumerNoteAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_consumer_note);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNote = (EditText) findViewById(R.id.edit_note);

        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String ip_address = sharedPref.getString("@string/ip_address", "");
        if (!ip_address.equals(""))
            mBackendIP = ip_address;

        String port = sharedPref.getString("@string/port", "");
        if (!port.equals(""))
            mBackendPort = port;

        String consumer_note_api = sharedPref.getString("@string/consumer_note_api", "");
        if (!consumer_note_api.equals(""))
            mConsumerNoteAPI = consumer_note_api;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_write_consumer_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.upload_note) {
            uploadNote();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void uploadNote() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm");

        String concierge_id = getIntent().getStringExtra("concierge_id");
        String consumer_id = getIntent().getStringExtra("consumer_id");
        String date = df.format(cal.getTime());
        String text = mNote.getText().toString();

        HttpPoster poster = new HttpPoster(getApplicationContext());
        poster.execute(
                mBackendIP + ":" + mBackendPort + mConsumerNoteAPI,
                "consumer_id", consumer_id,
                "concierge_id", concierge_id,
                "date", date,
                "text", text
        );

    }
}
