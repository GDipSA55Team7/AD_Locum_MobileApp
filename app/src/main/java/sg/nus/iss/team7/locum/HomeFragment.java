package sg.nus.iss.team7.locum;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sg.nus.iss.team7.locum.APICommunication.ApiMethods;
import sg.nus.iss.team7.locum.APICommunication.RetroFitClient;
import sg.nus.iss.team7.locum.Adapter.HomeRecommendedAdapter;
import sg.nus.iss.team7.locum.Adapter.JobSearchAdapter;
import sg.nus.iss.team7.locum.Interface.RecyclerViewInterface;
import sg.nus.iss.team7.locum.Model.JobPost;
import sg.nus.iss.team7.locum.Utilities.JsonFieldParser;

public class HomeFragment extends Fragment {

    private ArrayList<JobPost> responseList = new ArrayList<>();
    private ArrayList<JobPost> responseList2 = new ArrayList<>();
    private ShimmerFrameLayout shimmerFrameLayout;
    private ShimmerFrameLayout shimmerFrameLayout2;
    private HomeRecommendedAdapter adapter;
    private HomeRecommendedAdapter nextAdapter;
    private TextView emptyView;
    private TextView emptyView2;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewNext;
    private RecyclerViewInterface recyclerViewInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        emptyView = (TextView) view.findViewById(R.id.empty_view1);
        emptyView2 = (TextView) view.findViewById(R.id.empty_view2);

        // Recommended job recycler
        // Shimmer load effect
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();

        // Set up recycler view
        recyclerView = view.findViewById(R.id.recRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // Load object from API to recycler view
        adapter = new HomeRecommendedAdapter(recyclerView.getContext());
        getRecommendedJobs(adapter);
        recyclerView.setAdapter(adapter);

        // Next job recycler
        // Shimmer load effect
        shimmerFrameLayout2 = view.findViewById(R.id.shimmer_view_container2);
        shimmerFrameLayout2.startShimmer();

        // Set up recycler view
        recyclerViewNext = view.findViewById(R.id.nextRecyclerView);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(view.getContext());
        recyclerViewNext.setLayoutManager(linearLayoutManager2);

        // Load object from API to recycler view
        nextAdapter = new HomeRecommendedAdapter(recyclerViewNext.getContext());
        getNextJob(nextAdapter);
        recyclerViewNext.setAdapter(nextAdapter);

        return view;
    }

    public void getRecommendedJobs(HomeRecommendedAdapter adapter) {

        // API call
        Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
        ApiMethods api = retrofit.create(ApiMethods.class);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("FL_Shared_Pref", MODE_PRIVATE);
        String userDetails = sharedPref.getString("FL_Details", "no value");

        String id = JsonFieldParser.getField(userDetails, "id");

        Call<ArrayList<JobPost>> call = api.getJobRecommended(Integer.parseInt(id));

        call.enqueue(new Callback<ArrayList<JobPost>>() {
            @Override
            public void onResponse(Call<ArrayList<JobPost>> call, Response<ArrayList<JobPost>> response) {
                if (response.isSuccessful()) {
                    responseList = response.body();
                    if (responseList == null) {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        responseList = responseList.stream()
                                .sorted(Comparator.comparingDouble(JobPost::getSimilarity).reversed())
                                .limit(3)
                                .collect(Collectors.toCollection(ArrayList::new));
                        adapter.setMyList(responseList);
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<JobPost>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(),"error getting job list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getNextJob(HomeRecommendedAdapter adapter) {

        // API call
        Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
        ApiMethods api = retrofit.create(ApiMethods.class);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("FL_Shared_Pref", MODE_PRIVATE);
        String userDetails = sharedPref.getString("FL_Details", "no value");

        String id = JsonFieldParser.getField(userDetails, "id");

        Call<ArrayList<JobPost>> call = api.getJobConfirmed(Integer.parseInt(id));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        call.enqueue(new Callback<ArrayList<JobPost>>() {
            @Override
            public void onResponse(Call<ArrayList<JobPost>> call, Response<ArrayList<JobPost>> response) {
                if (response.isSuccessful()) {
                    responseList2 = response.body();
                    if (responseList2 == null) {
                        shimmerFrameLayout2.stopShimmer();
                        shimmerFrameLayout2.setVisibility(View.GONE);
                        emptyView2.setVisibility(View.VISIBLE);
                        recyclerViewNext.setVisibility(View.GONE);
                    } else {
                        responseList2 = responseList2.stream()
                                .filter(jobPost -> LocalDateTime.parse(jobPost.getStartDateTime(), formatter).isAfter(LocalDateTime.now()))
                                .sorted(Comparator.comparing(jobPost -> LocalDateTime.parse(jobPost.getStartDateTime(), formatter)))
                                .limit(1)
                                .collect(Collectors.toCollection(ArrayList::new));
                        nextAdapter.setMyList(responseList2);
                        shimmerFrameLayout2.stopShimmer();
                        shimmerFrameLayout2.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<JobPost>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(),"error getting job list", Toast.LENGTH_SHORT);
            }
        });
    }
}