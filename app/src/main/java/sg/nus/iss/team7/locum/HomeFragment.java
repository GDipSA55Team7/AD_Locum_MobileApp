package sg.nus.iss.team7.locum;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sg.nus.iss.team7.locum.APICommunication.ApiMethods;
import sg.nus.iss.team7.locum.APICommunication.RetroFitClient;
import sg.nus.iss.team7.locum.Adapter.HomeRecommendedAdapter;
import sg.nus.iss.team7.locum.Model.JobPost;
import sg.nus.iss.team7.locum.Utilities.JsonFieldParser;

public class HomeFragment extends Fragment {

    private ArrayList<JobPost> responseListRec, responseListNext, responseListOverview = new ArrayList<>();
    private ShimmerFrameLayout shimmerFrameLayoutRec, shimmerFrameLayoutNext;
    private HomeRecommendedAdapter recAdapter, nextAdapter;
    private TextView emptyView, emptyViewNext, scheduledCount, confirmationCount, completedCount, paymentCount;
    private View confirmedJobs, pendingConfirmationJobs, pendingPaymentJobs, completedJobs;
    private RecyclerView recyclerView, recyclerViewNext;
    private Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
    private ApiMethods api = retrofit.create(ApiMethods.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        emptyView = (TextView) view.findViewById(R.id.empty_view1);
        emptyViewNext = (TextView) view.findViewById(R.id.empty_view2);
        scheduledCount = (TextView) view.findViewById(R.id.scheduled);
        confirmationCount = (TextView) view.findViewById(R.id.pendingConfirmation);
        completedCount = (TextView) view.findViewById(R.id.completed);
        paymentCount = (TextView) view.findViewById(R.id.pendingPayment);
        confirmedJobs = (View) view.findViewById(R.id.confirmedJobs);
        pendingConfirmationJobs = (View) view.findViewById(R.id.pendingConfirmationJobs);
        completedJobs = (View) view.findViewById(R.id.completedJob);
        pendingPaymentJobs = (View) view.findViewById(R.id.pendingPaymentJobs);

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

        // Set overview counts
        getOverview();

        confirmedJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new ConfirmedJobChildFragment(), null)
                        .addToBackStack(null).commit();
            }
        });

        pendingConfirmationJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new AppliedJobChildFragment(), null)
                        .addToBackStack(null).commit();
            }
        });

        completedJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new HistoryJobChildFragment(), null)
                        .addToBackStack(null).commit();
            }
        });

        pendingPaymentJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new HistoryJobChildFragment(), null)
                        .addToBackStack(null).commit();
            }
        });

        return view;
    }

    public void getRecommendedJobs(HomeRecommendedAdapter adapter) {

        // API call
        String id = getUserIdString();

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
                Toast.makeText(getContext(), "error getting job list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getNextJob(HomeRecommendedAdapter adapter) {

        // API call
        String id = getUserIdString();

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
                        if (responseListNext.isEmpty()) { //if there is response but the date is not after current date
                            emptyViewNext.setVisibility(View.VISIBLE);
                        }
                        nextAdapter.setMyList(responseListNext);
                        shimmerFrameLayoutNext.stopShimmer();
                        shimmerFrameLayoutNext.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<JobPost>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "error getting job list", Toast.LENGTH_SHORT);
            }
        });
    }

    public void getOverview() {

        // API call
        String id = getUserIdString();

        Call<ArrayList<JobPost>> call = api.getJobsByUserId(Integer.parseInt(id));

        call.enqueue(new Callback<ArrayList<JobPost>>() {
            @Override
            public void onResponse(Call<ArrayList<JobPost>> call, Response<ArrayList<JobPost>> response) {
                if (response.isSuccessful()) {
                    responseListOverview = response.body();
                    if (responseListOverview == null) {
                        scheduledCount.setText("-");
                        confirmationCount.setText("-");
                        completedCount.setText("-");
                        paymentCount.setText("-");
                    } else {
                        Map<String, Long> statusCounts = responseListOverview.stream()
                                .collect(Collectors.groupingBy(JobPost::getStatus, Collectors.counting()));
                        scheduledCount.setText(statusCounts.getOrDefault("ACCEPTED", 0L).toString());
                        confirmationCount.setText(statusCounts.getOrDefault("PENDING_CONFIRMATION_BY_CLINIC", 0L).toString());
                        completedCount.setText(statusCounts.getOrDefault("COMPLETED_PAYMENT_PROCESSED", 0L).toString());
                        paymentCount.setText(statusCounts.getOrDefault("COMPLETED_PENDING_PAYMENT", 0L).toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<JobPost>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "error getting job count", Toast.LENGTH_SHORT);
            }
        });
    }

    @Nullable
    private String getUserIdString() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("FL_Shared_Pref", MODE_PRIVATE);
        String userDetails = sharedPref.getString("FL_Details", "no value");

        String id = JsonFieldParser.getField(userDetails, "id");
        return id;
    }

    public void onResume() {
        super.onResume();
        getRecommendedJobs(recAdapter);
        getNextJob(nextAdapter);
    }
}