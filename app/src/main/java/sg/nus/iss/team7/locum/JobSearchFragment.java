package sg.nus.iss.team7.locum;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import sg.nus.iss.team7.locum.Adapter.JobSearchAdapter;
import sg.nus.iss.team7.locum.Interface.RecyclerViewInterface;

public class JobSearchFragment extends Fragment implements RecyclerViewInterface{

    RecyclerView recyclerView;

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_job_search);
//
//        recyclerView = findViewById(R.id.jobRecyclerView);
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerView.setLayoutManager(linearLayoutManager);
//
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
//                linearLayoutManager.getOrientation());
//        recyclerView.addItemDecoration(dividerItemDecoration);
//
//        JobSearchAdapter adapter = new JobSearchAdapter(this, this);
//
//        recyclerView.setAdapter(adapter);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_job_search, container, false);

        recyclerView = view.findViewById(R.id.jobRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        JobSearchAdapter adapter = new JobSearchAdapter(recyclerView.getContext(), this);

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(int position) {
        // TODO: add the intent
        Toast.makeText(recyclerView.getContext(), "clicked item " + position,Toast.LENGTH_SHORT).show();
    }
}