package com.example.billsplit.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billsplit.R;
import com.example.billsplit.data.model.ActivityEvent;
import com.example.billsplit.data.repository.ActivityRepository;
import com.example.billsplit.ui.common.BottomNavFragment;

import java.util.ArrayList;
import java.util.List;

public class ActivityFeedFragment extends BottomNavFragment {

    private final ActivityRepository activityRepository = new ActivityRepository();
    private ActivityListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.activityRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ActivityListAdapter(false);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.notificationsIcon).setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_global_notificationsFragment));

        setupBottomNav(view);
        loadFeed();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFeed();
    }

    private void loadFeed() {
        List<ActivityEvent> events = activityRepository.getFeed();
        List<Object> items = new ArrayList<>();
        String lastSection = null;
        for (ActivityEvent event : events) {
            String section = ActivityTextFormatter.sectionFor(requireContext(), event.getCreatedAt());
            if (!section.equals(lastSection)) {
                items.add(section);
                lastSection = section;
            }
            items.add(event);
        }
        adapter.submitList(items);
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_activity;
    }
}
