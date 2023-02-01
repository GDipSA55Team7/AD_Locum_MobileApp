package sg.nus.iss.team7.locum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import sg.nus.iss.team7.locum.Adapter.FmPagerAdapter;
import sg.nus.iss.team7.locum.Interface.RecyclerViewInterface;

public class MyJobsFragment extends Fragment {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FmPagerAdapter pagerAdapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] titles = new String[]{"APPLIED","CONFIRMED","HISTORY"};



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_my_jobs, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.tablayout);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        if(fragments.isEmpty()) {
            fragments.add(new AppliedJobChildFragment());
            fragments.add(new ConfirmedJobChildFragment());
            fragments.add(new HistoryJobChildFragment());
        }

        for(int i=0;i<titles.length;i++){
            tabLayout.addTab(tabLayout.newTab());
        }

        tabLayout.setupWithViewPager(viewPager,false);
        pagerAdapter = new FmPagerAdapter(fragments,getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        for(int i=0;i<titles.length;i++){
            tabLayout.getTabAt(i).setText(titles[i]);
        }
        return view;
    }



}