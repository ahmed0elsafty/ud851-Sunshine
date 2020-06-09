/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.sunshine.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    public static final String[] projection = {
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_WEATHER_ID,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES
    };
    /*
     * In this Activity, you can share the selected day's forecast. No social sharing is complete
     * without using a hashtag. #BeTogetherNotTheSame
     */
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private static final int DATE_INDEX = 0;
    private static final int WEATHER_ID_INDEX = 1;
    private static final int MAX_TEMP_INDEX = 2;
    private static final int MIN_TEMP_INDEX = 3;
    private static final int HUMIDITY_INDEX = 4;
    private static final int PRESSURE_INDEX = 5;
    private static final int WIND_INDEX = 6;
    private static final int DEGREES_INDEX = 7;


    private static final int LOADER_ID = 22;


    TextView mDateTextView,
            mDescTextView,
            mHighTextView,
            mLowTextView,
            mHumidityTextView,
            mWindTextView,
            mPressureTextView;

    Uri mUri;
    /* A summary of the forecast that can be shared by clicking the share button in the ActionBar */
    private String mForecastSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mDateTextView = findViewById(R.id.day_date);
        mLowTextView = findViewById(R.id.day_low_temp);
        mHighTextView = findViewById(R.id.day_high_temp);
        mHumidityTextView = findViewById(R.id.day_humidity);
        mPressureTextView = findViewById(R.id.day_pressure);
        mWindTextView = findViewById(R.id.day_wind);
        mDescTextView = findViewById(R.id.day_weather_desc);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            mUri = intentThatStartedThisActivity.getData();
            if (mUri == null) {
                throw new NullPointerException(mUri.toString());
            }
        }
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.detail, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu. Android will
     * automatically handle clicks on the "up" button for us so long as we have specified
     * DetailActivity's parent Activity in the AndroidManifest.
     *
     * @param item The menu item that was selected by the user
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Get the ID of the clicked item */
        int id = item.getItemId();

        /* Settings menu item clicked */
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        /* Share menu item clicked */
        if (id == R.id.action_share) {
            Intent shareIntent = createShareForecastIntent();
            startActivity(shareIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing.  All we need
     * to do is set the type, text and the NEW_DOCUMENT flag so it treats our share as a new task.
     * See: http://developer.android.com/guide/components/tasks-and-back-stack.html for more info.
     *
     * @return the Intent to use to share our weather forecast
     */
    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        if (loaderId == LOADER_ID) {
            return new CursorLoader(this,
                    mUri,
                    projection,
                    null,
                    null,
                    null);
        } else {
            throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            if (cursor.moveToNext()) {
                long dateInMillis = cursor.getLong(DATE_INDEX);
                String date = SunshineDateUtils.getFriendlyDateString(this, dateInMillis, true);
                int weatherId = cursor.getInt(WEATHER_ID_INDEX);
                String desc = SunshineWeatherUtils.getStringForWeatherCondition(this, weatherId);
                double highTemp = cursor.getDouble(MAX_TEMP_INDEX);
                double lowTemp = cursor.getDouble(MIN_TEMP_INDEX);
                String highString = SunshineWeatherUtils.formatTemperature(this, highTemp);
                String lowString = SunshineWeatherUtils.formatTemperature(this, lowTemp);
                float humidity = cursor.getFloat(HUMIDITY_INDEX);
                String humidityString = getString(R.string.format_humidity, humidity);
                float pressure = cursor.getFloat(PRESSURE_INDEX);
                String pressureString = getString(R.string.format_pressure, pressure);
                float speedWind = cursor.getFloat(WIND_INDEX);
                float degrees = cursor.getFloat(DEGREES_INDEX);
                String windString = SunshineWeatherUtils.getFormattedWind(this, speedWind, degrees);
                mDateTextView.setText(date);
                mDescTextView.setText(desc);
                mHighTextView.setText(highString);
                mLowTextView.setText(lowString);
                mHumidityTextView.setText(humidityString);
                mPressureTextView.setText(pressureString);
                mWindTextView.setText(windString);
                mForecastSummary = String.format("%s - %s - %s/%s",
                        date, desc, highString, lowString);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}