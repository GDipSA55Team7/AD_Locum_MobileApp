package sg.nus.iss.team7.locum;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

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

    ArrayList<JobPost> responseList = new ArrayList<>();
    private ShimmerFrameLayout shimmerFrameLayout;
    private SwipeRefreshLayout swipeContainer;
    private JobSearchAdapter adapter;

    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_job_search, container, false);

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
                    adapter.setMyList(responseList);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeContainer.setRefreshing(false);
                    adapter.notifyDataSetChanged();

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
        getOpenJobs(adapter);
    }
}