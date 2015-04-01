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
import java.util.concurrent.ExecutionException;

public class GcmHandlerActivity extends ActionBarActivity {

    final static String TAG = "GcmHandlerActivity";

    private static final String IFASHION_IP_ADDRESS = "http://52.10.19.66";
    private static final String IFASHION_PORT = "8080";
    private static final String IFASHION_GETNOTE_API = "/consumer_notes";

    boolean mActivityStopped = false;

    private SharedPreferences mSharedPref;

    private LinearLayout mCustomerAnalyticsLayout;
    private TextView mProfileSharingStatus;

    private ImageView mStyleTarget;

    private ImageView mGender;
    private ImageView mLanguage;
    private ImageView mBudget;
    private ImageView mProdRec;

    private View mStyleRange;
    private ImageView mFavDrinkBlackTea;
    private ImageView mFavDrinkGreenTea;
    private ImageView mFavDrinkEspresso;

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

        mGender = (ImageView) findViewById(R.id.gender);
        mLanguage = (ImageView) findViewById(R.id.language);
        mBudget = (ImageView) findViewById(R.id.budget);
        mStyleRange  = (View) findViewById(R.id.style_range);
        mStyleTarget = (ImageView) findViewById(R.id.style_target);
        mProdRec = (ImageView) findViewById(R.id.prod_rec);
        mConsumerNotes = (TextView) findViewById(R.id.consumer_notes);

        mFavDrinkBlackTea = (ImageView) findViewById(R.id.fav_drink_black_tea);
        mFavDrinkGreenTea = (ImageView) findViewById(R.id.fav_drink_green_tea);
        mFavDrinkEspresso = (ImageView) findViewById(R.id.fav_drink_espresso);

