package com.example.billsplit.ui.friends;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billsplit.R;
import com.example.billsplit.data.model.GroupDetail;
import com.example.billsplit.data.model.GroupMember;
import com.example.billsplit.data.model.User;
import com.example.billsplit.data.repository.ApiCallback;
import com.example.billsplit.data.repository.GroupRepository;
import com.example.billsplit.data.repository.UserRepository;
import com.example.billsplit.local.TokenManager;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddGroupMemberFragment extends Fragment {

    private final GroupRepository groupRepository = new GroupRepository();
    private final UserRepository userRepository = new UserRepository();

    private String groupId;
    private ChipGroup currentMembersGroup;
    private UserResultAdapter suggestedAdapter;
    private List<GroupMember> currentMembers = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_group_member, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        groupId = args != null ? args.getString("groupId") : null;

        currentMembersGroup = view.findViewById(R.id.currentMembersGroup);

        RecyclerView recyclerView = view.findViewById(R.id.suggestedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        suggestedAdapter = new UserResultAdapter(this::addMember);
        recyclerView.setAdapter(suggestedAdapter);

        view.findViewById(R.id.backButton).setOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());
        view.findViewById(R.id.doneButton).setOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        android.widget.EditText searchInput = view.findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchSuggested(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        loadCurrentMembers();
    }

    private void addMember(User user) {
        if (groupId == null) return;
        groupRepository.addMember(groupId, user, new ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), R.string.member_added, Toast.LENGTH_SHORT).show();
                loadCurrentMembers();
            }

            @Override
            public void onError(String message) {
                if (isAdded()) Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCurrentMembers() {
        if (groupId == null) return;
        groupRepository.getGroupDetail(groupId, new ApiCallback<GroupDetail>() {
            @Override
            public void onSuccess(GroupDetail detail) {
                if (!isAdded()) return;
                currentMembers = detail.getMembers();
                renderCurrentMembers();
                suggestedAdapter.submitList(new ArrayList<>());
            }

            @Override
            public void onError(String message) {
                if (isAdded()) Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderCurrentMembers() {
        currentMembersGroup.removeAllViews();
        String currentUserId = TokenManager.getInstance().getCurrentUserId();
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (GroupMember member : currentMembers) {
            View chip = inflater.inflate(R.layout.item_member_chip, currentMembersGroup, false);
            boolean isCurrentUser = member.getUserId().equals(currentUserId);
            String name = isCurrentUser ? "You" : member.getUsername();

            ((TextView) chip.findViewById(R.id.chipName)).setText(name);
            ((TextView) chip.findViewById(R.id.chipInitial)).setText(name.isEmpty() ? ""
                    : name.substring(0, 1).toUpperCase(Locale.US));

            TextView remove = chip.findViewById(R.id.chipRemove);
            if (isCurrentUser) {
                remove.setVisibility(View.GONE);
            } else {
                remove.setOnClickListener(v -> removeMember(member.getUserId()));
            }
            currentMembersGroup.addView(chip);
        }
    }

    private void removeMember(String userId) {
        groupRepository.removeMember(groupId, userId, new ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (isAdded()) loadCurrentMembers();
            }

            @Override
            public void onError(String message) {
                if (isAdded()) Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchSuggested(String query) {
        if (query.trim().isEmpty()) {
            suggestedAdapter.submitList(new ArrayList<>());
            return;
        }
        userRepository.search(query, new ApiCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> results) {
                if (isAdded()) suggestedAdapter.submitList(excludeCurrentMembers(results));
            }

            @Override
            public void onError(String message) {
                if (isAdded()) Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<User> excludeCurrentMembers(List<User> users) {
        List<User> filtered = new ArrayList<>();
        for (User u : users) {
            boolean isMember = false;
            for (GroupMember m : currentMembers) {
                if (m.getUserId().equals(u.getId())) {
                    isMember = true;
                    break;
                }
            }
            if (!isMember) filtered.add(u);
        }
        return filtered;
    }
}
