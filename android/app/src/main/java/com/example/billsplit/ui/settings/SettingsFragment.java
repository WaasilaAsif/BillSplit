package com.example.billsplit.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.billsplit.R;

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

        view.findViewById(R.id.settingsLogOutRow).setOnClickListener(v -> {
            NavOptions options = new NavOptions.Builder()
                    .setPopUpTo(R.id.splashFragment, true)
                    .build();
            Navigation.findNavController(view).navigate(R.id.loginFragment, null, options);
        });
    }
}
