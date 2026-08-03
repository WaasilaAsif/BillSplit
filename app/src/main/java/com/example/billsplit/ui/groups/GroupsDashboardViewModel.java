package com.example.billsplit.ui.groups;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.billsplit.data.model.Group;
import com.example.billsplit.data.repository.GroupRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupsDashboardViewModel extends ViewModel {

    public enum UiState { LOADING, EMPTY, ERROR, SUCCESS }

    private final GroupRepository groupRepository = new GroupRepository();

    private final MutableLiveData<UiState> state = new MutableLiveData<>();
    private final MutableLiveData<List<Group>> groups = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Integer>> memberCounts = new MutableLiveData<>();
    private final MutableLiveData<GroupRepository.DashboardSummary> summary = new MutableLiveData<>();

    public LiveData<UiState> getState() {
        return state;
    }

    public LiveData<List<Group>> getGroups() {
        return groups;
    }

    public LiveData<Map<String, Integer>> getMemberCounts() {
        return memberCounts;
    }

    public LiveData<GroupRepository.DashboardSummary> getSummary() {
        return summary;
    }

    public void load() {
        List<Group> loaded = groupRepository.getGroups();
        Map<String, Integer> counts = new HashMap<>();
        for (Group g : loaded) {
            counts.put(g.getId(), groupRepository.getGroupMembers(g.getId()).size());
        }

        groups.setValue(loaded);
        memberCounts.setValue(counts);
        summary.setValue(groupRepository.getDashboardSummary());
        state.setValue(loaded.isEmpty() ? UiState.EMPTY : UiState.SUCCESS);
    }
}
