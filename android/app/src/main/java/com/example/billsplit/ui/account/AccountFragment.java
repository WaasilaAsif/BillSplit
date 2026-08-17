package com.example.billsplit.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.billsplit.R;
import com.example.billsplit.data.model.Friend;
import com.example.billsplit.data.model.Group;
import com.example.billsplit.data.model.Settlement;
import com.example.billsplit.data.model.User;
import com.example.billsplit.data.repository.ApiCallback;
import com.example.billsplit.data.repository.AuthRepository;
import com.example.billsplit.data.repository.FriendRepository;
import com.example.billsplit.data.repository.GroupRepository;
import com.example.billsplit.data.repository.SettlementRepository;
import com.example.billsplit.local.TokenManager;
import com.example.billsplit.ui.common.BottomNavFragment;
import com.example.billsplit.util.Money;

import java.util.List;
import java.util.Locale;

public class AccountFragment extends BottomNavFragment {

    private final GroupRepository groupRepository = new GroupRepository();
    private final FriendRepository friendRepository = new FriendRepository();
    private final SettlementRepository settlementRepository = new SettlementRepository();
    private final AuthRepository authRepository = new AuthRepository();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupBottomNav(view);
        renderProfileHeader(view);

        view.findViewById(R.id.settingsIcon).setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_global_settingsFragment));

        view.findViewById(R.id.paymentMethodsRow).setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_global_settingsFragment));
        view.findViewById(R.id.inviteFriendsRow).setOnClickListener(v -> shareInvite());
        view.findViewById(R.id.helpFeedbackRow).setOnClickListener(v ->
                Toast.makeText(requireContext(), R.string.coming_soon, Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.logOutRow).setOnClickListener(v -> logOut(view));

        renderStats(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) renderStats(getView());
    }

    private void renderProfileHeader(View view) {
        authRepository.getCurrentUser(new ApiCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (!isAdded()) return;
                String initial = user.getUsername().isEmpty() ? ""
                        : user.getUsername().substring(0, 1).toUpperCase(Locale.US);
                ((TextView) view.findViewById(R.id.profileInitial)).setText(initial);
                ((TextView) view.findViewById(R.id.profileNameText)).setText(user.getUsername());
                ((TextView) view.findViewById(R.id.profileEmailText)).setText(user.getEmail());
            }

            @Override
            public void onError(String message) {
                // Non-critical for this header — fail quietly, stats below still load independently.
            }
        });
    }

    private void renderStats(View view) {
        groupRepository.getGroups(new ApiCallback<List<Group>>() {
            @Override
            public void onSuccess(List<Group> groups) {
                if (isAdded()) ((TextView) view.findViewById(R.id.statGroupsText)).setText(String.valueOf(groups.size()));
            }

            @Override
            public void onError(String message) {
                // Stats are non-critical — fail quietly rather than toast-spamming this screen.
            }
        });

        friendRepository.getFriends(new ApiCallback<List<Friend>>() {
            @Override
            public void onSuccess(List<Friend> friends) {
                if (isAdded()) ((TextView) view.findViewById(R.id.statFriendsText)).setText(String.valueOf(friends.size()));
            }

            @Override
            public void onError(String message) {
            }
        });

        settlementRepository.getSettlements(new ApiCallback<List<Settlement>>() {
            @Override
            public void onSuccess(List<Settlement> settlements) {
                if (!isAdded()) return;
                double total = 0;
                for (Settlement s : settlements) total += s.getAmount();
                ((TextView) view.findViewById(R.id.statSettledText)).setText(Money.format(total));
            }

            @Override
            public void onError(String message) {
            }
        });
    }

    private void shareInvite() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invite_share_text,
                "https://billsplit.example.com/invite"));
        startActivity(Intent.createChooser(intent, getString(R.string.invite_friends)));
    }

    private void logOut(View view) {
        TokenManager.getInstance().clearToken();
        Toast.makeText(requireContext(), R.string.log_out, Toast.LENGTH_SHORT).show();
        NavOptions options = new NavOptions.Builder()
                .setPopUpTo(R.id.splashFragment, true)
                .build();
        Navigation.findNavController(view).navigate(R.id.loginFragment, null, options);
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_account;
    }
}
