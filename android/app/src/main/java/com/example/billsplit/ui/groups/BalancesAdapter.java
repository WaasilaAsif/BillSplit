package com.example.billsplit.ui.groups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billsplit.R;
import com.example.billsplit.data.model.MemberBalance;
import com.example.billsplit.local.TokenManager;
import com.example.billsplit.util.Money;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BalancesAdapter extends RecyclerView.Adapter<BalancesAdapter.BalanceViewHolder> {

    private final List<MemberBalance> entries = new ArrayList<>();

    public void submitList(List<MemberBalance> balances) {
        entries.clear();
        entries.addAll(balances);
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
        holder.bind(entries.get(position));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class BalanceViewHolder extends RecyclerView.ViewHolder {
        private final TextView initial;
        private final TextView name;
        private final TextView amount;

        BalanceViewHolder(@NonNull View itemView) {
            super(itemView);
            initial = itemView.findViewById(R.id.balanceInitial);
            name = itemView.findViewById(R.id.balanceName);
            amount = itemView.findViewById(R.id.balanceAmount);
        }

        void bind(MemberBalance entry) {
            String currentUserId = TokenManager.getInstance().getCurrentUserId();
            boolean isCurrentUser = currentUserId != null && currentUserId.equals(entry.getUserId());
            String displayName = isCurrentUser ? "You" : entry.getUsername();
            name.setText(displayName);
            initial.setText(displayName.isEmpty() ? "" : displayName.substring(0, 1).toUpperCase(Locale.US));

            double balance = entry.getBalance();
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
