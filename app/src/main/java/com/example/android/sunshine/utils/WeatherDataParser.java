package com.example.android.sunshine.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by niraj on 2/1/16.
 */
public class WeatherDataParser {

    private static final String LOG_TAG = WeatherDataParser.class.getSimpleName();
    private static final String ARRAY_NAME_TAG = "list";
    private static final String MAX_TEMPERATURE_TAG = "max";
    private static final String MIN_TEMPERATURE_TAG = "min";
    private static final String TEMPERATURE_TAG = "temp";
    private static final String DATE_TAG = "dt";
    private static final String DATE_FORMAT = "E,MMM d";
    private static final String WEATHER_TAG = "weather";
    private static final String WEATHER_SHORT_DESC_TAG = "main";

    /**
     * Given a string of the form returned by the api call:
     * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
     * retrieve the maximum temperature for the day indicated by dayIndex
     * (Note: 0-indexed, so 0 would refer to the first day).
     */
    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
            throws JSONException {

        JSONObject jsonObject = new JSONObject(weatherJsonStr);
        JSONArray weekWeather = (JSONArray) jsonObject.get(ARRAY_NAME_TAG);
        JSONObject dayWeather = weekWeather.getJSONObject(dayIndex);
        JSONObject dayTemperature = dayWeather.getJSONObject(TEMPERATURE_TAG);
        double maxTemp = (double) dayTemperature.get(MAX_TEMPERATURE_TAG);
        return maxTemp;
    }


    public static String[] getWeatherDataFromJson(String weatherJsonStr, int numDays, String units) throws JSONException {
        JSONObject jsonObject = new JSONObject(weatherJsonStr);
        JSONArray weekWeather = (JSONArray) jsonObject.get(ARRAY_NAME_TAG);

        int listSize = weekWeather.length();

        if (numDays != listSize) {
            throw new JSONException("list size in json string differs from no. of days. listSize=" +
                    listSize + " numDays=" + numDays);
        }

        String[] resultWeather = new String[numDays];

        for (int i = 0; i < numDays; i++) {

            JSONObject dayWeather = weekWeather.getJSONObject(i);

            String dateStr = getFormattedDate(dayWeather.getLong(DATE_TAG));
            String highLowTemp = getHighLowTemp(dayWeather.getJSONObject(TEMPERATURE_TAG), units);
            String description = getWeatherShortDescription(dayWeather.getJSONArray(WEATHER_TAG));

            resultWeather[i] = dateStr + " - " + description + " - " + highLowTemp;
        }
        Log.d(LOG_TAG, "prepared weather string array:\n" + Arrays.toString(resultWeather));

        return resultWeather;
    }

    private static String getWeatherShortDescription(JSONArray jsonArray) throws JSONException {
        return ((JSONObject) jsonArray.get(0)).getString(WEATHER_SHORT_DESC_TAG);
    }

    private static String getFormattedDate(long seconds) {

        Date date = new Date(seconds * 1000);//millis
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        return formatter.format(date).toString();

    }

    private static String getHighLowTemp(JSONObject jsonObject, String units) throws JSONException {

        double max = (double) jsonObject.getDouble(MAX_TEMPERATURE_TAG);
        double min = (double) jsonObject.getDouble(MIN_TEMPERATURE_TAG);

        if (!units.equals(Constants.UNIT_METRIC)) {
            if (units.equals(Constants.UNIT_IMPERIAL)) {
                max = max * 1.8 + 32;
                min = min * 1.8 + 32;
            } else {
                Log.e(LOG_TAG, "invalid units: " + units + ", proceeding with metric");
            }
        }
        return String.format("%.2f/%.2f", min, max);
    }
}
