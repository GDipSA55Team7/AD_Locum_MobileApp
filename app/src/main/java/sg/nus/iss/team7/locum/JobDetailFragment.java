package sg.nus.iss.team7.locum;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sg.nus.iss.team7.locum.APICommunication.ApiMethods;
import sg.nus.iss.team7.locum.APICommunication.RetroFitClient;
import sg.nus.iss.team7.locum.Model.FreeLancer;
import sg.nus.iss.team7.locum.Model.JobPost;
import sg.nus.iss.team7.locum.Model.PaymentDetailsDTO;
import sg.nus.iss.team7.locum.Utilities.DatetimeParser;
import sg.nus.iss.team7.locum.Utilities.JsonFieldParser;
import sg.nus.iss.team7.locum.ViewModel.ItemViewModel;

public class JobDetailFragment extends Fragment {

    ItemViewModel viewModel;
    JobPost jobPost;
    TextView addInfo;
    TextView title;
    TextView startTime;
    TextView endTime;
    TextView totalTime;
    TextView ratePerHour;
    TextView totalRate;
    Button button;
    private Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
    private ApiMethods api = retrofit.create(ApiMethods.class);

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

        title = view.findViewById(R.id.title);
        title.setText(jobPost.getTitle());

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
        addInfo.setText(jobPost.getDescription());
        addInfo.setMovementMethod(new ScrollingMovementMethod());

