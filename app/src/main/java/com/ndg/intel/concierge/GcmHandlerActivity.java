package com.ndg.intel.concierge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class GcmHandlerActivity extends ActionBarActivity {

    TextView mStyleScore = null;
    TextView mBudgetScore = null;
    TextView mFavSports = null;
    TextView mFavDrinks = null;
    TextView mProdRec = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcm_handler);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStyleScore = (TextView) findViewById(R.id.style_score);
        mBudgetScore = (TextView) findViewById(R.id.budget_score);
        mFavSports = (TextView) findViewById(R.id.fav_sports);
        mFavDrinks = (TextView) findViewById(R.id.fav_drinks);
        mProdRec = (TextView) findViewById(R.id.prod_rec);

        String style_score = getIntent().getExtras().getString("style_score");
        String budget_score = getIntent().getExtras().getString("budget_score");
        String fav_sports = getIntent().getExtras().getString("fav_sports");
        String fav_drinks = getIntent().getExtras().getString("fav_drinks");
        String prod_rec = getIntent().getExtras().getString("prod_rec");

        mStyleScore.setText(style_score);
        mBudgetScore.setText(budget_score);
        mFavSports.setText(fav_sports);
        mFavDrinks.setText(fav_drinks);
        mProdRec.setText(prod_rec);
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
