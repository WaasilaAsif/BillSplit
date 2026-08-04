package com.example.billsplit.ui.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billsplit.R;
import com.example.billsplit.data.model.Expense;
import com.example.billsplit.local.AppDataStore;
import com.example.billsplit.util.Money;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Reuses item_expense_row.xml, repurposed for the Friend Detail "expenses together" list. */
public class SharedExpensesAdapter extends RecyclerView.Adapter<SharedExpensesAdapter.ViewHolder> {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d", Locale.US);

    private final List<Expense> expenses = new ArrayList<>();
    private final AppDataStore store = AppDataStore.getInstance();

    public void submitList(List<Expense> newExpenses) {
        expenses.clear();
        expenses.addAll(newExpenses);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.description.setText(expense.getDescription());

        String groupName = "";
        if (expense.getGroupId() != null) {
            com.example.billsplit.data.model.Group g = store.getGroupById(expense.getGroupId());
            if (g != null) groupName = g.getName() + " · ";
        }
        String date = DATE_FORMAT.format(Instant.parse(expense.getCreatedAt()).atZone(ZoneOffset.UTC));
        holder.subtext.setText(groupName + date);

        holder.amount.setText(Money.format(expense.getAmount()));
        holder.amount.setTextColor(holder.itemView.getResources().getColor(R.color.color_error, null));
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView description;
        final TextView subtext;
        final TextView amount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.expenseDescription);
            subtext = itemView.findViewById(R.id.expensePaidBy);
            amount = itemView.findViewById(R.id.expenseShareInfo);
        }
    }
}
