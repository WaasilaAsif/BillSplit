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
import com.example.billsplit.data.model.Settlement;
import com.example.billsplit.data.model.User;
import com.example.billsplit.data.repository.FriendRepository;
import com.example.billsplit.data.repository.GroupRepository;
import com.example.billsplit.local.AppDataStore;
import com.example.billsplit.ui.common.BottomNavFragment;
import com.example.billsplit.util.Money;

import java.util.Locale;

public class AccountFragment extends BottomNavFragment {

    private final GroupRepository groupRepository = new GroupRepository();
    private final FriendRepository friendRepository = new FriendRepository();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        User currentUser = AppDataStore.getInstance().getCurrentUser();
        if (currentUser != null) {
            String initial = currentUser.getUsername().isEmpty() ? ""
                    : currentUser.getUsername().substring(0, 1).toUpperCase(Locale.US);
            ((TextView) view.findViewById(R.id.profileInitial)).setText(initial);
            ((TextView) view.findViewById(R.id.profileNameText)).setText(currentUser.getUsername());
            ((TextView) view.findViewById(R.id.profileEmailText)).setText(currentUser.getEmail());
        }

        view.findViewById(R.id.settingsIcon).setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_global_settingsFragment));
//Payment fragment not implemented yet
        view.findViewById(R.id.paymentMethodsRow).setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_global_settingsFragment));
        view.findViewById(R.id.inviteFriendsRow).setOnClickListener(v -> shareInvite());

        view.findViewById(R.id.logOutRow).setOnClickListener(v -> logOut(view));

        renderStats(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        renderStats(requireView());
    }

    private void renderStats(View view) {
        ((TextView) view.findViewById(R.id.statGroupsText))
                .setText(String.valueOf(groupRepository.getGroups().size()));
        ((TextView) view.findViewById(R.id.statFriendsText))
                .setText(String.valueOf(friendRepository.getFriends().size()));

        double settledTotal = 0;
        String userId = AppDataStore.CURRENT_USER_ID;
        //settled total can also be a good db level procedure
        for (Settlement s : AppDataStore.getInstance().getAllSettlements()) {
            if (userId.equals(s.getFromUserId()) || userId.equals(s.getToUserId())) {
                settledTotal += s.getAmount();
            }
        }
        ((TextView) view.findViewById(R.id.statSettledText)).setText(Money.format(settledTotal));
    }

    private void shareInvite() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invite_share_text,
                "https://billsplit.example.com/invite"));
        startActivity(Intent.createChooser(intent, getString(R.string.invite_friends)));
    }

    private void logOut(View view) {
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
