package com.example.billsplit.ui.groups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billsplit.R;
import com.example.billsplit.data.model.Expense;
import com.example.billsplit.local.TokenManager;
import com.example.billsplit.util.Money;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExpenseListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnExpenseClickListener {
        void onExpenseClick(Expense expense);
    }

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_EXPENSE = 1;

    private final List<Object> items = new ArrayList<>();
    private final OnExpenseClickListener listener;
    private Map<String, String> memberNames = java.util.Collections.emptyMap();

    public ExpenseListAdapter(OnExpenseClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Object> newItems, Map<String, String> memberNames) {
        items.clear();
        items.addAll(newItems);
        this.memberNames = memberNames;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_HEADER : TYPE_EXPENSE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(inflater.inflate(R.layout.item_section_header, parent, false));
        }
        return new ExpenseViewHolder(inflater.inflate(R.layout.item_expense_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).text.setText((String) item);
        } else {
            ((ExpenseViewHolder) holder).bind((Expense) item, memberNames, listener);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        final TextView text;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            text = (TextView) itemView;
        }
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        final TextView initial;
        final TextView description;
        final TextView paidBy;
        final TextView shareInfo;

        ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            initial = itemView.findViewById(R.id.expenseInitial);
            description = itemView.findViewById(R.id.expenseDescription);
            paidBy = itemView.findViewById(R.id.expensePaidBy);
            shareInfo = itemView.findViewById(R.id.expenseShareInfo);
        }

        void bind(Expense expense, Map<String, String> memberNames, OnExpenseClickListener listener) {
            description.setText(expense.getDescription());

            String userId = TokenManager.getInstance().getCurrentUserId();
            boolean youPaid = userId != null && userId.equals(expense.getPaidBy());
            String payerName;
            if (youPaid) {
                payerName = "You";
                paidBy.setText(itemView.getResources().getString(R.string.paid_by_you));
            } else {
                payerName = memberNames.getOrDefault(expense.getPaidBy(), "");
                paidBy.setText(itemView.getResources().getString(R.string.paid_by, payerName));
            }
            initial.setText(payerName.isEmpty() ? "" : payerName.substring(0, 1).toUpperCase(Locale.US));

            Double ownShare = expense.getYourShare();

            if (youPaid) {
                double lent = expense.getAmount() - (ownShare != null ? ownShare : 0);
                shareInfo.setText(itemView.getResources()
                        .getString(R.string.you_lent_share, Money.format(lent)));
                shareInfo.setTextColor(itemView.getResources().getColor(R.color.primary, null));
            } else if (ownShare != null) {
                shareInfo.setText(itemView.getResources()
                        .getString(R.string.you_owe_share, Money.format(ownShare)));
                shareInfo.setTextColor(itemView.getResources().getColor(R.color.color_error, null));
            } else {
                shareInfo.setText("");
            }

            itemView.setOnClickListener(v -> listener.onExpenseClick(expense));
        }
    }
}
