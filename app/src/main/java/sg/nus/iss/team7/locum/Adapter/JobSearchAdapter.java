package sg.nus.iss.team7.locum.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;

import sg.nus.iss.team7.locum.Model.JobPost;
import sg.nus.iss.team7.locum.R;
import sg.nus.iss.team7.locum.Interface.RecyclerViewInterface;
import sg.nus.iss.team7.locum.Utilities.DatetimeParser;

public class JobSearchAdapter extends RecyclerView.Adapter<JobSearchAdapter.MyViewHolder>{
    private final RecyclerViewInterface recyclerViewInterface;

    Context context;
    ArrayList<JobPost> myList;

    public JobSearchAdapter(Context context, RecyclerViewInterface recyclerViewInterface) {
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date, full_rate, hour_rate, time_start, time_end, job_name, clinic_name, address;

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

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        //int pos = getAbsoluteAdapterPosition();
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }

    public void setMyList(ArrayList<JobPost> list) {
        this.myList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_search_list_item, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(view, recyclerViewInterface);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        JobPost jobPost = myList.get(position);

        String addressStr = jobPost.getClinic().getAddress() + ", " + jobPost.getClinic().getPostalCode();
        String hourRateStr = "$" + jobPost.getRatePerHour().toString() + "/HR";
        String fullRateStr = "$" + jobPost.getTotalRate().toString();

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
        holder.job_name.setText(jobPost.getDescription());
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
}
