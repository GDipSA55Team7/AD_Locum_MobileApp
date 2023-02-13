package sg.nus.iss.team7.locum.Adapter;

import static android.content.Context.MODE_PRIVATE;
import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sg.nus.iss.team7.locum.APICommunication.ApiMethods;
import sg.nus.iss.team7.locum.APICommunication.RetroFitClient;
import sg.nus.iss.team7.locum.JobDetailActivity;
import sg.nus.iss.team7.locum.Model.Notification;
import sg.nus.iss.team7.locum.R;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder>{

    Context context;
    ArrayList<Notification> myList;
    Notification notification;

    public NotificationsAdapter(Context context) {
        this.context = context;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView notificationTitle, notificationBody;
        public ImageView itemNotificationIndicator;

        public MyViewHolder(@NonNull View view) {
            super(view);
            notificationBody = (TextView) view.findViewById(R.id.notificationBody);
            notificationTitle = (TextView) view.findViewById(R.id.notificationTitle);
            itemNotificationIndicator = (ImageView) view.findViewById(R.id.ItemNotificationIndicator);
        }
    }

    public void setMyList(ArrayList<Notification> list) {
        this.myList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(view);

        view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add("Mark as read").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                        dismissNotifications(viewHolder);
                        return false;
                    }
                });
                menu.add("View").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                        Intent intent = new Intent(context, JobDetailActivity.class);
                        int itemId = notification.getJobId().intValue();
                        intent.putExtra("itemId", itemId);
                        context.startActivity(intent);
                        return false;
                    }
                });
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        notification = myList.get(holder.getLayoutPosition());
        holder.notificationTitle.setText(notification.getTitle());
        holder.notificationBody.setText(notification.getBody());

        if (notification.isRead()) {
            holder.itemNotificationIndicator.setVisibility(View.GONE);
        }
    }

    public void dismissNotifications(@NonNull MyViewHolder holder) {

        // API call
        Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
        ApiMethods api = retrofit.create(ApiMethods.class);

        Long id = myList.get(holder.getBindingAdapterPosition()).getId();

        Call<ArrayList<Notification>> call = api.setAsRead(Integer.parseInt(String.valueOf(id)));

        call.enqueue(new Callback<ArrayList<Notification>>() {
            @Override
            public void onResponse(Call<ArrayList<Notification>> call, Response<ArrayList<Notification>> response) {
                if (response.isSuccessful()) {
                    holder.itemNotificationIndicator.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Notification>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(context, "error updating notification status", Toast.LENGTH_SHORT).show();
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
