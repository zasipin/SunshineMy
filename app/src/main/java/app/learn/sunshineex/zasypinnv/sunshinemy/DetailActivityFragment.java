package app.learn.sunshineex.zasypinnv.sunshinemy;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ShareActionProvider mShareActionProvider;
    private String mDetailedText;
    private String appName;
    private int loaderId = 2;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        appName = " #" + getString(R.string.app_name);
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

//        Intent intent = getActivity().getIntent();
//        String text = intent.getDataString();
//        TextView detail_text = (TextView)view.findViewById(R.id.detail_text);
//        mDetailedText = text + appName;
//        detail_text.setText(text);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(loaderId, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detail, menu);

        MenuItem item = menu.findItem(R.id.action_share);
        // Fetch and store ShareActionProvider
        if (item != null) {
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        }
        createShareIntent(mDetailedText);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void createShareIntent(String text)
    {
        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intentShare.setType("text/plain");
        intentShare.putExtra(Intent.EXTRA_TEXT, text);
        setShareIntent(intentShare);
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
//            Uri uriString = Uri.parse(intent.getDataString());

        return new CursorLoader(getActivity(), intent.getData(), ForecastProjection.FORECAST_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String text = convertCursorRowToUXFormat(data);
        TextView detail_text = (TextView)getView().findViewById(R.id.detail_text);
        mDetailedText = text + appName;
        detail_text.setText(text);

        if (mShareActionProvider != null)
        {
            createShareIntent(text);
        }
//        data.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        TextView detail_text = (TextView)getView().findViewById(R.id.detail_text);
//        detail_text.setText("");
//        loader.reset();
    }

    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(getActivity());
        String highLowStr = Utility.formatTemperature(getActivity(), high, isMetric) + "/" + Utility.formatTemperature(getActivity(), low, isMetric);
        return highLowStr;
    }

    private String convertCursorRowToUXFormat(Cursor cursor) {
        int idx_max_temp = ForecastProjection.COL_WEATHER_MAX_TEMP;
        int idx_min_temp = ForecastProjection.COL_WEATHER_MIN_TEMP;
        int idx_date = ForecastProjection.COL_WEATHER_DATE;
        int idx_short_desc = ForecastProjection.COL_WEATHER_DESC;
        if (cursor != null && cursor.moveToFirst()) {
            String highAndLow = formatHighLows(
                    cursor.getDouble(idx_max_temp),
                    cursor.getDouble(idx_min_temp));

            return Utility.formatDate(cursor.getLong(idx_date)) +
                    " - " + cursor.getString(idx_short_desc) +
                    " - " + highAndLow;
        }
        return "";
    }
}
