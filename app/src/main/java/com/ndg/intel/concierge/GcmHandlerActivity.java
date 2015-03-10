package com.ndg.intel.concierge;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GcmHandlerActivity extends ActionBarActivity {

    private LinearLayout mCustomerAnalyticsLayout;
    private TextView mProfileSharingStatus;
    private TextView mStyleScore;

    private View mBudgetRange;
    private ImageView mBudgetTarget;

    private TextView mProdRec;

    private ImageView mFavSportFootball;
    private ImageView mFavSportBasketball;
    private ImageView mFavSportGolf;
    private ImageView mFavSportFormula1;
    private ImageView mFavSportWatersports;
    private ImageView mFavDrinkTea;
    private ImageView mFavDrinkCoffee;
    private ImageView mFavDrinkChocolate;

    private int mBudgetScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcm_handler);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCustomerAnalyticsLayout = (LinearLayout) findViewById(R.id.customer_analytics_layout);
        mProfileSharingStatus = (TextView) findViewById(R.id.customer_profile_sharing_status);
        mStyleScore = (TextView) findViewById(R.id.style_score);
        mBudgetRange = (View) findViewById(R.id.budget_range);
        mBudgetTarget = (ImageView) findViewById(R.id.budget_target);
        mProdRec = (TextView) findViewById(R.id.prod_rec);

        ImageView mFavSportFootball = (ImageView) findViewById(R.id.fav_sport_football);
        ImageView mFavSportBasketball = (ImageView) findViewById(R.id.fav_sport_basketball);
        ImageView mFavSportGolf = (ImageView) findViewById(R.id.fav_sport_golf);
        ImageView mFavSportFormula1 = (ImageView) findViewById(R.id.fav_sport_formula1);
        ImageView mFavSportWatersports = (ImageView) findViewById(R.id.fav_sport_watersports);

        ImageView mFavDrinkTea = (ImageView) findViewById(R.id.fav_drink_tea);
        ImageView mFavDrinkCoffee = (ImageView) findViewById(R.id.fav_drink_coffee);
        ImageView mFavDrinkChocolate = (ImageView) findViewById(R.id.fav_drink_chocolate);

        String style_score = getIntent().getExtras().getString("style_score");
        String budget_score = getIntent().getExtras().getString("budget_score");
        String fav_sports = getIntent().getExtras().getString("fav_sports");
        String fav_drinks = getIntent().getExtras().getString("fav_drinks");
        String prod_rec = getIntent().getExtras().getString("prod_rec");
        String access_time = getIntent().getExtras().getString("access_time");

        if (access_time != null) {
            mProfileSharingStatus.setText("Access to Style Analytics is allowed for " +
                    access_time + " minutes");
            mCustomerAnalyticsLayout.setVisibility(View.VISIBLE);
            mStyleScore.setText(style_score);
            mBudgetScore = Integer.parseInt(budget_score);

            if (fav_sports.contains("Football")) {
                mFavSportFootball.setImageResource(R.drawable.football);
            }

            if (fav_sports.contains("Basketball")) {
                mFavSportBasketball.setImageResource(R.drawable.basketball);
            }

            if (fav_sports.contains("Golf")) {
                mFavSportGolf.setImageResource(R.drawable.golf);
            }

            if (fav_sports.contains("Formula")) {
                mFavSportFormula1.setImageResource(R.drawable.formula1);
            }

            if (fav_sports.contains("Watersports")) {
                mFavSportWatersports.setImageResource(R.drawable.watersports);
            }

            if (fav_drinks.contains("Tea")) {
                mFavDrinkTea.setImageResource(R.drawable.tea);
            }

            if (fav_drinks.contains("Coffee")) {
                mFavDrinkCoffee.setImageResource(R.drawable.coffee);
            }

            if (fav_drinks.contains("Chocolate")) {
                mFavDrinkChocolate.setImageResource(R.drawable.chocolate);
            }

            mProdRec.setText(prod_rec);
            int access_time_millis = Integer.parseInt(access_time) * 60000;
            finishIn(this, access_time_millis);
        } else {
            mProfileSharingStatus.setText("Access to Style Analytics is not allowed");
            mCustomerAnalyticsLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);

        //mBudgetTarget.setLeft();
        //mBudgetTarget.setPaddingRelative(50, 0, 0, 0);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mBudgetTarget.getLayoutParams());
        params.leftMargin = mBudgetScore*(mBudgetRange.getWidth() - mBudgetTarget.getWidth())/4;
        mBudgetTarget.setLayoutParams(params);
        mBudgetTarget.setVisibility(View.VISIBLE);
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

    private void finishIn(final Context context, int time) {
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("CUSTOMER PROFILE ALERT")
                        .setMessage("Access expired")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .show();
            }
        };

        handler.postDelayed(r, time);
    }
}
