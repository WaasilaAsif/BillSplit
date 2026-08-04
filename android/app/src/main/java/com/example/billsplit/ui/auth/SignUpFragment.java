package com.example.billsplit.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.billsplit.R;

public class SignUpFragment extends Fragment {

    private SignUpViewModel viewModel;

    private EditText fullNameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private CheckBox termsCheckbox;
    private Button createAccountButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        View backButton = view.findViewById(R.id.backButton);
        fullNameInput = view.findViewById(R.id.fullNameInput);
        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        confirmPasswordInput = view.findViewById(R.id.confirmPasswordInput);
        termsCheckbox = view.findViewById(R.id.termsCheckbox);
        createAccountButton = view.findViewById(R.id.createAccountButton);
        TextView logInLink = view.findViewById(R.id.logInLink);

        backButton.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());
        logInLink.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        createAccountButton.setOnClickListener(v -> viewModel.register(
                fullNameInput.getText().toString().trim(),
                emailInput.getText().toString().trim(),
                passwordInput.getText().toString(),
                confirmPasswordInput.getText().toString(),
                termsCheckbox.isChecked()));

        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            createAccountButton.setEnabled(!isLoading);
            createAccountButton.setText(isLoading ? getString(R.string.creating_account)
                    : getString(R.string.create_account_button));
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getSignUpSuccess().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                Toast.makeText(requireContext(), "Welcome, " + user.getUsername() + "!",
                        Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view)
                        .navigate(R.id.action_signUpFragment_to_groupsDashboardFragment);
            }
        });
    }
}
