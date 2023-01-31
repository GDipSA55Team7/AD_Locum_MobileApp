package sg.nus.iss.team7.locum;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import sg.nus.iss.team7.locum.Adapter.JobSearchAdapter;
import sg.nus.iss.team7.locum.Adapter.MyConfirmedJobAdapter;
import sg.nus.iss.team7.locum.Interface.CancelButtonInterface;
import sg.nus.iss.team7.locum.Interface.RecyclerViewInterface;

public class ConfirmedJobChildFragment extends Fragment implements RecyclerViewInterface,CancelButtonInterface{

    RecyclerView recyclerView;
    MyConfirmedJobAdapter adapter;
    JobDetailFragment jobDetailFragment;
    Button cancelBtn;

    String alertTitle;
    String alertMsg;
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

        adapter = new MyConfirmedJobAdapter(recyclerView.getContext(), this);

        recyclerView.setAdapter(adapter);

        adapter.buttonSetOnclick(this::onButtonClick);

        return view;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), JobDetailActivity.class);
        int itemId = (int) adapter.getItemId(position);
        intent.putExtra("itemId", itemId);
        startActivity(intent);
    }
    @Override
    public void onButtonClick(int position){

        alertMsg=getString(R.string.cancelMsg);
        alertTitle=getString(R.string.cancelAlertTitle);

                AlertDialog.Builder dlg = new AlertDialog.Builder(getContext())
                        .setTitle(alertTitle)
                        .setMessage(alertMsg)
                        .setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "Cancel successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "Not cancel", Toast.LENGTH_SHORT).show();
                            }
                        });
                dlg.show();
    }

}