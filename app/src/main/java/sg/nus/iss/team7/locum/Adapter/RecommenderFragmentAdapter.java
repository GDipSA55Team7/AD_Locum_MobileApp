package sg.nus.iss.team7.locum.Adapter;

import static androidx.core.content.ContextCompat.getColorStateList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;

import sg.nus.iss.team7.locum.Interface.RecyclerViewInterface;
import sg.nus.iss.team7.locum.Model.JobPost;
import sg.nus.iss.team7.locum.R;
import sg.nus.iss.team7.locum.Utilities.DatetimeParser;

public class RecommenderFragmentAdapter extends RecyclerView.Adapter<RecommenderFragmentAdapter.MyViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;

    Context context;
    ArrayList<JobPost> myList;
    JobPost jobPost;

    public RecommenderFragmentAdapter(Context context, RecyclerViewInterface recyclerViewInterface) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.context = context;
    }

    public void setMyList(ArrayList<JobPost> list) {
        this.myList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommended_list_item, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(view, recyclerViewInterface);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        jobPost = myList.get(position);

        String addressStr = jobPost.getClinic().getAddress() + ", " + jobPost.getClinic().getPostalCode();
        String hourRateStr = "$" + jobPost.getRatePerHour().toString() + "/HR";
        String fullRateStr = "$" + jobPost.getTotalRate().toString();


        int similarity = (int) (jobPost.getSimilarity() * 100);
        String similarityStr = similarity + "% match";
        holder.similarity.setText(similarityStr);

        try {
            holder.date.setText(DatetimeParser.parseDate(jobPost.getStartDateTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            holder.time_start.setText(DatetimeParser.parseTime(jobPost.getStartDateTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            holder.time_end.setText(DatetimeParser.parseTime(jobPost.getEndDateTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.hour_rate.setText(hourRateStr);
        holder.clinic_name.setText(jobPost.getClinic().getName());
        holder.full_rate.setText(fullRateStr);
        holder.address.setText(addressStr);
        holder.job_name.setText(jobPost.getTitle());
        setStatusBar(holder);
    }

    @Override
    public int getItemCount() {
        if (myList != null) {
            return myList.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return myList.get(position).getId();
    }

    private void setStatusBar(@NonNull MyViewHolder holder) {

        if (jobPost.getStatus().equalsIgnoreCase("PENDING_CONFIRMATION_BY_CLINIC")) {
            holder.status.setText("APPLIED");
            holder.status.setBackgroundTintList(getColorStateList(context, R.color.status_mid));
        } else if (jobPost.getStatus().equalsIgnoreCase("OPEN")) {
            holder.status.setText("OPEN");
            holder.status.setBackgroundTintList(getColorStateList(context, R.color.status_green));
        } else if (jobPost.getStatus().equalsIgnoreCase("ACCEPTED")) {
            holder.status.setText("ACCEPTED");
            holder.status.setBackgroundTintList(getColorStateList(context, R.color.darker_grey));
        } else if (jobPost.getStatus().equalsIgnoreCase("CANCELLED")) {
            holder.status.setText("CANCELLED");
            holder.status.setBackgroundTintList(getColorStateList(context, R.color.darker_grey));
        } else if (jobPost.getStatus().startsWith("COMPLETED")) {
            holder.status.setText("COMPLETED");
            holder.status.setBackgroundTintList(getColorStateList(context, R.color.darker_grey));
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date, full_rate, hour_rate, time_start, time_end, job_name, clinic_name, address, status, similarity;

        public MyViewHolder(@NonNull View view, RecyclerViewInterface recyclerViewInterface) {
            super(view);
            date = (TextView) view.findViewById(R.id.date);
            full_rate = (TextView) view.findViewById(R.id.full_rate);
            hour_rate = (TextView) view.findViewById(R.id.hour_rate);
            time_start = (TextView) view.findViewById(R.id.time_start);
            time_end = (TextView) view.findViewById(R.id.time_end);
            job_name = (TextView) view.findViewById(R.id.job_name);
            clinic_name = (TextView) view.findViewById(R.id.clinic_name);
            address = (TextView) view.findViewById(R.id.address);
            status = (TextView) view.findViewById(R.id.status);
            similarity = (TextView) view.findViewById(R.id.similarity2);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
