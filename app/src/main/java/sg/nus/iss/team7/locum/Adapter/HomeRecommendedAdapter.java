package sg.nus.iss.team7.locum.Adapter;

import static android.view.View.GONE;
import static androidx.core.content.ContextCompat.getColorStateList;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;

import sg.nus.iss.team7.locum.Interface.RecyclerViewInterface;
import sg.nus.iss.team7.locum.JobDetailActivity;
import sg.nus.iss.team7.locum.Model.JobPost;
import sg.nus.iss.team7.locum.R;
import sg.nus.iss.team7.locum.Utilities.DatetimeParser;

public class HomeRecommendedAdapter extends RecyclerView.Adapter<HomeRecommendedAdapter.MyViewHolder>{

    Context context;
    ArrayList<JobPost> myList;
    JobPost jobPost;

    public HomeRecommendedAdapter(Context context) {
        this.context = context;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date, hour_rate, address, clinic_name, time, similarity;
        public Button viewBtn;

        public MyViewHolder(@NonNull View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.date);
            time = (TextView) view.findViewById(R.id.time);
            hour_rate = (TextView) view.findViewById(R.id.hour_rate);
            clinic_name = (TextView) view.findViewById(R.id.clinic_name);
            address = (TextView) view.findViewById(R.id.address);
            similarity = (TextView) view.findViewById(R.id.similarity);
            viewBtn = (Button) view.findViewById(R.id.viewBtn);
        }
    }

    public void setMyList(ArrayList<JobPost> list) {
        this.myList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        jobPost = myList.get(position);

        String addressStr = jobPost.getClinic().getAddress() + ", " + jobPost.getClinic().getPostalCode();
        String hourRateStr = "$" + jobPost.getRatePerHour().toString() + "/HR";
        if (jobPost.getSimilarity() != 0.0) {
            int similarity = (int) (jobPost.getSimilarity() * 100);
            String similarityStr = similarity + "% match";
            holder.similarity.setText(similarityStr);
        } else {
            holder.similarity.setVisibility(GONE);
        }
        try {
            String timeStr = DatetimeParser.parseTime(jobPost.getStartDateTime()) + " - " + DatetimeParser.parseTime(jobPost.getEndDateTime());
            holder.time.setText(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            holder.date.setText(DatetimeParser.parseDate(jobPost.getStartDateTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.hour_rate.setText(hourRateStr);
        holder.clinic_name.setText(jobPost.getClinic().getName());
        holder.address.setText(addressStr);
        holder.viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, JobDetailActivity.class);
                int itemId = (int) jobPost.getId();
                intent.putExtra("itemId", itemId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (myList != null) {
            return myList.size();
        }
        return 0;
    }
}
