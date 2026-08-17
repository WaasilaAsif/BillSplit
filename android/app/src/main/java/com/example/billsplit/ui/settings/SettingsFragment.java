package com.example.billsplit.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.billsplit.R;
import com.example.billsplit.local.TokenManager;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.backButton).setOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        View.OnClickListener notBuiltYet = v -> Toast.makeText(requireContext(),
                R.string.coming_soon, Toast.LENGTH_SHORT).show();
        view.findViewById(R.id.editProfileRow).setOnClickListener(notBuiltYet);
        view.findViewById(R.id.changePasswordRow).setOnClickListener(notBuiltYet);
        view.findViewById(R.id.privacyPolicyRow).setOnClickListener(notBuiltYet);
        view.findViewById(R.id.termsOfServiceRow).setOnClickListener(notBuiltYet);

        view.findViewById(R.id.settingsLogOutRow).setOnClickListener(v -> {
            TokenManager.getInstance().clearToken();
            NavOptions options = new NavOptions.Builder()
                    .setPopUpTo(R.id.splashFragment, true)
                    .build();
            Navigation.findNavController(view).navigate(R.id.loginFragment, null, options);
        });
    }
}
