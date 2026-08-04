package com.example.billsplit.ui.friends;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billsplit.R;
import com.example.billsplit.data.repository.FriendRepository;

public class AddFriendFragment extends Fragment {

    private final FriendRepository friendRepository = new FriendRepository();
    private UserResultAdapter adapter;
    private View resultsGroup;
    private View noResultsGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resultsGroup = view.findViewById(R.id.resultsGroup);
        noResultsGroup = view.findViewById(R.id.noResultsGroup);

        RecyclerView recyclerView = view.findViewById(R.id.resultsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new UserResultAdapter(user -> {
            friendRepository.addFriend(user.getId());
            Toast.makeText(requireContext(), R.string.friend_added, Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp();
        });
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.backButton).setOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        view.findViewById(R.id.inviteViaLinkButton).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invite_share_text,
                    "https://billsplit.example.com/invite"));
            startActivity(Intent.createChooser(intent, getString(R.string.invite_via_link)));
        });

        android.widget.EditText searchInput = view.findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        search("");
    }

    private void search(String query) {
        if (query.trim().isEmpty()) {
            resultsGroup.setVisibility(View.GONE);
            noResultsGroup.setVisibility(View.GONE);
            return;
        }
        var results = friendRepository.search(query);
        adapter.submitList(results);
        resultsGroup.setVisibility(results.isEmpty() ? View.GONE : View.VISIBLE);
        noResultsGroup.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
