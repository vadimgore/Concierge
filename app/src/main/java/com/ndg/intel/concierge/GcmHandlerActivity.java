package com.ndg.intel.concierge;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class GcmHandlerActivity extends ActionBarActivity {

    final static String TAG = "GcmHandlerActivity";

    private static final String IFASHION_IP_ADDRESS = "http://52.10.19.66";
    private static final String IFASHION_PORT = "8080";
    private static final String IFASHION_GETNOTE_API = "/consumer_notes";

    static boolean mActivityStopped = false;

    private SharedPreferences mSharedPref;

    private LinearLayout mCustomerAnalyticsLayout;
    private TextView mProfileSharingStatus;

    private View mStyleRange;
    private ImageView mStyleTarget;
    private View mBudgetRange;
    private ImageView mBudgetTarget;

    private ImageView mProdRec;

    private ImageView mFavActivityFootball;
    private ImageView mFavActivityBasketball;
    private ImageView mFavActivityGolf;
    private ImageView mFavActivityFormula1;
    private ImageView mFavActivityDiving;
    private ImageView mFavDrinkTea;
    private ImageView mFavDrinkCoffee;
    private ImageView mFavDrinkChocolate;

    private ArrayList<Timepiece> mProducts;
    private int mStyleScore;
    private int mBudgetScore;
    private TextView mConsumerNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcm_handler);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        setProducts();

        mCustomerAnalyticsLayout = (LinearLayout) findViewById(R.id.customer_analytics_layout);
        mProfileSharingStatus = (TextView) findViewById(R.id.customer_profile_sharing_status);
        mStyleRange = findViewById(R.id.style_range);
        mStyleTarget = (ImageView) findViewById(R.id.style_target);
        mBudgetRange = findViewById(R.id.budget_range);
        mBudgetTarget = (ImageView) findViewById(R.id.budget_target);
        mProdRec = (ImageView) findViewById(R.id.prod_rec);
        mConsumerNotes = (TextView) findViewById(R.id.consumer_notes);

        mFavActivityFootball = (ImageView) findViewById(R.id.fav_activity_football);
        mFavActivityBasketball = (ImageView) findViewById(R.id.fav_activity_basketball);
        mFavActivityGolf = (ImageView) findViewById(R.id.fav_activity_golf);
        mFavActivityFormula1 = (ImageView) findViewById(R.id.fav_activity_formula1);
        mFavActivityDiving = (ImageView) findViewById(R.id.fav_activity_diving);

        mFavDrinkTea = (ImageView) findViewById(R.id.fav_drink_tea);
        mFavDrinkCoffee = (ImageView) findViewById(R.id.fav_drink_coffee);
        mFavDrinkChocolate = (ImageView) findViewById(R.id.fav_drink_chocolate);

        String style_score = getIntent().getExtras().getString("style_score");
        String budget_score = getIntent().getExtras().getString("budget_score");
        String fav_activities = getIntent().getExtras().getString("fav_activities");
        String fav_drinks = getIntent().getExtras().getString("fav_drinks");
        String prod_rec = getIntent().getExtras().getString("prod_rec");
        String access_time = getIntent().getExtras().getString("access_time");

        if (access_time != null) {
            mProfileSharingStatus.setText("Access to Style Analytics is allowed for " +
                    access_time + " minutes");
            mCustomerAnalyticsLayout.setVisibility(View.VISIBLE);

            mStyleScore = Integer.parseInt(style_score);
            mBudgetScore = Integer.parseInt(budget_score);

            if (fav_activities.contains("Football")) {
                mFavActivityFootball.setImageResource(R.drawable.football);
            }

            if (fav_activities.contains("Basketball")) {
                mFavActivityBasketball.setImageResource(R.drawable.basketball);
            }

            if (fav_activities.contains("Golf")) {
                mFavActivityGolf.setImageResource(R.drawable.golf);
            }

            if (fav_activities.contains("Formula")) {
                mFavActivityFormula1.setImageResource(R.drawable.formula1);
            }

            if (fav_activities.contains("Diving")) {
                mFavActivityDiving.setImageResource(R.drawable.diving);
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

            findMatchingProduct(prod_rec, fav_activities, mBudgetScore);

            postConsumerNotes();

            int access_time_millis = Integer.parseInt(access_time) * 60000;
            finishIn(this, access_time_millis);
        } else {
            mProfileSharingStatus.setText("Access to Style Analytics is not allowed");
            mCustomerAnalyticsLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        mActivityStopped = true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);

        final int MAX_STYLE_SCORE = 4;
        final int MAX_BUDGET_SCORE = 20;

        FrameLayout.LayoutParams styleParams =
                new FrameLayout.LayoutParams(mStyleTarget.getLayoutParams());
        styleParams.leftMargin =
                mStyleScore*(mBudgetRange.getWidth() - mBudgetTarget.getWidth())/MAX_BUDGET_SCORE;
        mStyleTarget.setLayoutParams(styleParams);
        mStyleTarget.setVisibility(View.VISIBLE);

        FrameLayout.LayoutParams budgetParams =
                new FrameLayout.LayoutParams(mBudgetTarget.getLayoutParams());
        budgetParams.leftMargin =
                mBudgetScore*(mBudgetRange.getWidth() - mBudgetTarget.getWidth())/MAX_STYLE_SCORE;
        mBudgetTarget.setLayoutParams(budgetParams);
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
        if (id == R.id.add_note) {
            Intent writeConsumerNote = new Intent(this, WriteConsumerNote.class);
            writeConsumerNote.putExtra("consumer_id", getIntent().getStringExtra("consumer_id"));
            writeConsumerNote.putExtra("concierge_id", mSharedPref.getString("id", ""));
            startActivity(writeConsumerNote);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void finishIn(final Context context, int time) {
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                if (mActivityStopped) return;
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

    private void setProducts() {
        mProducts = new ArrayList<>();

        // Carrera calibre 36 flyback chronograph with leather strap (Football)
        mProducts.add(new Timepiece(Timepiece.Collection.Football, Timepiece.Type.CHRONOGRAPH,
                Timepiece.Shape.ROUND, Timepiece.Strap.Leather, Timepiece.PriceRange.MEDIUM,
                R.drawable.carrera_calibre_36_flyback_chronograph_leather));

        // Carrera calibre 36 flyback chronograph with steel bracelet (Football)
        mProducts.add(new Timepiece(Timepiece.Collection.Football, Timepiece.Type.CHRONOGRAPH,
                Timepiece.Shape.ROUND, Timepiece.Strap.Steel, Timepiece.PriceRange.MEDIUM,
                R.drawable.carrera_calibre_36_flyback_chronograph_steel));

        // Aquaracer quartz with steel bracelet
        mProducts.add(new Timepiece(Timepiece.Collection.Diving, Timepiece.Type.ANALOGWATCH,
                Timepiece.Shape.ROUND, Timepiece.Strap.Steel, Timepiece.PriceRange.LOW,
                R.drawable.aquaracer_quartz_steel));

        // Formula 1 chronograph leather strap
        mProducts.add(new Timepiece(Timepiece.Collection.Formula_1, Timepiece.Type.CHRONOGRAPH,
                Timepiece.Shape.ROUND, Timepiece.Strap.Leather, Timepiece.PriceRange.MEDIUM,
                R.drawable.formula1_chronograph_leather));

        // Carrera heritage leather strap
        mProducts.add(new Timepiece(Timepiece.Collection.None, Timepiece.Type.ANALOGWATCH,
                Timepiece.Shape.ROUND, Timepiece.Strap.Leather, Timepiece.PriceRange.MEDIUM,
                R.drawable.carrera_heritage_leather));

        // Monaco chronograph square shape leather strap
        mProducts.add(new Timepiece(Timepiece.Collection.None, Timepiece.Type.CHRONOGRAPH,
                Timepiece.Shape.SQUARE, Timepiece.Strap.Leather, Timepiece.PriceRange.HIGH,
                R.drawable.monaco_chronograph_leather));

        // Monaco chronograph square shape steel bracelet
        mProducts.add(new Timepiece(Timepiece.Collection.None, Timepiece.Type.CHRONOGRAPH,
                Timepiece.Shape.SQUARE, Timepiece.Strap.Steel, Timepiece.PriceRange.HIGH,
                R.drawable.monaco_chronograph_steel));

        // Monaco ana;og square shape leather strap
        mProducts.add(new Timepiece(Timepiece.Collection.None, Timepiece.Type.ANALOGWATCH,
                Timepiece.Shape.SQUARE, Timepiece.Strap.Leather, Timepiece.PriceRange.MEDIUM,
                R.drawable.monaco_analog_leather));

    }

    private void findMatchingProduct(String prodRec, String favActivities, int budgetScore) {

        // This is ugly, but whatever....

        ArrayList<String> collection = new ArrayList<>();
        if (!favActivities.equals(""))
            collection.addAll(Arrays.asList(favActivities.split("\\s*,\\s*")));
        else
            collection.add("None");

        Timepiece.PriceRange price;
        Timepiece.Shape shape;
        Timepiece.Type type;
        Timepiece.Strap strap;

        if (budgetScore == Timepiece.PriceRange.LOW.ordinal())
            price = Timepiece.PriceRange.LOW;
        else if (budgetScore < Timepiece.PriceRange.HIGH.ordinal())
            price = Timepiece.PriceRange.MEDIUM;
        else
            price = Timepiece.PriceRange.HIGH;

        if (prodRec.contains("ROUND"))
            shape = Timepiece.Shape.ROUND;
        else
            shape = Timepiece.Shape.SQUARE;

        if (prodRec.contains("CHRONOGRAPH"))
            type = Timepiece.Type.CHRONOGRAPH;
        else
            type = Timepiece.Type.ANALOGWATCH;

        if (prodRec.contains("Leather"))
            strap = Timepiece.Strap.Leather;
        else
            strap = Timepiece.Strap.Steel;

        for (String s : collection) {
            Timepiece watch = new Timepiece(Timepiece.Collection.valueOf(s), type, shape, strap, price, 0);
            for (Timepiece t : mProducts) {
                if (t.match(watch)) {
                    Log.i(TAG, "found matching product with image id = " + t.getImageId());
                    mProdRec.setImageResource(t.getImageId());
                    return;
                }
            }
        }
    }

    private void postConsumerNotes() {

        String httpURL = IFASHION_IP_ADDRESS + ":" + IFASHION_PORT + IFASHION_GETNOTE_API;
        String consumer_id = getIntent().getStringExtra("consumer_id");
        String httpResponse = "";
        try {
            // Get full Concierge profile from iFashion
            httpResponse = new HttpGetter().execute(httpURL, "consumer_id=" + consumer_id).get();

        } catch (InterruptedException e) {
            // Handle exception
            Log.i(TAG, "InterruptedException:" + e.getMessage());
        } catch (ExecutionException e) {
            // Handle exception
            Log.i(TAG, "ExecutionException:" + e.getMessage());
        }

        if (!httpResponse.equals("")) {

            String notes = "";
            try {

                JSONObject jsonResponse = new JSONObject(httpResponse);
                JSONArray jsonNotes = new JSONArray(jsonResponse.get("notes").toString());

                int numNotes = jsonNotes.length();
                for (int i = 0; i < numNotes; i++) {
                    String date = jsonNotes.getJSONObject(i).getString("date");
                    String text = jsonNotes.getJSONObject(i).getString("text");
                    if (!date.equals("") && !text.equals(""))
                        notes += (date + ": " + text + "\n\n");
                }

            } catch (JSONException e) {
                Log.i(TAG, "JSONException:" + e.getMessage());
            }

            mConsumerNotes.setText(notes);
        }
    }
}
