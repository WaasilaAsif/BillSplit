package com.example.billsplit.ui.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billsplit.R;
import com.example.billsplit.data.model.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

/** Mixed list of section-header strings and ActivityEvent rows, used by both
 * the Activity Feed and Notifications screens. */
public class ActivityListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_EVENT = 1;

    private final List<Object> items = new ArrayList<>();
    private final boolean showUnreadStyling;

    public ActivityListAdapter(boolean showUnreadStyling) {
        this.showUnreadStyling = showUnreadStyling;
    }

    public void submitList(List<Object> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_HEADER : TYPE_EVENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(inflater.inflate(R.layout.item_section_header, parent, false));
        }
        return new EventViewHolder(inflater.inflate(R.layout.item_activity_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).text.setText((String) item);
        } else {
            ((EventViewHolder) holder).bind((ActivityEvent) item, showUnreadStyling);
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

    static class EventViewHolder extends RecyclerView.ViewHolder {
        final View dot;
        final TextView text;
        final TextView time;
        final View root;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.activityRowRoot);
            dot = itemView.findViewById(R.id.unreadDot);
            text = itemView.findViewById(R.id.activityText);
            time = itemView.findViewById(R.id.activityTimeText);
        }

        void bind(ActivityEvent event, boolean showUnreadStyling) {
            text.setText(ActivityTextFormatter.describe(itemView.getContext(), event));
            time.setText(ActivityTextFormatter.relativeTime(itemView.getContext(), event.getCreatedAt()));

            boolean unread = showUnreadStyling && !event.isRead();
            dot.setVisibility(unread ? View.VISIBLE : View.GONE);
            root.setBackgroundTintList(itemView.getResources().getColorStateList(
                    unread ? R.color.color_unread_highlight : android.R.color.transparent, null));
        }
    }
    //abcs
}
