package sg.nus.iss.team7.locum;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class SettingsFragment extends Fragment {

    TextView editProfileLink,logOutLink;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_settings, container, false);
        editProfileLink = view.findViewById(R.id.editProfile);
        logOutLink = view.findViewById(R.id.logOut);
        editProfileLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),EditProfileActivity.class);
                startActivity(intent);
            }
        });
        logOutLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setIcon(R.drawable.ic_exit_application)
                        .setTitle(getResources().getString(R.string.LogOut))
                        .setMessage(getResources().getString(R.string.LogOutPrompt))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.Yes), (dialog, id) ->
                                {
                                    clearSharedPref();
                                    getActivity().finish();
                                }
                        )
                        .setNegativeButton(getResources().getString(R.string.No), null)
                        .show();

            }
        });
        return view;
    }

    private void clearSharedPref(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getResources().getString(R.string.Freelancer_Shared_Pref), MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

}