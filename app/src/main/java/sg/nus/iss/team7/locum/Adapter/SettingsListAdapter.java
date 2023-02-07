package sg.nus.iss.team7.locum.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import sg.nus.iss.team7.locum.EditProfileActivity;
import sg.nus.iss.team7.locum.FireBase.FirebaseTokenUtils;
import sg.nus.iss.team7.locum.LoginActivity;
import sg.nus.iss.team7.locum.Model.FreeLancer;
import sg.nus.iss.team7.locum.R;

public class SettingsListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final Integer[] imageId;
    private final String[] SettingsText;

    public SettingsListAdapter(Activity context, String[] SettingsText, Integer[] imageId) {
        super(context, R.layout.settings_list_item, SettingsText);
        this.context = context;
        this.SettingsText = SettingsText;
        this.imageId = imageId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.settings_list_item, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.textView);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imgView);
        txtTitle.setText(SettingsText[position]);
        imageView.setImageResource(imageId[position]);

        String editProfileString = context.getResources().getString(R.string.EditProfile);
        String logOutString = context.getResources().getString(R.string.LogOut);
        View linkView = rowView.findViewById(R.id.rowSetting);
        if (SettingsText[position].equals(editProfileString)) {
            linkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EditProfileActivity.class);
                    context.startActivity(intent);
                }
            });
        } else if (SettingsText[position].equals(logOutString)) {
            linkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setIcon(R.drawable.ic_exit_application)
                            .setTitle(context.getResources().getString(R.string.LogOut))
                            .setMessage(context.getResources().getString(R.string.LogOutPrompt))
                            .setCancelable(false)
                            .setPositiveButton(context.getResources().getString(R.string.Yes), (dialog, id) -> {
                                FreeLancer loggedOutUser = readFromSharedPref();
                                clearSharedPref();
                                FirebaseTokenUtils.updateServerOnLogout(loggedOutUser.getUsername());
                                returnToLoginActivity();
                            })
                            .setNegativeButton(context.getResources().getString(R.string.No), null)
                            .show();
                }
            });
        }
        return rowView;
    }
    private void clearSharedPref(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getContext().getResources().getString(R.string.Freelancer_Shared_Pref), MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }
    private void returnToLoginActivity(){
        Intent intent = new Intent(getContext(), LoginActivity.class);
        context.startActivity(intent);
    }
    private FreeLancer readFromSharedPref(){
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getContext().getResources().getString(R.string.Freelancer_Shared_Pref), MODE_PRIVATE);
        String json = sharedPreferences.getString(getContext().getResources().getString(R.string.Freelancer_Details), "");
        FreeLancer fl = gson.fromJson(json, FreeLancer.class);
        return fl;
    }
}