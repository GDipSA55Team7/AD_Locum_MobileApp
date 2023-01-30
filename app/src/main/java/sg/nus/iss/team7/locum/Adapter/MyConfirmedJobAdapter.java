package sg.nus.iss.team7.locum.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sg.nus.iss.team7.locum.Interface.CancelButtonInterface;
import sg.nus.iss.team7.locum.Interface.RecyclerViewInterface;
import sg.nus.iss.team7.locum.R;

public class MyConfirmedJobAdapter extends RecyclerView.Adapter<MyConfirmedJobAdapter.MyViewHolder>{
    private final RecyclerViewInterface recyclerViewInterface;
    private CancelButtonInterface buttonInterface;
    Context context;
    List<String> myList;

    public MyConfirmedJobAdapter(Context context, RecyclerViewInterface recyclerViewInterface) {
        this.recyclerViewInterface = recyclerViewInterface;
    }


    public void buttonSetOnclick(CancelButtonInterface buttonInterface){
        this.buttonInterface = buttonInterface;
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date, full_rate, hour_rate, time_start, time_end, job_name, clinic_name, address;
        public Button cancelBtn;

        public MyViewHolder(@NonNull View view, RecyclerViewInterface recyclerViewInterface, CancelButtonInterface buttonInterface) {
            super(view);
            date = (TextView) view.findViewById(R.id.date);
            full_rate = (TextView) view.findViewById(R.id.full_rate);
            hour_rate = (TextView) view.findViewById(R.id.hour_rate);
            time_start = (TextView) view.findViewById(R.id.time_start);
            time_end = (TextView) view.findViewById(R.id.time_end);
            job_name = (TextView) view.findViewById(R.id.job_name);
            clinic_name = (TextView) view.findViewById(R.id.clinic_name);
            address = (TextView) view.findViewById(R.id.address);
            cancelBtn= view.findViewById(R.id.cancelBtn);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAbsoluteAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(buttonInterface != null){
                        int position = getAbsoluteAdapterPosition();

                        if(position != RecyclerView.NO_POSITION) {
                            buttonInterface.onButtonClick(position);
                        }
                    }
                }
            });

        }
    }

    public void MyAdapter(List<String> list) {
        this.myList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_job_confirmed_list_item, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(view, recyclerViewInterface, buttonInterface);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 10;
    }




}
