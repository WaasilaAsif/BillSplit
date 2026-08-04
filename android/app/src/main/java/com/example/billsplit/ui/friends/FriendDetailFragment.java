package com.example.billsplit.ui.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billsplit.R;
import com.example.billsplit.data.model.Friend;
import com.example.billsplit.data.repository.FriendRepository;
import com.example.billsplit.local.AppDataStore;
import com.example.billsplit.ui.settle.SettleUpBottomSheetFragment;
import com.example.billsplit.util.Money;

import java.util.Locale;

public class FriendDetailFragment extends Fragment {

    private final FriendRepository friendRepository = new FriendRepository();
    private String friendId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        friendId = args != null ? args.getString("friendId") : null;

        view.findViewById(R.id.backButton).setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(view).navigateUp());

        RecyclerView recyclerView = view.findViewById(R.id.sharedExpensesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        SharedExpensesAdapter adapter = new SharedExpensesAdapter();
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.addExpenseFab).setOnClickListener(v -> {
            Bundle formArgs = new Bundle();
            androidx.navigation.Navigation.findNavController(view)
                    .navigate(R.id.action_global_expenseFormFragment, formArgs);
        });

        view.findViewById(R.id.settleUpButton).setOnClickListener(v -> {
            Friend friend = friendRepository.getFriend(friendId);
            if (friend == null || friend.isSettled()) return;
            String fromUserId = friend.isOwedToYou() ? friendId : AppDataStore.CURRENT_USER_ID;
            String toUserId = friend.isOwedToYou() ? AppDataStore.CURRENT_USER_ID : friendId;
            SettleUpBottomSheetFragment.newInstance(fromUserId, toUserId,
                            Math.abs(friend.getNetBalance()), null)
                    .show(getParentFragmentManager(), "settle_up");
        });

        render(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        RecyclerView recyclerView = getView() != null
                ? getView().findViewById(R.id.sharedExpensesRecyclerView) : null;
        if (recyclerView != null && recyclerView.getAdapter() instanceof SharedExpensesAdapter) {
            render((SharedExpensesAdapter) recyclerView.getAdapter());
        }
    }

    private void render(SharedExpensesAdapter adapter) {
        Friend friend = friendRepository.getFriend(friendId);
        if (friend == null) return;
        View view = requireView();

        String initial = friend.getUsername().isEmpty() ? ""
                : friend.getUsername().substring(0, 1).toUpperCase(Locale.US);
        ((TextView) view.findViewById(R.id.friendInitialText)).setText(initial);
        ((TextView) view.findViewById(R.id.friendNameText)).setText(friend.getUsername());

        TextView banner = view.findViewById(R.id.balanceBanner);
        if (friend.isSettled()) {
            banner.setText(R.string.all_settled_up);
            banner.setBackgroundTintList(getResources().getColorStateList(R.color.color_surface_variant, null));
            banner.setTextColor(getResources().getColor(R.color.text_secondary, null));
        } else if (friend.isOwedToYou()) {
            banner.setText(getString(R.string.friend_owes_you, friend.getUsername(),
                    Money.format(friend.getNetBalance())));
            banner.setBackgroundTintList(getResources().getColorStateList(R.color.color_primary_container, null));
            banner.setTextColor(getResources().getColor(R.color.primary, null));
        } else {
            banner.setText(getString(R.string.you_owe_friend, friend.getUsername(),
                    Money.format(friend.getNetBalance())));
            banner.setBackgroundTintList(getResources().getColorStateList(R.color.color_error_container, null));
            banner.setTextColor(getResources().getColor(R.color.color_error, null));
        }

        adapter.submitList(friendRepository.getSharedExpenses(friendId));
    }
}
