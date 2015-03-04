package com.ndg.intel.concierge;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class HttpPoster extends AsyncTask<String, String, String> {

    private final static String TAG = "HttpPoster";
    private Context mContext;

    HttpPoster(Context aContext) {
        mContext = aContext;
    }

    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0]; // URL to call

        StringBuilder builder = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(urlString);
        HttpResponse response;
        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("name", params[1]));
            nameValuePairs.add(new BasicNameValuePair("id", params[2]));
            nameValuePairs.add(new BasicNameValuePair("title", params[3]));
            nameValuePairs.add(new BasicNameValuePair("specialty", params[4]));
            nameValuePairs.add(new BasicNameValuePair("languages", params[5]));
            nameValuePairs.add(new BasicNameValuePair("photo", params[6]));
            nameValuePairs.add(new BasicNameValuePair("gcm_regid", params[7]));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            // Execute HTTP Post Request
            response = httpclient.execute(httppost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                Log.i(TAG, "Your data: " + builder.toString()); //response data
            } else {
                Log.e(TAG, "Failed with error: " + statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }

        return builder.toString();
    }

    protected void onPostExecute(String result) {
        Log.i(TAG, "onPostExecute: " + result);
        Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
    }
}
