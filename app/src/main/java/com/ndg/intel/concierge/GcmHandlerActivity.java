package com.ndg.intel.concierge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

public class GcmHandlerActivity extends ActionBarActivity {
/*
    TextView mTitle = null;
    TextView mCustomerName = null;
    TextView mCustomerProfile = null;
    TextView mCustomerStyle = null;
    TextView mRecentPurchases = null;
    TextView mItem1Name = null;
    TextView mItem1Brand = null;
    TextView mItem1Price = null;
    TextView mItem1Date = null;
    TextView mItem2Name = null;
    TextView mItem2Brand = null;
    TextView mItem2Price = null;
    TextView mItem2Date = null;
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcm_handler);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toast.makeText(getApplicationContext(), "GcmHandlerActivity called!", Toast.LENGTH_LONG).show();

/*
        mTitle = (TextView) findViewById(R.id.title);
        mCustomerName = (TextView) findViewById(R.id.customer_name);
        mCustomerProfile = (TextView) findViewById(R.id.customer_profile);
        mCustomerStyle = (TextView) findViewById(R.id.customer_style);
        mRecentPurchases = (TextView) findViewById(R.id.recent_purchases);

        mItem1Name = (TextView) findViewById(R.id.item1_name);
        mItem1Brand = (TextView) findViewById(R.id.item1_brand);
        mItem1Price = (TextView) findViewById(R.id.item1_price);
        mItem1Date = (TextView) findViewById(R.id.item1_date);

        mItem2Name = (TextView) findViewById(R.id.item2_name);
        mItem2Brand = (TextView) findViewById(R.id.item2_brand);
        mItem2Price = (TextView) findViewById(R.id.item2_price);
        mItem2Date = (TextView) findViewById(R.id.item2_date);

        String name = getIntent().getExtras().getString("name");
        String profile = getIntent().getExtras().getString("profile");
        String style = getIntent().getExtras().getString("style");
        String purchases = getIntent().getExtras().getString("purchases");

        mTitle.setText("VIP CUSTOMER ALERT");
        mCustomerName.setText(name + " is approaching our store!");
        mCustomerProfile.setText("Profile: " + profile);
        mCustomerStyle.setText("Style: " + style);
        mRecentPurchases.setText("Recent purchases:");

        try {
            //JSONObject jsonPurchases = new JSONObject(purchases);
            JSONArray jsonArray = new JSONArray(purchases);

            mItem1Name.setText("Item: " + jsonArray.getJSONObject(0).getString("item"));
            mItem1Brand.setText("Brand: " + jsonArray.getJSONObject(0).getString("brand"));
            mItem1Price.setText("Price: $" + jsonArray.getJSONObject(0).getString("price"));
            mItem1Date.setText("Purchase date: " + jsonArray.getJSONObject(0).getString("date"));

            mItem2Name.setText("Item: " + jsonArray.getJSONObject(1).getString("item"));
            mItem2Brand.setText("Brand: " + jsonArray.getJSONObject(1).getString("brand"));
            mItem2Price.setText("Price: $" + jsonArray.getJSONObject(1).getString("price"));
            mItem2Date.setText("Purchase date: " + jsonArray.getJSONObject(1).getString("date"));

        } catch (JSONException e) {
            Log.i("JSON parsing error: ", e.toString());
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
*/
    }

    @Override
    protected void onNewIntent (Intent intent) {
        String mMsg = intent.getExtras().getString("profile");
        Toast.makeText(getApplicationContext(), "onNewIntent called!", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gcm_handler, menu);
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
