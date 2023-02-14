package sg.nus.iss.team7.locum;

import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionInflater;
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
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sg.nus.iss.team7.locum.APICommunication.ApiMethods;
import sg.nus.iss.team7.locum.APICommunication.RetroFitClient;
import sg.nus.iss.team7.locum.Adapter.JobSearchAdapter;
import sg.nus.iss.team7.locum.Interface.RecyclerViewInterface;
import sg.nus.iss.team7.locum.Model.FreeLancer;
import sg.nus.iss.team7.locum.Model.JobPost;

public class JobSearchFragment extends Fragment implements RecyclerViewInterface{

    private ArrayList<JobPost> responseList = new ArrayList<>();
    private ShimmerFrameLayout shimmerFrameLayout;
    private SwipeRefreshLayout swipeContainer;
    private JobSearchAdapter adapter;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private TextView emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_job_search, container, false);

        // Empty view if list is empty
        emptyView = view.findViewById(R.id.empty_search);

        // Set up search bar
        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        // Shimmer load effect
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();

        // Set up swipe up to reload list
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(R.color.app_main_blue);

        // Set up recycler view
        recyclerView = view.findViewById(R.id.jobRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // Add dividers to recycler view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        Drawable dividerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.divider);
        InsetDrawable insetDivider = new InsetDrawable(dividerDrawable, 40, 0, 40, 0);
        dividerItemDecoration.setDrawable(insetDivider);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Load object from API to recycler view
        adapter = new JobSearchAdapter(recyclerView.getContext(), this);
        getOpenJobs(adapter);
        recyclerView.setAdapter(adapter);

        // Set listener for swipe up to reload
        swipeContainer.setOnRefreshListener(() -> getOpenJobs(adapter));

        return view;
    }

    private void filterList(String text) {
        if (text.length() >= 3 || text.isEmpty()) {
            ArrayList<JobPost> filteredList = new ArrayList<>();
            for (JobPost jobPost : responseList) {
                if (!filteredList.contains(jobPost)) {
                    if (jobPost.getClinic().getName().toLowerCase().contains(text.toLowerCase())) {
                        filteredList.add(jobPost);
                    }
                    else if (jobPost.getDescription().toLowerCase().contains(text.toLowerCase())) {
                        filteredList.add(jobPost);
                    }
                    else if (jobPost.getClinic().getAddress().toLowerCase().contains(text.toLowerCase())) {
                        filteredList.add(jobPost);
                    }
                }
            }

            if (!filteredList.isEmpty()) {
                adapter.setMyList(filteredList);
            }
        }

    }

    @Override
    public void onItemClick(int position) {
        // load job details on item click
        Intent intent = new Intent(getContext(), JobDetailActivity.class);
        int itemId = (int) adapter.getItemId(position);
        intent.putExtra("itemId", itemId);
        startActivity(intent);
    }

    public void getOpenJobs(JobSearchAdapter adapter) {

        // API call
        Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
        ApiMethods api = retrofit.create(ApiMethods.class);

        Call<ArrayList<JobPost>> call = api.getAllOpenJobs();

        call.enqueue(new Callback<ArrayList<JobPost>>() {
            @Override
            public void onResponse(Call<ArrayList<JobPost>> call, Response<ArrayList<JobPost>> response) {
                if (response.isSuccessful()) {
                    responseList = response.body();
                    if (responseList != null) {
                        responseList = responseList.stream()
                                .sorted(Comparator.comparing(o -> LocalDateTime.parse(o.getStartDateTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))))
                                .collect(Collectors.toCollection(ArrayList::new));
                    }
                    adapter.setMyList(responseList);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeContainer.setRefreshing(false);

                    if (responseList == null || responseList.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
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

    public void onResume () {
        super.onResume();
        searchView.setQuery("", false);
        getOpenJobs(adapter);
    }
}