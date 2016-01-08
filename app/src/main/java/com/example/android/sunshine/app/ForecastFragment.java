package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.sunshine.utils.Constants;
import com.example.android.sunshine.utils.WeatherDataParser;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();
    private ArrayAdapter<String> mForecastAdapter;


    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String pinCode = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
        fetchWeatherTask.execute(pinCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //create dummy data
        String[] weekWeather = {
                "sun-19 dec- sunny",
                "mon-20 dec- clear day",
                "tue-21 dec- bright",
                "wed-22 dec-foggy",
                "thu-23 dec- cold",
                "fri-24 dec partially cloudy",
                "sat-25 dec- pleasant"
        };

        List<String> weekWeatherList = new ArrayList<String>(Arrays.asList(weekWeather));

        // create array adapter which will create elements for the listView, don't use dummy data
        mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, new ArrayList<String>());

        // find the listView element
        ListView listView = (ListView) rootView.findViewById(R.id.listView_forecast);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedRow = mForecastAdapter.getItem(position);
                Toast.makeText(getActivity(), "clicked: " + clickedRow, Toast.LENGTH_SHORT).show();
                //use intent to launch activity
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, clickedRow);
                startActivity(intent);
            }
        });

        return rootView;
    }

//    public static void main(String[] args) throws Exception {
//        ForecastFragment f = new ForecastFragment();
//        f.getLiveWeatherData();
//    }

    private String[] getLiveWeatherData(String pinCode, String mode, int numDays, String units, String appId) {
        Log.d(LOG_TAG, "in geLiveWeatherData fn()");

        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        String forecastJsonStr = null;
        try {

            Uri builtUri = Uri.parse(Constants.BASE_URL).buildUpon().
                    appendQueryParameter(Constants.PIN_CODE_TAG, pinCode).
                    appendQueryParameter(Constants.MODE_TAG, mode).
                    appendQueryParameter(Constants.COUNT_TAG, Integer.toString(numDays)).
                    appendQueryParameter(Constants.UNITS_TAG, units).
                    appendQueryParameter(Constants.APP_ID_TAG, appId).build();

            Log.v(LOG_TAG, "uri built: " + builtUri);

            URL url = new URL(builtUri.toString());

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            Log.d(LOG_TAG, "connected to the api");

            InputStream inputStream = connection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            forecastJsonStr = buffer.toString();
            //System.out.println("resp= " + forecastJsonStr);
            Log.d(LOG_TAG, "response string= " + forecastJsonStr);
            // return forecastJsonStr;
        } catch (IOException e) {
            Log.e(LOG_TAG, "error", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {

                }
            }
        }
        if (forecastJsonStr == null || forecastJsonStr.isEmpty()) {
            Log.e(LOG_TAG, "no response received from the server side");
            return null;
        }

        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unitsByUser = prefs.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_metric_value));

            return WeatherDataParser.getWeatherDataFromJson(forecastJsonStr, numDays, unitsByUser);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "error while parsing json: ", e);
        }
        return null;
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            return getLiveWeatherData(params[0], Constants.MODE_JSON, Constants.COUNT_DEFAULT, Constants.UNIT_METRIC, Constants.APP_ID_DEFAULT);
        }

        @Override
        protected void onPostExecute(String[] weekWeatherList) {
            mForecastAdapter.clear();
            if (weekWeatherList != null) {
                mForecastAdapter.addAll(weekWeatherList);
            }
            Log.d(LOG_TAG, "refreshed weather data!");

        }
    }
}