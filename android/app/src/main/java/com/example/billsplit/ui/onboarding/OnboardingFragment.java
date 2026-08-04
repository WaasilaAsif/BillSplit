package com.example.billsplit.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.billsplit.R;

public class OnboardingFragment extends Fragment {

    private static final int PAGE_COUNT = 3;

    private ViewPager2 pager;
    private Button nextButton;
    private View[] dots;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pager = view.findViewById(R.id.onboardingPager);
        nextButton = view.findViewById(R.id.nextButton);
        TextView skipText = view.findViewById(R.id.skipText);
        dots = new View[]{
                view.findViewById(R.id.dot0),
                view.findViewById(R.id.dot1),
                view.findViewById(R.id.dot2)
        };

        pager.setAdapter(new OnboardingPagerAdapter());
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
                nextButton.setText(position == PAGE_COUNT - 1 ? getString(R.string.get_started)
                        : getString(R.string.next));
            }
        });

        skipText.setOnClickListener(v -> goToLogin());
        nextButton.setOnClickListener(v -> {
            int current = pager.getCurrentItem();
            if (current == PAGE_COUNT - 1) {
                goToLogin();
            } else {
                pager.setCurrentItem(current + 1, true);
            }
        });
    }

    private void updateDots(int selected) {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setBackgroundTintList(getResources().getColorStateList(
                    i == selected ? R.color.primary : R.color.color_neutral_variant, null));
        }
    }

    private void goToLogin() {
        Navigation.findNavController(requireView())
                .navigate(R.id.action_onboardingFragment_to_loginFragment);
    }
}
