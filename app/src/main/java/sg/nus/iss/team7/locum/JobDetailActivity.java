package sg.nus.iss.team7.locum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;

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

                    String addressStr = jobPost.getClinic().getAddress() + ", " + jobPost.getClinic().getPostalCode();

                    clinicNameText.setText(jobPost.getClinic().getName());
                    addressText.setText(addressStr);

                    setStatusBar();


                    try {
                        dateText.setText(DatetimeParser.parseDate(jobPost.getStartDateTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

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