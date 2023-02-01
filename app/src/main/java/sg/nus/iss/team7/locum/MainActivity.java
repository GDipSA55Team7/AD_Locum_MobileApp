package sg.nus.iss.team7.locum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import sg.nus.iss.team7.locum.Utilities.JsonFieldParser;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private int saveState;
    private HomeFragment homeFragment = new HomeFragment();
    private JobSearchFragment jobSearchFragment = new JobSearchFragment();
    private MyJobsFragment myJobsFragment = new MyJobsFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        SharedPreferences sharedPref = getSharedPreferences("FL_Shared_Pref", MODE_PRIVATE);
        String userDetails = sharedPref.getString("FL_Details", "no value");

        String name = JsonFieldParser.getField(userDetails, "name");

        TextView topBarText = findViewById(R.id.nameBar);
        topBarText.setText("Hello, " + name);


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
                }

                return false;
            }
        });
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