package app.learn.sunshineex.zasypinnv.sunshinemy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.learn.sunshineex.zasypinnv.sunshinemy.data.WeatherContract;

// for Loaders

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    ForecastAdapter mForecastAdapter;
    int loaderId = 0;
    int mPosition;
    Cursor cur;
    ListView lv;

    private String mPositionBundle = "position";

    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;


    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(mPositionBundle))
            mPosition = savedInstanceState.getInt(mPositionBundle, 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        List<String> forecastData = new ArrayList<String>(); //CreateFakeList();

//        // populate database
//        String locationSetting = Utility.getPreferredLocation(getActivity());
//
//        // Sort order:  Ascending, by date.
//        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
//        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
//                locationSetting, System.currentTimeMillis());
//
//        cur = getActivity().getContentResolver().query(weatherForLocationUri,
//                null, null, null, sortOrder);


//        mForecastAdapter = new ForecastAdapter(getActivity(),
//                R.layout.list_item_forecast,
//                R.id.list_item_forecast_textview,
//                forecastData);

//        mForecastAdapter = new ForecastAdapter(getActivity(), cur, 0);

        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);
        lv = (ListView)rootView.findViewById(R.id.listview_forecast);
        lv.setAdapter(mForecastAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if(cursor != null){
                    String locationSettings = Utility.getPreferredLocation(getActivity());

                    ((Callback) getActivity())
                            .onItemSelected(WeatherContract.WeatherEntry
                                    .buildWeatherLocationWithDate(locationSettings, cursor.getLong(COL_WEATHER_DATE)));
                }
                mPosition = position;

            }
        });


//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Context context = getContext();
//                String text = mForecastAdapter.getItem(i);
//                int duration = Toast.LENGTH_SHORT;
//
//                Intent intent = new Intent(context, DetailActivity.class);
//                intent.putExtra(Intent.EXTRA_TEXT, text);
//                startActivity(intent);
////                Toast toast = Toast.makeText(context, text, duration);
////                toast.show();
//            }
//        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION)
        {
            outState.putInt(mPositionBundle, mPosition);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        if (id == R.id.action_show_location_on_map)
        {
            openPrefferedLocationInMap();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(loaderId, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // populate database
        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());
        cur = getActivity().getContentResolver().query(weatherForLocationUri,
                null, null, null, sortOrder);

        return new CursorLoader(getActivity(), weatherForLocationUri, FORECAST_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
        if(mPosition != ListView.INVALID_POSITION)
            lv.smoothScrollToPosition(mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

    @Override
    public void onStart() {
        super.onStart();
//        updateWeather();
    }

    public void onLocationChanged()
    {
        updateWeather();
        getLoaderManager().restartLoader(loaderId, null, this);
    }

    private void updateWeather()
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = pref.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
//        FetchWeatherTask task = new FetchWeatherTask(getActivity(), mForecastAdapter);
        FetchWeatherTask task = new FetchWeatherTask(getActivity());
        task.execute(location);
//        task.execute(location);
    }

    private List<String> CreateFakeList()
    {
        String[] forecasts = {
            "Today - sunny - 88/63",
            "Tomorrow - sunny - 89/67",
            "Tomorrow - sunny - 90/67",
            "Tomorrow - sunny - 89/67",
            "Tomorrow - sunny - 89/67",
            "Tomorrow - cloudy - 75/50",
            "Tomorrow - sunny - 89/67",
            "Tomorrow - cloudy - 75/50",
            "Tomorrow - sunny - 89/67"
        };

        List<String> allForecasts = new ArrayList<String>(Arrays.asList(forecasts));

        //String response = CallForecast();

        return allForecasts;
    }

    private void openPrefferedLocationInMap()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String location = prefs.getString(getString(R.string.pref_location_key),
                                          getString(R.string.pref_location_default));
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null)
        {
            startActivity(intent);
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri contentUri);
    }
}
