package com.example.billsplit.ui.onboarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billsplit.R;

class OnboardingPagerAdapter extends RecyclerView.Adapter<OnboardingPagerAdapter.PageViewHolder> {

    private static final int[] TITLES = {
            R.string.onboarding_title_1, R.string.onboarding_title_2, R.string.onboarding_title_3
    };
    private static final int[] SUBTITLES = {
            R.string.onboarding_subtitle_1, R.string.onboarding_subtitle_2, R.string.onboarding_subtitle_3
    };

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        holder.title.setText(TITLES[position]);
        holder.subtitle.setText(SUBTITLES[position]);
    }

    @Override
    public int getItemCount() {
        return TITLES.length;
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView subtitle;

        PageViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.pageTitle);
            subtitle = itemView.findViewById(R.id.pageSubtitle);
        }
    }
}
