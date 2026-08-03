package com.example.billsplit.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billsplit.R;
import com.example.billsplit.data.model.ActivityEvent;
import com.example.billsplit.data.repository.ActivityRepository;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private final ActivityRepository activityRepository = new ActivityRepository();
    private ActivityListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.notificationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ActivityListAdapter(true);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.backButton).setOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());
        view.findViewById(R.id.markAllReadText).setOnClickListener(v -> {
            activityRepository.markAllRead();
            loadNotifications();
        });

        loadNotifications();
    }

    private void loadNotifications() {
        List<ActivityEvent> events = activityRepository.getNotifications();
        List<Object> items = new ArrayList<>(events);
        adapter.submitList(items);
    }
}
