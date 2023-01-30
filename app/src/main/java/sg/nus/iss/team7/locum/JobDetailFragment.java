package sg.nus.iss.team7.locum;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;

import sg.nus.iss.team7.locum.Model.JobPost;
import sg.nus.iss.team7.locum.Utilities.DatetimeParser;

public class JobDetailFragment extends Fragment {

    TextView addInfo;
    TextView description;
    TextView startTime;
    TextView endTime;
    TextView totalTime;
    TextView ratePerHour;
    TextView totalRate;

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
        JobPost jobPost = (JobPost) getArguments().getParcelable("jobPost");

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

        return view;
    }
}