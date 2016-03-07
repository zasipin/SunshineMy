package app.learn.sunshineex.zasypinnv.sunshinemy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private ShareActionProvider mShareActionProvider;
    private String mDetailedText;
    private String appName = " #" + getString(R.string.app_name);

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
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        TextView detail_text = (TextView)view.findViewById(R.id.detail_text);
        mDetailedText = text + appName;
        detail_text.setText(text);

        return view;
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
}