        // Set button text according to job post status
        button = view.findViewById(R.id.jobDetailBtn);
        if (jobPost.getStatus().equalsIgnoreCase("OPEN")) {
            button.setText("APPLY");
        } else if (jobPost.getStatus().equalsIgnoreCase("PENDING_CONFIRMATION_BY_CLINIC")) {
            button.setText("CANCEL");
        } else if (jobPost.getStatus().equalsIgnoreCase("ACCEPTED")) {
            button.setText("CANCEL");
        } else if (jobPost.getStatus().equalsIgnoreCase("CANCELLED")) {
            button.setVisibility(GONE);
        }
        else if (jobPost.getStatus().startsWith("COMPLETED") || jobPost.getStatus().equalsIgnoreCase("Processed_Payment") ) {

            button.setText("PAYMENT DETAILS");

            //If pending payment
            if(jobPost.getStatus().contains("PENDING_PAYMENT")){
                //Show payment pending animation
                LottieAnimationView paymentProcessingAnimation = (LottieAnimationView) view.findViewById(R.id.paymentProcessingAnimation);
                paymentProcessingAnimation.setVisibility(View.VISIBLE);

                //update text
                TextView textView = (TextView) view.findViewById(R.id.paymentStatus);
                textView.setText("Pending");
                textView.setVisibility(View.VISIBLE);
                textView.setTextColor(Color.parseColor("#0070ba"));
                TextView paymentStatusTxt = (TextView) view.findViewById(R.id.paymentStatusText);
                paymentStatusTxt.setVisibility(View.VISIBLE);
            }
            //if payment success
            else{
                //Show payment success animation
                LottieAnimationView paymentSuccessAnimation = (LottieAnimationView) view.findViewById(R.id.paymentSuccessAnimation);
                paymentSuccessAnimation.setVisibility(View.VISIBLE);

                //update text
                TextView paymentStatus = (TextView) view.findViewById(R.id.paymentStatus);
                paymentStatus.setText("Completed");
                paymentStatus.setTextColor(Color.parseColor("#22b573"));
                paymentStatus.setVisibility(View.VISIBLE);
                TextView paymentStatusTxt = (TextView) view.findViewById(R.id.paymentStatusText);
                paymentStatusTxt.setVisibility(View.VISIBLE);
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jobPost.getStatus().equalsIgnoreCase("OPEN")) {

                    // API call
                    String id = getUserIdString();

                    Call<ArrayList<JobPost>> call = api.getJobsByUserId(Integer.parseInt(id));

                    call.enqueue(new Callback<ArrayList<JobPost>>() {
                        @Override
                        public void onResponse(Call<ArrayList<JobPost>> call, Response<ArrayList<JobPost>> response) {
                            if (response.isSuccessful()) {
                               List<JobPost> jobPostListByUser = response.body();
                                if (jobPostListByUser == null) {
                                    setJobStatus("apply");
                                } else {
                                    if(jobOverlapsWithExistingJobsByDayAndTime(jobPost,jobPostListByUser)){
                                        dialogForJobOverlapByDateAndTime();
                                    }
                                    else{
                                        setJobStatus("apply");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<JobPost>> call, Throwable t) {
                            t.printStackTrace();
                            Toast.makeText(getContext(),"error getting job count", Toast.LENGTH_SHORT);
                        }
                    });

                } else if (jobPost.getStatus().equalsIgnoreCase("PENDING_CONFIRMATION_BY_CLINIC") || jobPost.getStatus().equalsIgnoreCase("ACCEPTED")){
                    showCancelDialogue();
                }
                else if ((jobPost.getStatus().startsWith("COMPLETED"))) {

                    Gson gson = new Gson();
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.Freelancer_Shared_Pref), MODE_PRIVATE);
                    String json = sharedPreferences.getString(getResources().getString(R.string.Freelancer_Details), "");
                    FreeLancer fl = gson.fromJson(json, FreeLancer.class);


                    PaymentDetailsDTO paymentDTO = new PaymentDetailsDTO(
                            jobPost.getId(),
                            jobPost.getRatePerHour(),
                            jobPost.getTotalRate(),
                            jobPost.getAdditionalFeeListString(),
                            jobPost.getDescription(),
                            jobPost.getStartDateTime(),
                            jobPost.getEndDateTime(),
                            jobPost.getPaymentDate(),
                            jobPost.getPaymentRefNo(),
                            jobPost.getClinic().getName(),
                            jobPost.getClinic().getAddress(),
                            jobPost.getClinic().getPostalCode(),
                            jobPost.getClinic().getContact(),
                            jobPost.getClinic().getHcicode(),
                            fl.getName(),
                            fl.getEmail(),
                            fl.getContact(),
                            fl.getMedicalLicenseNo()
                    );
                    launchPaymentDetailsActivity(paymentDTO);
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

        String id = JsonFieldParser.getField(userDetails, "id");

        Call<JobPost> call = api.setJobStatus(jobPost.getId().toString(), status, id);

        call.enqueue(new Callback<JobPost>() {
            @Override
            public void onResponse(Call<JobPost> call, Response<JobPost> response) {
                if (response.isSuccessful()) {
                    if (status.equalsIgnoreCase("apply")) {
                        //todo check if any job application on same timing
                        jobPost.setStatus("PENDING_CONFIRMATION_BY_CLINIC");
                        button.setEnabled(false);
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
                Toast.makeText(getContext(), "Error setting job status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showCancelDialogue() {
        String alertMsg=getString(R.string.cancelMsg);
        String alertTitle=getString(R.string.cancelAlertTitle);

        AlertDialog.Builder dlg = new AlertDialog.Builder(getContext())
                .setTitle(alertTitle)
                .setMessage(alertMsg)
                .setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Application was successfully cancelled", Toast.LENGTH_SHORT).show();
                        setJobStatus("cancel");
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Application was not cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
        dlg.show();
    }


    private void launchPaymentDetailsActivity(PaymentDetailsDTO paymentDTO){
        Intent intent = new Intent(getActivity(),PaymentDetailsActivity.class);
        intent.putExtra("paymentDetails", paymentDTO);
        startActivity(intent);
    }
    public void dialogForJobOverlapByDateAndTime(){

        String alertMsg="Comfirm Want to Apply?";
        String alertTitle="Job Date and Time Overlaps with Existing Jobs";

        AlertDialog.Builder dlg = new AlertDialog.Builder(getContext())
                .setTitle(alertTitle)
                .setMessage(alertMsg)
                .setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setJobStatus("apply");
                    }
                })
                .setNegativeButton(getResources().getString(R.string.No), null);
        dlg.show();
    }


    @Nullable
    private String getUserIdString() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("FL_Shared_Pref", MODE_PRIVATE);
        String userDetails = sharedPref.getString("FL_Details", "no value");

        String id = JsonFieldParser.getField(userDetails, "id");
        return id;
    }

    public Boolean jobOverlapsWithExistingJobsByDayAndTime(JobPost jobPost,List<JobPost> existingJobsListForUser) {
        Boolean overlaps = false;
        LocalDateTime jobPostStart =  DatetimeParser.parseLocalDateTime(jobPost.getStartDateTime());
        LocalDateTime jobPostEnd = DatetimeParser.parseLocalDateTime(jobPost.getEndDateTime());
        for (JobPost existingJob : existingJobsListForUser ) {
            LocalDateTime existingJobStart =  DatetimeParser.parseLocalDateTime(existingJob.getStartDateTime());
            LocalDateTime existingJobEnd =  DatetimeParser.parseLocalDateTime(existingJob.getEndDateTime());
            if (!(jobPostEnd.isBefore(existingJobStart) || jobPostStart.isAfter(existingJobEnd))) {
                overlaps = true;
                break;
            }
        }
        return overlaps;
    }

}