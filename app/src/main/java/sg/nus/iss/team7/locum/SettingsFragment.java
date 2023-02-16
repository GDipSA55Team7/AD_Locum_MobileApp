package sg.nus.iss.team7.locum;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import sg.nus.iss.team7.locum.Adapter.SettingsListAdapter;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        ListView list = (ListView) view.findViewById(R.id.settingsListView);
        String[] values = {"Edit Profile", "Log Out"};
        Integer[] images = {R.drawable.ic_editprofile_settings, R.drawable.ic_logout_settings};
        SettingsListAdapter adapter = new SettingsListAdapter((Activity) getContext(), values, images);

        list.setAdapter(adapter);

        return view;
    }
}