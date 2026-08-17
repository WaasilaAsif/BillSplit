package com.example.billsplit.ui.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billsplit.R;
import com.example.billsplit.data.model.Expense;
import com.example.billsplit.data.model.Group;
import com.example.billsplit.util.Money;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDetailFragment extends Fragment {

    private static final String ARG_GROUP_ID = "groupId";

    private enum Tab { EXPENSES, BALANCES, TOTALS }

    private GroupDetailViewModel viewModel;
    private ExpenseListAdapter expenseAdapter;
    private BalancesAdapter balancesAdapter;

    private TextView groupNameText;
    private TextView groupSpendText;
    private TextView balanceBanner;
    private TextView tabExpenses;
    private TextView tabBalances;
    private TextView tabTotals;
    private RecyclerView expensesRecyclerView;
    private View emptyExpensesGroup;
    private RecyclerView balancesRecyclerView;
    private View totalsGroup;
    private TextView totalsSpendText;
    private TextView totalsPaidText;
    private TextView totalsOwedText;

    private Tab currentTab = Tab.EXPENSES;
    private boolean hasExpenses = false;
    private Map<String, String> memberNames = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(GroupDetailViewModel.class);

        groupNameText = view.findViewById(R.id.groupNameText);
        groupSpendText = view.findViewById(R.id.groupSpendText);
        balanceBanner = view.findViewById(R.id.balanceBanner);
        tabExpenses = view.findViewById(R.id.tabExpenses);
        tabBalances = view.findViewById(R.id.tabBalances);
        tabTotals = view.findViewById(R.id.tabTotals);
        expensesRecyclerView = view.findViewById(R.id.expensesRecyclerView);
        emptyExpensesGroup = view.findViewById(R.id.emptyExpensesGroup);
        balancesRecyclerView = view.findViewById(R.id.balancesRecyclerView);
        totalsGroup = view.findViewById(R.id.totalsGroup);
        totalsSpendText = view.findViewById(R.id.totalsSpendText);
        totalsPaidText = view.findViewById(R.id.totalsPaidText);
        totalsOwedText = view.findViewById(R.id.totalsOwedText);

        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        expenseAdapter = new ExpenseListAdapter(this::openExpense);
        expensesRecyclerView.setAdapter(expenseAdapter);

        balancesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        balancesAdapter = new BalancesAdapter();
        balancesRecyclerView.setAdapter(balancesAdapter);

        view.findViewById(R.id.backButton).setOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());
        view.findViewById(R.id.moreButton).setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString(ARG_GROUP_ID, viewModel.getGroupId());
            Navigation.findNavController(view).navigate(R.id.action_global_addGroupMemberFragment, args);
        });

        tabExpenses.setOnClickListener(v -> selectTab(Tab.EXPENSES));
        tabBalances.setOnClickListener(v -> selectTab(Tab.BALANCES));
        tabTotals.setOnClickListener(v -> selectTab(Tab.TOTALS));

        FloatingActionButton addExpenseFab = view.findViewById(R.id.addExpenseFab);
        addExpenseFab.setOnClickListener(v -> openAddExpense());
        view.findViewById(R.id.emptyAddExpenseButton).setOnClickListener(v -> openAddExpense());

        viewModel.getGroup().observe(getViewLifecycleOwner(), this::renderGroup);
        viewModel.getMemberNames().observe(getViewLifecycleOwner(), names -> {
            memberNames = names;
            List<Object> items = viewModel.getExpenseItems().getValue();
            if (items != null) expenseAdapter.submitList(items, memberNames);
        });
        viewModel.getExpenseItems().observe(getViewLifecycleOwner(), items -> {
            expenseAdapter.submitList(items, memberNames);
            hasExpenses = !items.isEmpty();
            updateTabVisibility();
        });
        viewModel.getMemberBalances().observe(getViewLifecycleOwner(), balancesAdapter::submitList);
        viewModel.getBalanceBannerText().observe(getViewLifecycleOwner(), this::renderBanner);
        viewModel.getGroupSpend().observe(getViewLifecycleOwner(), spend ->
                totalsSpendText.setText(getString(R.string.total_group_spend, Money.format(spend))));
        viewModel.getYourPaidTotal().observe(getViewLifecycleOwner(), paid ->
                totalsPaidText.setText(getString(R.string.you_paid_total, Money.format(paid))));
        viewModel.getYourShareTotal().observe(getViewLifecycleOwner(), share ->
                totalsOwedText.setText(getString(R.string.your_share_total, Money.format(share))));
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
        });

        selectTab(Tab.EXPENSES);
        loadGroup();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadGroup();
    }

    private void loadGroup() {
        Bundle args = getArguments();
        String groupId = args != null ? args.getString(ARG_GROUP_ID) : null;
        if (groupId != null) {
            viewModel.load(groupId);
        }
    }

    private void renderGroup(Group group) {
        if (group == null) return;
        groupNameText.setText(group.getName());
        groupSpendText.setText(getString(R.string.group_spend,
                Money.format(viewModel.getGroupSpend().getValue() != null
                        ? viewModel.getGroupSpend().getValue() : 0)));
    }

    private void renderBanner(String text) {
        if (text == null) {
            balanceBanner.setVisibility(View.GONE);
            return;
        }
        balanceBanner.setVisibility(View.VISIBLE);
        balanceBanner.setText(text);
        Integer colorRes = viewModel.getBalanceBannerColor().getValue();
        boolean isError = colorRes != null && colorRes == R.color.color_error;
        balanceBanner.setBackgroundTintList(getResources().getColorStateList(
                isError ? R.color.color_error_container : R.color.color_primary_container, null));
        balanceBanner.setTextColor(getResources().getColor(
                isError ? R.color.color_error : R.color.primary, null));
    }

    private void selectTab(Tab tab) {
        currentTab = tab;
        tabExpenses.setTextColor(getResources().getColor(
                tab == Tab.EXPENSES ? R.color.primary : R.color.text_secondary, null));
        tabBalances.setTextColor(getResources().getColor(
                tab == Tab.BALANCES ? R.color.primary : R.color.text_secondary, null));
        tabTotals.setTextColor(getResources().getColor(
                tab == Tab.TOTALS ? R.color.primary : R.color.text_secondary, null));
        updateTabVisibility();
    }

    private void updateTabVisibility() {
        boolean showExpensesEmpty = currentTab == Tab.EXPENSES && !hasExpenses;
        expensesRecyclerView.setVisibility(currentTab == Tab.EXPENSES && hasExpenses ? View.VISIBLE : View.GONE);
        emptyExpensesGroup.setVisibility(showExpensesEmpty ? View.VISIBLE : View.GONE);
        balancesRecyclerView.setVisibility(currentTab == Tab.BALANCES ? View.VISIBLE : View.GONE);
        totalsGroup.setVisibility(currentTab == Tab.TOTALS ? View.VISIBLE : View.GONE);
    }

    private void openExpense(Expense expense) {
        Bundle args = new Bundle();
        args.putString("expenseId", expense.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_global_expenseDetailFragment, args);
    }

    private void openAddExpense() {
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_ID, viewModel.getGroupId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_global_expenseFormFragment, args);
    }
}
