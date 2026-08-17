package com.example.billsplit.ui.groups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billsplit.R;
import com.example.billsplit.data.model.Group;
import com.example.billsplit.util.Money;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupViewHolder> {

    public interface OnGroupClickListener {
        void onGroupClick(Group group);
    }

    private final List<Group> groups = new ArrayList<>();
    private final OnGroupClickListener listener;

    public GroupsAdapter(OnGroupClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Group> newGroups) {
        groups.clear();
        groups.addAll(newGroups);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_row, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.bind(group, group.getMemberCount(), listener);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        private final TextView initial;
        private final TextView name;
        private final TextView memberCount;
        private final TextView balanceLabel;
        private final TextView balanceAmount;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            initial = itemView.findViewById(R.id.groupInitial);
            name = itemView.findViewById(R.id.groupName);
            memberCount = itemView.findViewById(R.id.groupMemberCount);
            balanceLabel = itemView.findViewById(R.id.groupBalanceLabel);
            balanceAmount = itemView.findViewById(R.id.groupBalanceAmount);
        }

        void bind(Group group, int members, OnGroupClickListener listener) {
            name.setText(group.getName());
            initial.setText(group.getName().isEmpty() ? ""
                    : group.getName().substring(0, 1).toUpperCase(Locale.US));
            memberCount.setText(itemView.getResources()
                    .getString(R.string.members_count, members));

            double balance = group.getCurrentUserBalance();
            int color;
            if (Math.abs(balance) < 0.01) {
                balanceLabel.setText(R.string.settled_up);
                balanceAmount.setText("");
                color = itemView.getResources().getColor(R.color.text_tertiary, null);
            } else if (balance < 0) {
                balanceLabel.setText(R.string.you_owe_label);
                balanceAmount.setText(Money.format(balance));
                color = itemView.getResources().getColor(R.color.color_error, null);
            } else {
                balanceLabel.setText(R.string.owed_label);
                balanceAmount.setText(Money.format(balance));
                color = itemView.getResources().getColor(R.color.primary, null);
            }
            balanceLabel.setTextColor(color);
            balanceAmount.setTextColor(color);

            itemView.setOnClickListener(v -> listener.onGroupClick(group));
        }
    }
}
