package sg.nus.iss.team7.locum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sg.nus.iss.team7.locum.APICommunication.ApiMethods;
import sg.nus.iss.team7.locum.APICommunication.RetroFitClient;
import sg.nus.iss.team7.locum.Model.JobPost;
import sg.nus.iss.team7.locum.Utilities.JsonFieldParser;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private int saveState;
    private HomeFragment homeFragment = new HomeFragment();
    private JobSearchFragment jobSearchFragment = new JobSearchFragment();
    private MyJobsFragment myJobsFragment = new MyJobsFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();
    private JobMatchFragment jobMatchFragment = new JobMatchFragment();
    private NotificationsFragment notificationsFragment = new NotificationsFragment();
    private ImageView notificationIndicator;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        ImageButton notificationButton = (ImageButton) findViewById(R.id.notificationsImage);
        notificationIndicator = (ImageView) findViewById(R.id.notificationIndicator);

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.container,notificationsFragment).commit();
            }
        });

        getNotificationStatus();

        SharedPreferences sharedPref = getSharedPreferences("FL_Shared_Pref", MODE_PRIVATE);
        String userDetails = sharedPref.getString("FL_Details", "no value");
        if(userDetails!="no value") {
            String name = JsonFieldParser.getField(userDetails, "name");

            TextView topBarText = findViewById(R.id.nameBar);
            topBarText.setText("Hello, " + name);

        }
        if(savedInstanceState!=null) {
            bottomNavigationView.setSelectedItemId(saveState);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuHome:
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.container,homeFragment).commit();
                        return true;
                    case R.id.menuSearch:
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.container,jobSearchFragment).commit();
                        return true;
                    case R.id.menuJobs:
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.container,myJobsFragment).commit();
                        return true;
                    case R.id.menuSettings:
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.container,settingsFragment).commit();
                        return true;
                    case R.id.menuRecommended:
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.container,jobMatchFragment).commit();
                        return true;
                }

                return false;
            }
        });
    }

    public void getNotificationStatus() {

        // API call
        Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
        ApiMethods api = retrofit.create(ApiMethods.class);

        String id = getUserIdString();

        Call<Boolean> call = api.getNotificationStatus(Integer.parseInt(id));

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Boolean hasUnread = response.body();
                    if (hasUnread != null) {
                        if (hasUnread) {
                            notificationIndicator.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(context,"error getting notifications", Toast.LENGTH_SHORT);
            }
        });
    }

    @Nullable
    private String getUserIdString() {
        SharedPreferences sharedPref = getSharedPreferences("FL_Shared_Pref", MODE_PRIVATE);
        String userDetails = sharedPref.getString("FL_Details", "no value");

        String id = JsonFieldParser.getField(userDetails, "id");
        return id;
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(saveState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        saveState = bottomNavigationView.getSelectedItemId();
    }
}