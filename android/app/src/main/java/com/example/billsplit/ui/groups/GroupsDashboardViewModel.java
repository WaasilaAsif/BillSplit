package com.example.billsplit.ui.groups;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.billsplit.data.model.Group;
import com.example.billsplit.data.repository.ApiCallback;
import com.example.billsplit.data.repository.GroupRepository;

import java.util.List;

public class GroupsDashboardViewModel extends ViewModel {

    public enum UiState { LOADING, EMPTY, ERROR, SUCCESS }

    private final GroupRepository groupRepository = new GroupRepository();

    private final MutableLiveData<UiState> state = new MutableLiveData<>();
    private final MutableLiveData<List<Group>> groups = new MutableLiveData<>();
    private final MutableLiveData<GroupRepository.DashboardSummary> summary = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<UiState> getState() {
        return state;
    }

    public LiveData<List<Group>> getGroups() {
        return groups;
    }

    public LiveData<GroupRepository.DashboardSummary> getSummary() {
        return summary;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void load() {
        state.setValue(UiState.LOADING);
        groupRepository.getGroups(new ApiCallback<List<Group>>() {
            @Override
            public void onSuccess(List<Group> result) {
                groups.setValue(result);
                summary.setValue(computeSummary(result));
                state.setValue(result.isEmpty() ? UiState.EMPTY : UiState.SUCCESS);
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
                state.setValue(UiState.ERROR);
            }
        });
    }

    private GroupRepository.DashboardSummary computeSummary(List<Group> groups) {
        double youOwe = 0;
        double youAreOwed = 0;
        for (Group g : groups) {
            double balance = g.getCurrentUserBalance();
            if (balance < 0) youOwe += -balance;
            else youAreOwed += balance;
        }
        return new GroupRepository.DashboardSummary(youOwe, youAreOwed);
    }
}
