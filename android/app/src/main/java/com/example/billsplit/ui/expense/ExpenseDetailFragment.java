package com.example.billsplit.ui.expense;

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

import com.example.billsplit.R;
import com.example.billsplit.data.model.Expense;
import com.example.billsplit.data.model.ExpenseSplit;
import com.example.billsplit.local.TokenManager;
import com.example.billsplit.ui.settle.SettleUpBottomSheetFragment;
import com.example.billsplit.util.Money;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ExpenseDetailFragment extends Fragment {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US);

    private ExpenseDetailViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expense_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ExpenseDetailViewModel.class);

        view.findViewById(R.id.backButton).setOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        Bundle args = getArguments();
        String expenseId = args != null ? args.getString("expenseId") : null;

        view.findViewById(R.id.editIcon).setOnClickListener(v -> {
            Bundle editArgs = new Bundle();
            editArgs.putString("expenseId", expenseId);
            Navigation.findNavController(view).navigate(R.id.action_global_expenseFormFragment, editArgs);
        });
        view.findViewById(R.id.editButton).setOnClickListener(v -> {
            Bundle editArgs = new Bundle();
            editArgs.putString("expenseId", expenseId);
            Navigation.findNavController(view).navigate(R.id.action_global_expenseFormFragment, editArgs);
        });
        view.findViewById(R.id.deleteIcon).setOnClickListener(v -> viewModel.delete());
        view.findViewById(R.id.settleUpButton).setOnClickListener(v -> {
            ExpenseDetailViewModel.SettleTarget target = viewModel.resolveSettleTarget();
            Expense expense = viewModel.getExpense().getValue();
            if (target == null || expense == null) return;
            String currentUserId = TokenManager.getInstance().getCurrentUserId();
            String otherPartyId = target.fromUserId.equals(currentUserId) ? target.toUserId : target.fromUserId;
            SettleUpBottomSheetFragment.newInstance(target.fromUserId, target.toUserId, target.amount,
                            currentGroupId(), nameFor(expense, otherPartyId))
                    .show(getParentFragmentManager(), "settle_up");
        });

        viewModel.getExpense().observe(getViewLifecycleOwner(), this::render);
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
        });
        viewModel.getDeleted().observe(getViewLifecycleOwner(), deleted -> {
            if (deleted != null && deleted) Navigation.findNavController(view).navigateUp();
        });
        viewModel.load(expenseId);
    }

    private String currentGroupId() {
        Expense e = viewModel.getExpense().getValue();
        return e != null ? e.getGroupId() : null;
    }

    private void render(Expense expense) {
        if (expense == null) return;
        View root = requireView();

        ((TextView) root.findViewById(R.id.expenseDescriptionText)).setText(expense.getDescription());
        ((TextView) root.findViewById(R.id.expenseAmountText)).setText(Money.format(expense.getAmount()));

        String userId = TokenManager.getInstance().getCurrentUserId();
        boolean youPaid = userId != null && userId.equals(expense.getPaidBy());
        String payerName = nameFor(expense, expense.getPaidBy());

        String dateLabel = DATE_FORMAT.format(Instant.parse(expense.getCreatedAt()).atZone(ZoneOffset.UTC));
        String addedBy = youPaid ? getString(R.string.added_by_you) : getString(R.string.added_by, payerName);
        ((TextView) root.findViewById(R.id.expenseMetaText)).setText(addedBy + " · " + dateLabel);

        String payerSummary = youPaid
                ? getString(R.string.you_paid, Money.format(expense.getAmount()))
                : getString(R.string.payer_paid, payerName, Money.format(expense.getAmount()));
        ((TextView) root.findViewById(R.id.payerSummaryText)).setText(payerSummary);

        String footer = youPaid ? getString(R.string.you_added_this_expense)
                : getString(R.string.added_this_expense, payerName);
        ((TextView) root.findViewById(R.id.addedByFooterText)).setText(footer);

        renderSplits(root, expense);
    }

    private void renderSplits(View root, Expense expense) {
        if (expense.getSplits() == null) return;
        ((TextView) root.findViewById(R.id.splitHeaderText)).setText(
                getString(R.string.split_equally_between, expense.getSplits().size()));

        android.widget.LinearLayout container = root.findViewById(R.id.splitsContainer);
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        String userId = TokenManager.getInstance().getCurrentUserId();

        for (ExpenseSplit split : expense.getSplits()) {
            View row = inflater.inflate(R.layout.item_balance_row, container, false);
            TextView name = row.findViewById(R.id.balanceName);
            TextView amount = row.findViewById(R.id.balanceAmount);

            boolean isPayer = split.getUserId().equals(expense.getPaidBy());
            name.setText(split.getUserId().equals(userId) ? "You" : split.getUsername());

            if (isPayer) {
                double owedBack = expense.getAmount() - split.getShareAmount();
                amount.setText(getString(R.string.owed_amount_label, Money.format(owedBack)));
                amount.setTextColor(getResources().getColor(R.color.primary, null));
            } else {
                amount.setText(getString(R.string.owe_amount_label, Money.format(split.getShareAmount())));
                amount.setTextColor(getResources().getColor(R.color.color_error, null));
            }
            container.addView(row);
        }
    }

    private String nameFor(Expense expense, String userId) {
        if (expense.getSplits() == null || userId == null) return "";
        for (ExpenseSplit split : expense.getSplits()) {
            if (userId.equals(split.getUserId())) {
                return split.getUsername();
            }
        }
        return "";
    }
}
