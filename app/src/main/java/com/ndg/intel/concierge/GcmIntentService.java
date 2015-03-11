package com.ndg.intel.concierge;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
    private final static String TAG = "GCM_INTENT_SERVICE";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.i(TAG, "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.i(TAG, "Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, send notification
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.i(TAG, "Received: " + extras.toString());
                // Post notification of received message.
                sendNotification(extras);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Bundle bundle) {
        //Toast.makeText(getApplicationContext(), "onPostExecute: " + msg, Toast.LENGTH_LONG).show();

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent mIntent = new Intent(this, GcmHandlerActivity.class);
        //mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //mIntent.setAction("Concierge.showCustomerProfile");

        // EXTRACT Customer Profile from Bundle
        String style_score = bundle.getString("style_score");
        String budget_score = bundle.getString("budget_score");
        String fav_activities =  bundle.getString("fav_activities");
        String fav_drinks = bundle.getString("fav_drinks");
        String prod_rec = bundle.getString("prod_rec");
        String access_time = bundle.getString("access_time");

        mIntent.putExtra("style_score", style_score);
        mIntent.putExtra("budget_score", budget_score);
        mIntent.putExtra("fav_activities", fav_activities);
        mIntent.putExtra("fav_drinks", fav_drinks);
        mIntent.putExtra("prod_rec", prod_rec);
        mIntent.putExtra("access_time", access_time);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, mIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.gcm_notification)
                        .setContentTitle("VIP Customer Alert")
                        .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setLights(Color.BLUE, 500, 500);
        long[] vibPattern = {500, 500, 500, 500, 500};
        mBuilder.setVibrate(vibPattern);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(sound);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}