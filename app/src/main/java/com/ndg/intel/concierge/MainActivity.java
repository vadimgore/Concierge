package com.ndg.intel.concierge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "Main Activity:";
    private Intent mConciergeProfile;
    private Intent mCloudSettings;

    private static final String PROPERTY_REG_ID = "gcm_registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String DEFAULT_BACKEND_IP_ADDRESS = "http://52.10.19.66";
    private static final String DEFAULT_BACKEND_PORT = "8080";
    private static final String DEFAULT_BACKEND_CONCIERGE_API = "/concierge";
    private static final String DEFAULT_BACKEND_PROFILE_API = "/profile";
    private static final String DEFAULT_BACKEND_CONSUMER_NOTE_API = "/consumer_note";

    // This is the project number you got from the Google API Console, for GCM services
    private final String SENDER_ID = "1032714926342";
    GoogleCloudMessaging mGcm;
    Boolean mGcmRegistered;
    String mGcmRegid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setBackendDefaults();

        // Check device for Play Services APK.
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            mGcm = GoogleCloudMessaging.getInstance(this);
            mGcmRegid = getRegistrationId(getApplicationContext());

            if (mGcmRegid.isEmpty()) {
                registerInBackground();
            }
            else {
                Toast.makeText(getApplicationContext(), "Device already registered",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
            Toast.makeText(getApplicationContext(), "Google Play Services no supported!",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_profile:
                mConciergeProfile = new Intent(this, ConciergeProfileActivity.class);
                mConciergeProfile.putExtra("@string/gcm_regid", mGcmRegid);
                startActivity(mConciergeProfile);
                return true;
            case R.id.action_cloud_settings:
                mCloudSettings = new Intent(this, CloudSettingsActivity.class);
                mCloudSettings.putExtra("@string/gcm_regid", mGcmRegid);
                startActivity(mCloudSettings);
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                Toast.makeText(getApplicationContext(), "Google Play Services are no supported on this device!",
                        Toast.LENGTH_LONG).show();

            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg;
                try {
                    if (mGcm == null) {
                        mGcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    mGcmRegid = mGcm.register(SENDER_ID);
                    msg = "New device has been registered successfully";

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(getApplicationContext(), mGcmRegid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, "onPostExecute: " + msg);
                Toast.makeText(getApplicationContext(), "onPostExecute:" + msg,
                        Toast.LENGTH_LONG).show();

            }
        }.execute(null, null, null);
    }
    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
        Log.i(TAG, "regid = " + mGcmRegid);
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private void setBackendDefaults() {
        Log.i(TAG, "Setting missing backend settings to their defaults");

        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor editor = sharedPref.edit();

        if (sharedPref.getString("@string/ip_address", "").equals(""))
            editor.putString("@string/ip_address", DEFAULT_BACKEND_IP_ADDRESS);

        if (sharedPref.getString("@string/port", "").equals(""))
            editor.putString("@string/port", DEFAULT_BACKEND_PORT);

        if (sharedPref.getString("@string/concierge_api", "").equals(""))
            editor.putString("@string/concierge_api", DEFAULT_BACKEND_CONCIERGE_API);

        if (sharedPref.getString("@string/profile_api", "").equals(""))
            editor.putString("@string/profile_api", DEFAULT_BACKEND_PROFILE_API);

        if (sharedPref.getString("@string/consumer_note_api", "").equals(""))
            editor.putString("@string/consumer_note_api", DEFAULT_BACKEND_CONSUMER_NOTE_API);

        editor.commit();

    }
}
