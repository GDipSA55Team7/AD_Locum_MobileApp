package sg.nus.iss.team7.locum.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import sg.nus.iss.team7.locum.Model.FreeLancer;
import sg.nus.iss.team7.locum.R;

public class SharedPrefUtility {

    public static FreeLancer readFromSharedPref(Context context){

        Gson gson = new Gson();
        SharedPreferences sharedPreferences =  context.getSharedPreferences( context.getResources().getString(R.string.Freelancer_Shared_Pref), Context.MODE_PRIVATE);
        String json = sharedPreferences.getString( context.getResources().getString(R.string.Freelancer_Details), "");
        FreeLancer fl = gson.fromJson(json, FreeLancer.class);
        return fl;
    }


    public static void storeFLDetailsInSharedPref(Context context, FreeLancer freeLancer){
        Gson gson = new Gson();
        String json = gson.toJson(freeLancer);
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.Freelancer_Shared_Pref), Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(context.getResources().getString(R.string.Freelancer_Details), json).apply();
    }
}
