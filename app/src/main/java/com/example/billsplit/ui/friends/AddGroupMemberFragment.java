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
import com.example.billsplit.data.model.User;
import com.example.billsplit.data.repository.FriendRepository;
import com.example.billsplit.data.repository.GroupRepository;
import com.example.billsplit.local.AppDataStore;
import com.google.android.material.chip.ChipGroup;

import java.util.List;
import java.util.Locale;

public class AddGroupMemberFragment extends Fragment {

    private final GroupRepository groupRepository = new GroupRepository();
    private final FriendRepository friendRepository = new FriendRepository();

    private String groupId;
    private ChipGroup currentMembersGroup;
    private UserResultAdapter suggestedAdapter;

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
        suggestedAdapter = new UserResultAdapter(user -> {
            if (groupId == null) return;
            groupRepository.addMember(groupId, user);
            Toast.makeText(requireContext(), R.string.member_added, Toast.LENGTH_SHORT).show();
            renderCurrentMembers();
            renderSuggested("");
        });
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
                renderSuggested(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        renderCurrentMembers();
        renderSuggested("");
    }

    private void renderCurrentMembers() {
        if (groupId == null) return;
        currentMembersGroup.removeAllViews();
        List<User> members = groupRepository.getGroupMemberUsers(groupId);
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (User user : members) {
            View chip = inflater.inflate(R.layout.item_member_chip, currentMembersGroup, false);
            boolean isCurrentUser = AppDataStore.CURRENT_USER_ID.equals(user.getId());
            String name = isCurrentUser ? "You" : user.getUsername();

            ((TextView) chip.findViewById(R.id.chipName)).setText(name);
            ((TextView) chip.findViewById(R.id.chipInitial)).setText(name.isEmpty() ? ""
                    : name.substring(0, 1).toUpperCase(Locale.US));

            TextView remove = chip.findViewById(R.id.chipRemove);
            if (isCurrentUser) {
                remove.setVisibility(View.GONE);
            } else {
                remove.setOnClickListener(v -> {
                    groupRepository.removeMember(groupId, user.getId());
                    renderCurrentMembers();
                });
            }
            currentMembersGroup.addView(chip);
        }
    }

    private void renderSuggested(String query) {
        // FriendRepository.search short-circuits to empty on a blank query,
        // so the default "suggested" list is every non-member user instead.
        List<User> results = query.trim().isEmpty()
                ? suggestedNonMembers()
                : excludeGroupMembers(friendRepository.search(query));
        suggestedAdapter.submitList(results);
    }

    private List<User> suggestedNonMembers() {
        List<User> nonMembers = new java.util.ArrayList<>();
        List<User> members = groupRepository.getGroupMemberUsers(groupId);
        for (User u : AppDataStore.getInstance().getUsers()) {
            if (u.getId().equals(AppDataStore.CURRENT_USER_ID)) continue;
            if (!containsUser(members, u.getId())) nonMembers.add(u);
        }
        return nonMembers;
    }

    private List<User> excludeGroupMembers(List<User> users) {
        List<User> members = groupRepository.getGroupMemberUsers(groupId);
        List<User> filtered = new java.util.ArrayList<>();
        for (User u : users) {
            if (!containsUser(members, u.getId())) filtered.add(u);
        }
        return filtered;
    }

    private boolean containsUser(List<User> users, String userId) {
        for (User u : users) {
            if (u.getId().equals(userId)) return true;
        }
        return false;
    }
}
