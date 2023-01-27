package sg.nus.iss.team7.locum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    JobSearchFragment jobSearchFragment = new JobSearchFragment();
    MyJobsFragment myJobsFragment = new MyJobsFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuHome:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
                        return true;
                    case R.id.menuSearch:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,jobSearchFragment).commit();
                        return true;
                    case R.id.menuJobs:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,myJobsFragment).commit();
                        return true;
                    case R.id.menuSettings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,settingsFragment).commit();
                        return true;
                }

                return false;
            }
        });
    }
}