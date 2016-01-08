package com.example.android.sunshine.data;

/**
 * Created by niraj on 8/1/16.
 */
public class WeatherContract {

    /* This is a inner class which contains contents of the weather table */
    private static final class WeatherEntry implements BaseColumns {

        /**
         * Name of the table.
         */
        public static final String TABLE_NAME = "weather";

        /**
         * Foreign key reference to location.
         */
        public static String COLUMN_LOC_KEY = "location_id";

        /**
         * Date.
         */
        private static final String COLUMN_DATE = "date";

        /**
         * Foreign key reference to weather.
         */
        private static final String COLUMN_WEATHER_ID = "weather_id";

        private static final String COLUMN_SHORT_DESC = "short_desc";

        private static
    }
}
