package com.example.billsplit.ui.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billsplit.R;
import com.example.billsplit.data.model.Group;
import com.example.billsplit.ui.common.BottomNavFragment;
import com.example.billsplit.util.Money;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GroupsDashboardFragment extends BottomNavFragment {

    private GroupsDashboardViewModel viewModel;
    private GroupsAdapter adapter;

    private View contentGroup;
    private View emptyGroup;
    private View errorGroup;
    private View loadingGroup;
    private TextView totalBalanceText;
    private TextView youOweChip;
    private TextView youAreOwedChip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_groups_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(GroupsDashboardViewModel.class);

        contentGroup = view.findViewById(R.id.contentGroup);
        emptyGroup = view.findViewById(R.id.emptyGroup);
        errorGroup = view.findViewById(R.id.errorGroup);
        loadingGroup = view.findViewById(R.id.loadingGroup);
        totalBalanceText = view.findViewById(R.id.totalBalanceText);
        youOweChip = view.findViewById(R.id.youOweChip);
        youAreOwedChip = view.findViewById(R.id.youAreOwedChip);

        RecyclerView recyclerView = view.findViewById(R.id.groupsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new GroupsAdapter(this::openGroup);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addFab = view.findViewById(R.id.addFab);
        addFab.setOnClickListener(v -> showCreateGroup());
        view.findViewById(R.id.emptyCreateGroupButton).setOnClickListener(v -> showCreateGroup());
        view.findViewById(R.id.retryButton).setOnClickListener(v -> viewModel.load());
        view.findViewById(R.id.notificationsIcon).setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_global_notificationsFragment));

        viewModel.getState().observe(getViewLifecycleOwner(), this::renderState);
        viewModel.getGroups().observe(getViewLifecycleOwner(), groups -> {
            if (viewModel.getMemberCounts().getValue() != null) {
                adapter.submitList(groups, viewModel.getMemberCounts().getValue());
            }
        });
        viewModel.getMemberCounts().observe(getViewLifecycleOwner(), counts -> {
            if (viewModel.getGroups().getValue() != null) {
                adapter.submitList(viewModel.getGroups().getValue(), counts);
            }
        });
        viewModel.getSummary().observe(getViewLifecycleOwner(), this::renderSummary);

        setupBottomNav(view);
        viewModel.load();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.load();
        }
    }

    private void renderState(GroupsDashboardViewModel.UiState state) {
        contentGroup.setVisibility(state == GroupsDashboardViewModel.UiState.SUCCESS
                || state == GroupsDashboardViewModel.UiState.EMPTY ? View.VISIBLE : View.GONE);
        emptyGroup.setVisibility(state == GroupsDashboardViewModel.UiState.EMPTY ? View.VISIBLE : View.GONE);
        errorGroup.setVisibility(state == GroupsDashboardViewModel.UiState.ERROR ? View.VISIBLE : View.GONE);
        loadingGroup.setVisibility(state == GroupsDashboardViewModel.UiState.LOADING ? View.VISIBLE : View.GONE);
    }

    private void renderSummary(com.example.billsplit.data.repository.GroupRepository.DashboardSummary summary) {
        double net = summary.getNet();
        totalBalanceText.setText((net >= 0 ? "+" : "-") + Money.format(net));
        youOweChip.setText(getString(R.string.you_owe_amount, Money.format(summary.youOwe)));
        youAreOwedChip.setText(getString(R.string.owed_amount, Money.format(summary.youAreOwed)));
    }

    private void openGroup(Group group) {
        Bundle args = new Bundle();
        args.putString("groupId", group.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_global_groupDetailFragment, args);
    }

    private void showCreateGroup() {
        new com.example.billsplit.ui.groups.CreateGroupBottomSheetFragment()
                .show(getParentFragmentManager(), "create_group");
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_groups;
    }
}
