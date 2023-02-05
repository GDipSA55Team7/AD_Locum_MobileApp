package sg.nus.iss.team7.locum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sg.nus.iss.team7.locum.APICommunication.ApiMethods;
import sg.nus.iss.team7.locum.APICommunication.RetroFitClient;
import sg.nus.iss.team7.locum.Model.JobPost;
import sg.nus.iss.team7.locum.Utilities.DatetimeParser;
import sg.nus.iss.team7.locum.ViewModel.ItemViewModel;

public class JobDetailActivity extends AppCompatActivity {

    private JobPost jobPost;
    private ItemViewModel viewModel;
    private TextView dateText;
    private TextView clinicNameText;
    private TextView addressText;
    private TextView statusText;

    private ImageView addressImg;

    private ImageView phoneImg;

    private ImageView emailImg;

    private String addressStr;

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Boolean mLocationPermissionGranted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        Intent intent = getIntent();
        int itemId = intent.getIntExtra("itemId", 0);

        getJobById(itemId);

        // listener to update status in UI if job is applied
        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        viewModel.getSelectedItem().observe(this, jobPost -> {
            setStatusBar();
        });


    }

    public void getJobById(int id) {

        // API call
        Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
        ApiMethods api = retrofit.create(ApiMethods.class);

        Call<JobPost> call = api.getJobById(id);

        call.enqueue(new Callback<JobPost>() {
            @Override
            public void onResponse(Call<JobPost> call, Response<JobPost> response) {
                if (response.isSuccessful()) {
                    jobPost = response.body();

                    dateText = (TextView) findViewById(R.id.date);
                    clinicNameText = (TextView) findViewById(R.id.clinicName);
                    addressText = (TextView) findViewById(R.id.address);
                    statusText = (TextView) findViewById(R.id.status);

                    addressStr = jobPost.getClinic().getAddress() + ", " + jobPost.getClinic().getPostalCode();
                    String phoneNo = jobPost.getClinic().getContact();
                    String emailAddress = jobPost.getClinic().getEmail();

                    clinicNameText.setText(jobPost.getClinic().getName());
                    addressText.setText(addressStr);

                    setStatusBar();


                    try {
                        dateText.setText(DatetimeParser.parseDate(jobPost.getStartDateTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    phoneImg = (ImageView) findViewById(R.id.phoneIcon);
                    phoneImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            callPhone(phoneNo);

                        }
                    });

                    emailImg = (ImageView) findViewById(R.id.emailIcon);

                    emailImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendEmail(emailAddress);
                        }
                    });

                    addressImg = (ImageView) findViewById(R.id.addressIcon);
                    addressImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getLocationPermission();
                        }
                    });

                    // Pass the data to fragment
                    Fragment jobDetailFragment = JobDetailFragment.newInstance(jobPost);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, jobDetailFragment).commit();
                }
            }

            @Override
            public void onFailure(Call<JobPost> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(),"error getting job", Toast.LENGTH_SHORT);
            }
        });
    }

    public void callPhone(String phoneNo){

        Uri uri = Uri.parse("tel:"+phoneNo);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        if (intent.resolveActivity(getPackageManager()) != null)
        {
            startActivity(intent);
        }

    }

    public void sendEmail(String emailAddress){

        Uri uri = Uri.parse("mailto:"+ emailAddress);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void getLocationPermission(){
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            }
        }
        else {
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults){
        mLocationPermissionGranted = false ;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i<grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    initMap();
                }
            }
        }
    }

    public void initMap(){
        Fragment mapsFragment = new MapsFragment(mLocationPermissionGranted,addressStr,jobPost);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,mapsFragment).commit();
    }

    private void setStatusBar() {
        if(jobPost.getStatus().equalsIgnoreCase("PENDING_CONFIRMATION_BY_CLINIC")) {
            statusText.setText("APPLIED");
            statusText.setBackgroundTintList(getColorStateList(R.color.status_mid));
        } else if (jobPost.getStatus().equalsIgnoreCase("OPEN")) {
            statusText.setText("OPEN");
            statusText.setBackgroundTintList(getColorStateList(R.color.status_green));
        } else if(jobPost.getStatus().equalsIgnoreCase("ACCEPTED")){
            statusText.setText("ACCEPTED");
            statusText.setBackgroundTintList(getColorStateList(R.color.darker_grey));
        }else if (jobPost.getStatus().startsWith("COMPLETED")) {
            statusText.setText("COMPLETED");
            statusText.setBackgroundTintList(getColorStateList(R.color.darker_grey));
        }
    }


}