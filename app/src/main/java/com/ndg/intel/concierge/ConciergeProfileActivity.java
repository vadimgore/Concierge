package com.ndg.intel.concierge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class ConciergeProfileActivity extends ActionBarActivity {

    private static final String TAG = "ConciergeProfile:";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_LOAD = 2;

    enum Language {
        English,
        French,
        Spanish,
        German
    }

    private ImageView mPhoto;
    private EditText mName;
    private EditText mID;
    private EditText mTitle;
    private EditText mSpecialty;

    private CheckBox mEnglish;
    private CheckBox mFrench;
    private CheckBox mSpanish;
    private CheckBox mGerman;

    private SharedPreferences mSharedPref;
    private ArrayList<String> mLanguages;

    private String mGcmRegid;
    private String miFashionIP;
    private String miFashionPort;
    private String miFashionAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concierge_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGcmRegid = getIntent().getExtras().getString("@string/gcm_regid");
        miFashionIP = getIntent().getExtras().getString("@string/ip_address");
        miFashionPort = getIntent().getExtras().getString("@string/port");
        miFashionAPI = getIntent().getExtras().getString("@string/api");

        mName = (EditText) findViewById(R.id.name);
        mID = (EditText) findViewById(R.id.id);
        mTitle = (EditText) findViewById(R.id.title);
        mSpecialty = (EditText) findViewById(R.id.specialty);

        mEnglish = (CheckBox) findViewById(R.id.checkbox_english);
        mFrench = (CheckBox) findViewById(R.id.checkbox_french);
        mSpanish = (CheckBox) findViewById(R.id.checkbox_spanish);
        mGerman = (CheckBox) findViewById(R.id.checkbox_german);
        mPhoto = (ImageView) findViewById(R.id.concierge_photo);
        mLanguages = new ArrayList<>();

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        restoreState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_concierge_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save_profile) {
            saveState();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.upload_profile) {
            sendToBackend();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user touches the button */
    public void onTakeImage(View view) {
        // Do something in response to button click
        Log.i(TAG, "onTakeImage called");

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            double scaleFactor = 256.0 / imageBitmap.getHeight();
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, (int) (imageBitmap.getWidth()*scaleFactor),
                    (int) (imageBitmap.getHeight()*scaleFactor), false);
            mPhoto.setImageBitmap(scaledBitmap);
        }
        else if (requestCode == REQUEST_IMAGE_LOAD && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            String selectedImagePath = getPath(selectedImageUri);
            Bitmap imageBitmap;
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            imageBitmap = BitmapFactory.decodeFile(selectedImagePath, bitmapOptions);
            //imageBitmap = new ExifUtil().rotateBitmap(selectedImageUri.toString(), imageBitmap);
            double scaleFactor = 256.0 / imageBitmap.getHeight();
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, (int) (imageBitmap.getWidth()*scaleFactor),
                    (int) (imageBitmap.getHeight()*scaleFactor), false);
            mPhoto.setImageBitmap(scaledBitmap);
        }
    }

    /** Called when the user touches the button */
    public void onLoadImage(View view) {
        // Do something in response to button click
        Log.i(TAG, "onLoadImage called");

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_LOAD);
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void saveState() {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.clear();
        editor.putString("name", mName.getText().toString());
        editor.putString("id", mID.getText().toString());
        editor.putString("title", mTitle.getText().toString());
        editor.putString("specialty", mSpecialty.getText().toString());

        // Languages
        for (String s : mLanguages) {
            editor.putString(s, s);
        }

        // Encode bitmap into a byte array
        Bitmap photo = ((BitmapDrawable)mPhoto.getDrawable()).getBitmap();
        String encodedImage = encodeBitmap(photo);
        editor.putString("image", encodedImage);

        editor.commit();

        Toast.makeText(getApplicationContext(), "Profile saved!", Toast.LENGTH_LONG).show();
    }

    private String encodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encodedBitmap = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedBitmap;
    }

    private Bitmap decodeBitmap(String encodedBitmap) {
        byte[] b = Base64.decode(encodedBitmap, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        return decodedBitmap;
    }

    public void restoreState() {

        String name = mSharedPref.getString("name", "");
        if( !name.equalsIgnoreCase("") ){
            mName.setText(name);
        }

        String id = mSharedPref.getString("id", "");
        if( !id.equalsIgnoreCase("") ){
            mID.setText(id);
        }

        String title = mSharedPref.getString("title", "");
        if( !title.equalsIgnoreCase("") ){
            mTitle.setText(title);
        }

        String specialty = mSharedPref.getString("specialty", "");
        if( !specialty.equalsIgnoreCase("") ){
            mSpecialty.setText(specialty);
        }

        for (Language l : Language.values()) {
            String s = mSharedPref.getString(l.name(), "");
            if (!s.equals("")) {
                mLanguages.add(s);

                switch (s) {
                    case "English":
                        mEnglish.setChecked(true);
                        break;
                    case "French":
                        mFrench.setChecked(true);
                        break;
                    case "Spanish":
                        mSpanish.setChecked(true);
                        break;
                    case "German":
                        mGerman.setChecked(true);
                        break;
                    default:
                        Log.i(TAG, "Restoring unsupported language: " + s);
                }

            }
        }

        String encodedImage = mSharedPref.getString("image", "");
        if( !encodedImage.equalsIgnoreCase("") ){
            Bitmap decodedImage = decodeBitmap(encodedImage);
            mPhoto.setImageBitmap(decodedImage);
        }
    }



    public void sendToBackend() {
        StringBuilder languages = new StringBuilder();
        int i = 0;
        int size = mLanguages.size();
        for (String l : mLanguages) {
            languages.append(l);
            if (i++ < size-1)
                languages.append(',');
        }

        HttpPoster poster = new HttpPoster(getApplicationContext());
        poster.execute(
                miFashionIP + ":" + miFashionPort + miFashionAPI,
                "name", mName.getText().toString(),
                "id", mID.getText().toString(),
                "title", mTitle.getText().toString(),
                "specialty", mSpecialty.getText().toString(),
                "languages", languages.toString(),
                "photo", encodeBitmap( ((BitmapDrawable)mPhoto.getDrawable()).getBitmap()),
                "gcm_regid", mGcmRegid
        );
    }

    public void onLanguagesCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        if (checked) {
            mLanguages.add(((CheckBox) view).getText().toString());
        } else {
            mLanguages.remove(((CheckBox) view).getText().toString());
        }
    }
}
