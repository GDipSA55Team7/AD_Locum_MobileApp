package sg.nus.iss.team7.locum;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sg.nus.iss.team7.locum.APICommunication.ApiMethods;
import sg.nus.iss.team7.locum.APICommunication.RetroFitClient;
import sg.nus.iss.team7.locum.Adapter.NotificationsAdapter;
import sg.nus.iss.team7.locum.Model.Notification;
import sg.nus.iss.team7.locum.Utilities.JsonFieldParser;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private ArrayList<Notification> responseList = new ArrayList<Notification>();
    private TextView emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notfications_inbox, container, false);

        emptyView = view.findViewById(R.id.NotificationsEmpty);

        // Set up recycler view
        recyclerView = view.findViewById(R.id.NotificationRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // Add dividers to recycler view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        Drawable dividerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.divider);
        InsetDrawable insetDivider = new InsetDrawable(dividerDrawable, 40, 0, 40, 0);
        dividerItemDecoration.setDrawable(insetDivider);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Load object from API to recycler view
        adapter = new NotificationsAdapter(recyclerView.getContext());
        getNotifications(adapter);
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void getNotifications(NotificationsAdapter adapter) {

        // API call
        Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
        ApiMethods api = retrofit.create(ApiMethods.class);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("FL_Shared_Pref", MODE_PRIVATE);
        String userDetails = sharedPref.getString("FL_Details", "no value");

        String id = JsonFieldParser.getField(userDetails, "id");

        Call<ArrayList<Notification>> call = api.getNotifications(Integer.parseInt(id));

        call.enqueue(new Callback<ArrayList<Notification>>() {
            @Override
            public void onResponse(Call<ArrayList<Notification>> call, Response<ArrayList<Notification>> response) {
                if (response.isSuccessful()) {
                    responseList = response.body();
                    if (responseList == null) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        adapter.setMyList(responseList);
                    }
                } else if (response.code() == 500) {
                    emptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Notification>> call, Throwable t) {
                t.printStackTrace();
                emptyView.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "error getting notification list", Toast.LENGTH_SHORT).show();
            }
        });
    }
}