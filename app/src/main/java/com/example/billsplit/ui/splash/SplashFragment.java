package com.example.billsplit.ui.splash;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.billsplit.R;

public class SplashFragment extends Fragment {

    private static final long DISPLAY_DURATION_MS = 1200;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable advance = this::navigateToOnboarding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler.postDelayed(advance, DISPLAY_DURATION_MS);
    }

    private void navigateToOnboarding() {
        if (!isAdded()) return;
        Navigation.findNavController(requireView())
                .navigate(R.id.action_splashFragment_to_onboardingFragment);
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacks(advance);
        super.onDestroyView();
    }
}
