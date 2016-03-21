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
    int loaderId = 1;
    Cursor cur;

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
        ListView lv = (ListView)rootView.findViewById(R.id.listview_forecast);
        lv.setAdapter(mForecastAdapter);


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

        return new CursorLoader(getActivity(), weatherForLocationUri, null, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
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

//    public class FetchWeatherTask extends AsyncTask<String, Void, String[]>
//    {
//        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
//
//        private String[] CallForecast(String postalCode)
//        {
//            // These two need to be declared outside the try/catch
//            // so that they can be closed in the finally block.
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//            int numDays = 7;
//
//            // Will contain the raw JSON response as a string.
//            String forecastJsonStr = null;
//
//            try {
//                // Construct the URL for the OpenWeatherMap query
//                // Possible parameters are avaiable at OWM's forecast API page, at
//                // http://openweathermap.org/API#forecast
//                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=150000&mode=json&units=metric&cnt=7&appid=61c0547f277bf21ab39a9567aa4bbdd4");
//
//                URL url = this.ConstructUrl(postalCode);
//
//                // Create the request to OpenWeatherMap, and open the connection
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                // Read the input stream into a String
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                if (inputStream == null) {
//                    // Nothing to do.
//                    return null;
//                }
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                    // But it does make debugging a *lot* easier if you print out the completed
//                    // buffer for debugging.
//                    buffer.append(line + "\n");
//                }
//
//                if (buffer.length() == 0) {
//                    // Stream was empty.  No point in parsing.
//                    return null;
//                }
//                forecastJsonStr = buffer.toString();
//
////                Log.v(LOG_TAG, "Forecast JSON string: " + forecastJsonStr);
//
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Error ", e);
//                // If the code didn't successfully get the weather data, there's no point in attemping
//                // to parse it.
//                return null;
//            } finally{
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e(LOG_TAG, "Error closing stream", e);
//                    }
//                }
//            }
//
//            WeatherDataParser weatherParser = new WeatherDataParser(getActivity());
//            try {
//                return weatherParser.getWeatherDataFromJson(forecastJsonStr, numDays);
//            }
//            catch (JSONException ex)
//            {
//                Log.e(LOG_TAG, ex.getMessage(), ex);
//                ex.printStackTrace();
//                return null;
//            }
//        }
//
//        private URL ConstructUrl(String postalCode)
//        {
//            String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily";
//            String mode = "json";
//            String units = "metric";
//            String cnt = "7";
//            String appid = "61c0547f277bf21ab39a9567aa4bbdd4";
//
//            String QUERY_PARAM = "q";
//            String FORMAT_PARAM = "mode";
//            String UNITS_PARAM = "units";
//            String DAYS_PARAM = "cnt";
//            String APP_PARAM = "appid";
//
//                    //?q=150000&mode=json&units=metric&cnt=7&appid=61c0547f277bf21ab39a9567aa4bbdd4";
//
//            Uri uri = Uri.parse(baseUrl).buildUpon()
//                .appendQueryParameter(QUERY_PARAM, postalCode)
//                .appendQueryParameter(FORMAT_PARAM, mode)
//                .appendQueryParameter(UNITS_PARAM, units)
//                .appendQueryParameter(DAYS_PARAM, cnt)
//                .appendQueryParameter(APP_PARAM, appid)
//                    .build();
//
//            URL url;
//            try {
//               url = new URL(uri.toString());
//            }
//            catch (MalformedURLException ex)
//            {
//                throw new RuntimeException(ex);
//            }
//
////            Log.v(LOG_TAG, "Built URI: " + uri.toString());
//
//            return url;
//        }
//
//        @Override
//        protected String[] doInBackground(String... params) {
//            String[] str = null;
//            if (params.length > 0) {
//                str = CallForecast(params[0]);
//            }
//            return str;
//        }
//
//        @Override
//        protected void onPostExecute(String[] strings) {
//            if (strings != null && strings.length > 0) {
//                mForecastAdapter.clear();
//                mForecastAdapter.addAll(strings);
////                mForecastAdapter.notifyDataSetChanged();
//            }
////            super.onPostExecute(strings);
//        }
//    }
}
