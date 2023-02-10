package sg.nus.iss.team7.locum;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sg.nus.iss.team7.locum.APICommunication.ApiMethods;
import sg.nus.iss.team7.locum.APICommunication.RetroFitClient;
import sg.nus.iss.team7.locum.Adapter.HomeRecommendedAdapter;
import sg.nus.iss.team7.locum.Interface.RecyclerViewInterface;
import sg.nus.iss.team7.locum.Model.JobPost;
import sg.nus.iss.team7.locum.Utilities.JsonFieldParser;

public class HomeFragment extends Fragment {

    private ArrayList<JobPost> responseListRec = new ArrayList<>();
    private ArrayList<JobPost> responseListNext = new ArrayList<>();
    private ShimmerFrameLayout shimmerFrameLayoutRec;
    private ShimmerFrameLayout shimmerFrameLayoutNext;
    private HomeRecommendedAdapter recAdapter;
    private HomeRecommendedAdapter nextAdapter;
    private TextView emptyView;
    private TextView emptyViewNext;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        emptyView = (TextView) view.findViewById(R.id.empty_view1);
        emptyViewNext = (TextView) view.findViewById(R.id.empty_view2);

        // Recommended job recycler
        // Shimmer load effect
        shimmerFrameLayoutRec = view.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayoutRec.startShimmer();

        // Set up recycler view
        recyclerView = view.findViewById(R.id.recRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // Load object from API to recycler view
        recAdapter = new HomeRecommendedAdapter(recyclerView.getContext());
        getRecommendedJobs(recAdapter);
        recyclerView.setAdapter(recAdapter);

        // Next job recycler
        // Shimmer load effect
        shimmerFrameLayoutNext = view.findViewById(R.id.shimmer_view_container2);
        shimmerFrameLayoutNext.startShimmer();

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
                    responseListRec = response.body();
                    if (responseListRec == null) {
                        shimmerFrameLayoutRec.stopShimmer();
                        shimmerFrameLayoutRec.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        responseListRec = responseListRec.stream()
                                .sorted(Comparator.comparingDouble(JobPost::getSimilarity).reversed())
                                .limit(3)
                                .collect(Collectors.toCollection(ArrayList::new));
                        adapter.setMyList(responseListRec);
                        shimmerFrameLayoutRec.stopShimmer();
                        shimmerFrameLayoutRec.setVisibility(View.GONE);
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
                    responseListNext = response.body();
                    if (responseListNext == null) {
                        shimmerFrameLayoutNext.stopShimmer();
                        shimmerFrameLayoutNext.setVisibility(View.GONE);
                        emptyViewNext.setVisibility(View.VISIBLE);
                        recyclerViewNext.setVisibility(View.GONE);
                    } else {
                        responseListNext = responseListNext.stream()
                                .filter(jobPost -> LocalDateTime.parse(jobPost.getStartDateTime(), formatter).isAfter(LocalDateTime.now()))
                                .sorted(Comparator.comparing(jobPost -> LocalDateTime.parse(jobPost.getStartDateTime(), formatter)))
                                .limit(1)
                                .collect(Collectors.toCollection(ArrayList::new));
                        nextAdapter.setMyList(responseListNext);
                        shimmerFrameLayoutNext.stopShimmer();
                        shimmerFrameLayoutNext.setVisibility(View.GONE);
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

    public void onResume () {
        super.onResume();
        getRecommendedJobs(recAdapter);
    }
}