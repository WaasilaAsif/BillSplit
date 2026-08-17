package com.example.billsplit.ui.friends;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.example.billsplit.R;
import com.example.billsplit.data.model.Friend;
import com.example.billsplit.data.repository.ApiCallback;
import com.example.billsplit.data.repository.FriendRepository;
import com.example.billsplit.ui.common.BottomNavFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FriendsFragment extends BottomNavFragment {

    private final FriendRepository friendRepository = new FriendRepository();
    private FriendsAdapter adapter;
    private List<Friend> allFriends = new ArrayList<>();

    private View emptyFriendsGroup;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.friendsRecyclerView);
        emptyFriendsGroup = view.findViewById(R.id.emptyFriendsGroup);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new FriendsAdapter(this::openFriend);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.addFriendIcon).setOnClickListener(v -> openAddFriend());
        view.findViewById(R.id.addFriendFab).setOnClickListener(v -> openAddFriend());
        view.findViewById(R.id.emptyAddFriendButton).setOnClickListener(v -> openAddFriend());

        android.widget.EditText searchInput = view.findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        setupBottomNav(view);
        loadFriends();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFriends();
    }

    private void loadFriends() {
        friendRepository.getFriends(new ApiCallback<List<Friend>>() {
            @Override
            public void onSuccess(List<Friend> friends) {
                allFriends = friends;
                filter("");
            }

            @Override
            public void onError(String message) {
                if (isAdded()) Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filter(String query) {
        List<Friend> filtered;
        if (query.trim().isEmpty()) {
            filtered = allFriends;
        } else {
            String needle = query.trim().toLowerCase(Locale.US);
            filtered = new ArrayList<>();
            for (Friend f : allFriends) {
                if (f.getUsername().toLowerCase(Locale.US).contains(needle)) {
                    filtered.add(f);
                }
            }
        }
        adapter.submitList(filtered);
        boolean showEmpty = allFriends.isEmpty();
        emptyFriendsGroup.setVisibility(showEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(showEmpty ? View.GONE : View.VISIBLE);
    }

    private void openFriend(Friend friend) {
        Bundle args = new Bundle();
        args.putString("friendId", friend.getUserId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_global_friendDetailFragment, args);
    }

    private void openAddFriend() {
        Navigation.findNavController(requireView()).navigate(R.id.action_global_addFriendFragment);
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_friends;
    }
}
