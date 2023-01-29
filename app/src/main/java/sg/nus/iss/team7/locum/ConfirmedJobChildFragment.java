package sg.nus.iss.team7.locum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import sg.nus.iss.team7.locum.Adapter.JobSearchAdapter;
import sg.nus.iss.team7.locum.Adapter.MyConfirmedJobAdapter;
import sg.nus.iss.team7.locum.Interface.RecyclerViewInterface;

public class ConfirmedJobChildFragment extends Fragment implements RecyclerViewInterface{

    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.child_fragment_confirmed, container, false);

        recyclerView = view.findViewById(R.id.myConfirmedJobRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        MyConfirmedJobAdapter adapter = new MyConfirmedJobAdapter(recyclerView.getContext(), this);

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(recyclerView.getContext(), "clicked item " + position,Toast.LENGTH_SHORT).show();
    }
}