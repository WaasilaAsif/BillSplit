package com.example.billsplit.ui.common;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.billsplit.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public abstract class BottomNavFragment extends Fragment {

    @IdRes
    protected abstract int getSelectedNavItemId();

    protected void setupBottomNav(View root) {
        BottomNavigationView bottomNav = root.findViewById(R.id.bottomNav);
        if (bottomNav == null) return;

        bottomNav.setSelectedItemId(getSelectedNavItemId());
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == getSelectedNavItemId()) return true;

            int destinationId = destinationFor(itemId);
            if (destinationId == 0) return false;

            NavOptions options = new NavOptions.Builder()
                    .setPopUpTo(R.id.groupsDashboardFragment, false)
                    .setLaunchSingleTop(true)
                    .build();
            Navigation.findNavController(root).navigate(destinationId, null, options);
            return true;
        });
    }

    @IdRes
    private int destinationFor(@IdRes int navItemId) {
        if (navItemId == R.id.nav_groups) return R.id.groupsDashboardFragment;
        if (navItemId == R.id.nav_friends) return R.id.friendsFragment;
        if (navItemId == R.id.nav_activity) return R.id.activityFeedFragment;
        if (navItemId == R.id.nav_account) return R.id.accountFragment;
        return 0;
    }
}
