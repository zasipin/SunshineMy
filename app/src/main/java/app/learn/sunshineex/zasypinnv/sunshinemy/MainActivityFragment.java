package app.learn.sunshineex.zasypinnv.sunshinemy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        List<String> forecastData = CreateFakeList();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                                                R.layout.list_item_forecast,
                                                                R.id.list_item_forecast_textview,
                                                                forecastData);
        ListView lv = (ListView)rootView.findViewById(R.id.listview_forecast);
        lv.setAdapter(adapter);
        return rootView;
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

        return allForecasts;
    }
}
