package sg.nus.iss.team7.locum;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sg.nus.iss.team7.locum.APICommunication.ApiMethods;
import sg.nus.iss.team7.locum.APICommunication.RetroFitClient;
import sg.nus.iss.team7.locum.Model.JobPost;
import sg.nus.iss.team7.locum.Utilities.DatetimeParser;
import sg.nus.iss.team7.locum.Utilities.JsonFieldParser;
import sg.nus.iss.team7.locum.ViewModel.ItemViewModel;

public class JobDetailFragment extends Fragment {

    ItemViewModel viewModel;
    JobPost jobPost;
    TextView addInfo;
    TextView description;
    TextView startTime;
    TextView endTime;
    TextView totalTime;
    TextView ratePerHour;
    TextView totalRate;
    Button button;

    public static JobDetailFragment newInstance(JobPost jobPost) {
        JobDetailFragment fragment = new JobDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("jobPost", (Parcelable) jobPost);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_detail, container, false);
        // Inflate the layout for this fragment
        jobPost = (JobPost) getArguments().getParcelable("jobPost");

        String hourRateStr = "$" + jobPost.getRatePerHour().toString() + "/HR";
        String fullRateStr = "$" + jobPost.getTotalRate().toString();

        description = view.findViewById(R.id.title);
        description.setText(jobPost.getDescription());

        startTime = view.findViewById(R.id.startTime);
        try {
            startTime.setText(DatetimeParser.parseTime(jobPost.getStartDateTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        endTime = view.findViewById(R.id.endTime);
        try {
            endTime.setText(DatetimeParser.parseTime(jobPost.getEndDateTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        totalTime = view.findViewById(R.id.totalTime);
        try {
            totalTime.setText(DatetimeParser.getHoursBetween(jobPost.getStartDateTime(), jobPost.getEndDateTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ratePerHour = view.findViewById(R.id.hourlyRate);
        ratePerHour.setText(hourRateStr);

        totalRate = view.findViewById(R.id.totalShift);
        totalRate.setText(fullRateStr);

        addInfo = view.findViewById(R.id.addInfo);
        addInfo.setMovementMethod(new ScrollingMovementMethod());

        // Set button text according to job post status
        button = view.findViewById(R.id.jobDetailBtn);
        if (jobPost.getStatus().equalsIgnoreCase("OPEN")) {
            button.setText("APPLY");
        } else if (jobPost.getStatus().equalsIgnoreCase("PENDING_ACCEPTANCE")){
            button.setText("CANCEL");
        } else if(jobPost.getStatus().equalsIgnoreCase("ACCEPTED")){
            button.setText("CANCEL");
        } else if ((jobPost.getStatus().startsWith("COMPLETED"))) {
            button.setVisibility(GONE);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jobPost.getStatus().equalsIgnoreCase("OPEN")) {
                    setJobStatus("apply");
                } else if (jobPost.getStatus().equalsIgnoreCase("PENDING_ACCEPTANCE")){
                    setJobStatus("cancel");
                } else if (jobPost.getStatus().equalsIgnoreCase("ACCEPTED")){
                    String alertMsg=getString(R.string.cancelMsg);
                    String alertTitle=getString(R.string.cancelAlertTitle);

                    AlertDialog.Builder dlg = new AlertDialog.Builder(getContext())
                        .setTitle(alertTitle)
                        .setMessage(alertMsg)
                        .setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "Cancel successfully", Toast.LENGTH_SHORT).show();
                                setJobStatus("cancel");
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "Not cancel", Toast.LENGTH_SHORT).show();
                            }
                        });
                    dlg.show();
                }
            }
        });

        return view;
    }

    public void setJobStatus(String status) {
        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);

        // API call
        Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
        ApiMethods api = retrofit.create(ApiMethods.class);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("FL_Shared_Pref", MODE_PRIVATE);
        String userDetails = sharedPref.getString("FL_Details", "no value");
        // TODO: change userid hardcoding
        //String id = JsonFieldParser.getField(userDetails, "id");

        Call<JobPost> call = api.setJobStatus(jobPost.getId().toString(), status, "1");

        call.enqueue(new Callback<JobPost>() {
            @Override
            public void onResponse(Call<JobPost> call, Response<JobPost> response) {
                if (response.isSuccessful()) {
                    if (status.equalsIgnoreCase("apply")) {
                        jobPost.setStatus("PENDING_ACCEPTANCE");
                        button.setText("CANCEL");
                    } else if (status.equalsIgnoreCase("cancel")) {
                        jobPost.setStatus("OPEN");
                        button.setText("APPLY");
                    }
                    viewModel.selectItem(jobPost);
                }
            }

            @Override
            public void onFailure(Call<JobPost> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "error setting job status", Toast.LENGTH_SHORT);
            }
        });
    }
}