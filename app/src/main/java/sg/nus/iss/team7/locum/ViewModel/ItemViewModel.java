package sg.nus.iss.team7.locum.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import sg.nus.iss.team7.locum.Model.JobPost;

public class ItemViewModel extends ViewModel {
    private final MutableLiveData<JobPost> selectedItem = new MutableLiveData<JobPost>();

    public void selectItem(JobPost jobPost) {
        selectedItem.setValue(jobPost);
    }

    public LiveData<JobPost> getSelectedItem() {
        return selectedItem;
    }
}

