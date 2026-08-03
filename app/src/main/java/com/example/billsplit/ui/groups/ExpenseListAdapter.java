package com.example.billsplit.ui.groups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billsplit.R;
import com.example.billsplit.data.model.Expense;
import com.example.billsplit.data.model.ExpenseSplit;
import com.example.billsplit.data.model.User;
import com.example.billsplit.local.AppDataStore;
import com.example.billsplit.util.Money;

import java.util.ArrayList;
import java.util.List;

/**
 * Mixed list of month-header strings and Expense rows, matching the
 * design's "JULY 2026 / expense rows / JUNE 2026 / expense rows" layout.
 */
public class ExpenseListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnExpenseClickListener {
        void onExpenseClick(Expense expense);
    }

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_EXPENSE = 1;

    private final List<Object> items = new ArrayList<>();
    private final AppDataStore store = AppDataStore.getInstance();
    private final OnExpenseClickListener listener;

    public ExpenseListAdapter(OnExpenseClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Object> newItems) {
        items.clear();
        items.addAll(newItems);
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
            ((ExpenseViewHolder) holder).bind((Expense) item, store, listener);
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
        final TextView description;
        final TextView paidBy;
        final TextView shareInfo;

        ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.expenseDescription);
            paidBy = itemView.findViewById(R.id.expensePaidBy);
            shareInfo = itemView.findViewById(R.id.expenseShareInfo);
        }

        void bind(Expense expense, AppDataStore store, OnExpenseClickListener listener) {
            description.setText(expense.getDescription());

            String userId = AppDataStore.CURRENT_USER_ID;
            if (userId.equals(expense.getPaidBy())) {
                paidBy.setText(itemView.getResources().getString(R.string.paid_by_you));
            } else {
                User payer = store.getUserById(expense.getPaidBy());
                String name = payer != null ? payer.getUsername() : "";
                paidBy.setText(itemView.getResources().getString(R.string.paid_by, name));
            }

            Double ownShare = null;
            if (expense.getSplits() != null) {
                for (ExpenseSplit split : expense.getSplits()) {
                    if (userId.equals(split.getUserId())) {
                        ownShare = split.getShareAmount();
                        break;
                    }
                }
            }

            if (userId.equals(expense.getPaidBy())) {
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
