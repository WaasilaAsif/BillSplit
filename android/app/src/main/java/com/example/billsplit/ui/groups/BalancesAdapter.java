package com.example.billsplit.ui.groups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billsplit.R;
import com.example.billsplit.local.AppDataStore;
import com.example.billsplit.util.Money;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BalancesAdapter extends RecyclerView.Adapter<BalancesAdapter.BalanceViewHolder> {

    private final List<Map.Entry<String, Double>> entries = new ArrayList<>();
    private final AppDataStore store = AppDataStore.getInstance();

    public void submitList(Map<String, Double> balances) {
        entries.clear();
        entries.addAll(new LinkedHashMap<>(balances).entrySet());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BalanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_balance_row, parent, false);
        return new BalanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceViewHolder holder, int position) {
        Map.Entry<String, Double> entry = entries.get(position);
        holder.bind(store.getUserById(entry.getKey()), entry.getValue());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class BalanceViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView amount;

        BalanceViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.balanceName);
            amount = itemView.findViewById(R.id.balanceAmount);
        }

        void bind(com.example.billsplit.data.model.User user, double balance) {
            boolean isCurrentUser = user != null && AppDataStore.CURRENT_USER_ID.equals(user.getId());
            name.setText(isCurrentUser ? "You" : (user != null ? user.getUsername() : ""));

            int color;
            if (Math.abs(balance) < 0.01) {
                amount.setText(R.string.settled_up);
                color = itemView.getResources().getColor(R.color.text_tertiary, null);
            } else if (balance < 0) {
                amount.setText(itemView.getResources().getString(R.string.owe_amount_label, Money.format(balance)));
                color = itemView.getResources().getColor(R.color.color_error, null);
            } else {
                amount.setText(itemView.getResources().getString(R.string.owed_amount_label, Money.format(balance)));
                color = itemView.getResources().getColor(R.color.primary, null);
            }
            amount.setTextColor(color);
        }
    }
}
