package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(LOG_TAG,new Object(){}.getClass().getEnclosingMethod().getName());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(LOG_TAG,new Object(){}.getClass().getEnclosingMethod().getName());
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //no inspection SimplifiableIfStatement

        // TODO: why not this code in Fragment of this activity, sim in DetailActivity.java
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_show_map_location) {
            createMapView();
        }

        return super.onOptionsItemSelected(item);
    }

    private void createMapView() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String locationPin = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

        // Create a Uri from an intent string. Use the result to create an Intent.
        Uri gmmIntentUri = Uri.parse("geo:0,0?").buildUpon().
                appendQueryParameter("q", locationPin).build();

        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Log.e(LOG_TAG, "couldn't find map app, can't show location: " + locationPin);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e(LOG_TAG,new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @Override
    protected void onDestroy() {
        Log.e(LOG_TAG,new Object(){}.getClass().getEnclosingMethod().getName());
        super.onDestroy();
    }

    @Override
    protected void onPostResume() {
        Log.e(LOG_TAG,new Object(){}.getClass().getEnclosingMethod().getName());
        super.onPostResume();
    }

    @Override
    protected void onStop() {
        Log.e(LOG_TAG,new Object(){}.getClass().getEnclosingMethod().getName());
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.e(LOG_TAG,new Object(){}.getClass().getEnclosingMethod().getName());
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.e(LOG_TAG,new Object(){}.getClass().getEnclosingMethod().getName());
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.e(LOG_TAG,new Object(){}.getClass().getEnclosingMethod().getName());
        super.onStart();
    }
}