        String access_time = getIntent().getExtras().getString("access_time");
        if (access_time != null) {

            String gender = getIntent().getExtras().getString("gender");
            String language = getIntent().getExtras().getString("language");
            Integer age_group = Integer.parseInt(getIntent().getExtras().getString("age_group"));
            String style_score = getIntent().getExtras().getString("style_score");
            String budget_score = getIntent().getExtras().getString("budget_score");
            String fav_drinks = getIntent().getExtras().getString("fav_drinks");
            String prod_rec = getIntent().getExtras().getString("prod_rec");

            mProfileSharingStatus.setText("Access to Style Analytics is allowed for " +
                    access_time + " minutes");
            mCustomerAnalyticsLayout.setVisibility(View.VISIBLE);

            // Set consumer's demographics
            int male_images[] = {R.drawable.young_male, R.drawable.middleage_male, R.drawable.senior_male};
            int female_images[] = {R.drawable.young_female, R.drawable.middleage_female, R.drawable.senior_female};
            if (gender.equals("male")) {
                mGender.setImageResource(male_images[age_group]);
            } else if (gender.equals("female")) {
                mGender.setImageResource(female_images[age_group]);
            }

            mBudgetScore = Integer.parseInt(budget_score);
            if (mBudgetScore < 1)
                mBudget.setImageResource(R.drawable.small_budget);
            else if (mBudgetScore < 3)
                mBudget.setImageResource(R.drawable.medium_budget);
            else
                mBudget.setImageResource(R.drawable.large_budget);

            int langResId = getResources().getIdentifier(language.toLowerCase(), "drawable", getPackageName());
            mLanguage.setImageResource(langResId);

            mStyleScore = Integer.parseInt(style_score);


            if (fav_drinks.contains("tea")) {
                mFavDrinkBlackTea.setImageResource(R.drawable.black_tea);
            }

            if (fav_drinks.contains("chocolate")) {
                mFavDrinkGreenTea.setImageResource(R.drawable.chocolate);
            }

            if (fav_drinks.contains("coffee")) {
                mFavDrinkEspresso.setImageResource(R.drawable.espresso);
            }

            findMatchingProduct(prod_rec, gender, mBudgetScore);

            postConsumerNotes();

            int access_time_millis = Integer.parseInt(access_time) * 60000;
            finishIn(this, access_time_millis);
        } else {
            mProfileSharingStatus.setText("Access to Style Analytics is not allowed");
            mCustomerAnalyticsLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mActivityStopped = false;
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

        final int MAX_STYLE_SCORE = 16;

        FrameLayout.LayoutParams styleParams =
                new FrameLayout.LayoutParams(mStyleTarget.getLayoutParams());
        styleParams.leftMargin =
                mStyleScore*(mStyleRange.getWidth() - mStyleTarget.getWidth())/MAX_STYLE_SCORE;
        mStyleTarget.setLayoutParams(styleParams);
        mStyleTarget.setVisibility(View.VISIBLE);
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

        // Men watches

        // Carrera chronograph with leather strap
        mProducts.add(new Timepiece(Timepiece.Collection.CARRERA, Timepiece.Gender.MALE,
                Timepiece.Type.CHRONOGRAPH, Timepiece.Shape.ROUND, Timepiece.Strap.LEATHER,
                Timepiece.PriceRange.MEDIUM, R.drawable.men_carrera_chronograph_leather));

        // Carrera calibre 36 flyback chronograph with steel bracelet
        mProducts.add(new Timepiece(Timepiece.Collection.CARRERA, Timepiece.Gender.MALE,
                Timepiece.Type.CHRONOGRAPH, Timepiece.Shape.ROUND, Timepiece.Strap.STEEL,
                Timepiece.PriceRange.MEDIUM, R.drawable.men_carrera_chronograph_steel));

        // Aquaracer quartz with steel bracelet
        mProducts.add(new Timepiece(Timepiece.Collection.AQUARACER, Timepiece.Gender.MALE,
                Timepiece.Type.ANALOGWATCH, Timepiece.Shape.ROUND, Timepiece.Strap.STEEL,
                Timepiece.PriceRange.LOW, R.drawable.men_aquaracer_quartz_steel));

        // Formula 1 chronograph leather strap
        mProducts.add(new Timepiece(Timepiece.Collection.FORMULA1, Timepiece.Gender.MALE,
                Timepiece.Type.CHRONOGRAPH, Timepiece.Shape.ROUND, Timepiece.Strap.LEATHER,
                Timepiece.PriceRange.MEDIUM, R.drawable.men_formula1_chronograph_leather));

        // Carrera heritage leather strap
        mProducts.add(new Timepiece(Timepiece.Collection.NONE, Timepiece.Gender.MALE,
                Timepiece.Type.ANALOGWATCH, Timepiece.Shape.ROUND, Timepiece.Strap.LEATHER,
                Timepiece.PriceRange.MEDIUM, R.drawable.carrera_heritage_leather));

        // Monaco chronograph square shape leather strap
        mProducts.add(new Timepiece(Timepiece.Collection.MONACO, Timepiece.Gender.MALE,
                Timepiece.Type.CHRONOGRAPH, Timepiece.Shape.SQUARE, Timepiece.Strap.LEATHER,
                Timepiece.PriceRange.HIGH, R.drawable.monaco_chronograph_leather));

        // Monaco chronograph square shape steel bracelet
        mProducts.add(new Timepiece(Timepiece.Collection.MONACO, Timepiece.Gender.MALE,
                Timepiece.Type.CHRONOGRAPH, Timepiece.Shape.SQUARE, Timepiece.Strap.STEEL,
                Timepiece.PriceRange.HIGH, R.drawable.men_monaco_chronograph_steel));

        // Monaco ana;og square shape leather strap
        mProducts.add(new Timepiece(Timepiece.Collection.NONE, Timepiece.Gender.MALE,
                Timepiece.Type.ANALOGWATCH, Timepiece.Shape.SQUARE, Timepiece.Strap.LEATHER,
                Timepiece.PriceRange.MEDIUM, R.drawable.men_monaco_analog_leather));

        // Women watches

        // Aquaracer quartz with steel bracelet
        mProducts.add(new Timepiece(Timepiece.Collection.AQUARACER, Timepiece.Gender.FEMALE,
                Timepiece.Type.CHRONOGRAPH, Timepiece.Shape.ROUND, Timepiece.Strap.STEEL,
                Timepiece.PriceRange.HIGH, R.drawable.women_aquaracer_chronograph_steel));

        // Carrera quartz with steel bracelet
        mProducts.add(new Timepiece(Timepiece.Collection.CARRERA, Timepiece.Gender.FEMALE,
                Timepiece.Type.ANALOGWATCH, Timepiece.Shape.ROUND, Timepiece.Strap.STEEL,
                Timepiece.PriceRange.LOW, R.drawable.women_carrera_quartz_steel));

        // Formula1 quartz with steel bracelet
        mProducts.add(new Timepiece(Timepiece.Collection.FORMULA1, Timepiece.Gender.FEMALE,
                Timepiece.Type.ANALOGWATCH, Timepiece.Shape.ROUND, Timepiece.Strap.STEEL,
                Timepiece.PriceRange.MEDIUM, R.drawable.women_formula1_quartz_steel));

        // Monaco quartz with leather strap
        mProducts.add(new Timepiece(Timepiece.Collection.MONACO, Timepiece.Gender.FEMALE,
                Timepiece.Type.ANALOGWATCH, Timepiece.Shape.SQUARE, Timepiece.Strap.LEATHER,
                Timepiece.PriceRange.MEDIUM, R.drawable.women_monaco_quartz_leather));

    }

    private void findMatchingProduct(String prodRec, String aGender, int budgetScore) {

        // Build desired Timepiece based on the consumer profile
        try {
            JSONObject jProdRec = new JSONObject(prodRec);
            Timepiece.Collection collection = Timepiece.Collection.valueOf(jProdRec.getString("collection").toUpperCase());
            Timepiece.Shape shape = Timepiece.Shape.valueOf(jProdRec.getString("shape").toUpperCase());
            Timepiece.Type type = Timepiece.Type.valueOf(jProdRec.getString("type").toUpperCase());
            Timepiece.Strap strap = Timepiece.Strap.valueOf(jProdRec.getString("strap").toUpperCase());
            Timepiece.PriceRange price = Timepiece.PriceRange.values()[budgetScore];
            Timepiece.Gender gender = Timepiece.Gender.valueOf(aGender.toUpperCase());

            Timepiece product = new Timepiece(collection, gender, type, shape, strap, price, 0);

            for (Timepiece t : mProducts) {
                if (t.match(product)) {
                    Log.i(TAG, "found matching product with image id = " + t.getImageId());
                    mProdRec.setImageResource(t.getImageId());
                    return;
                }
            }

        } catch (JSONException e) {
            Log.i(TAG, "JSONException: " + e.getMessage());
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
