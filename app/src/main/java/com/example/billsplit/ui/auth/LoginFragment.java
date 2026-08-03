package com.example.billsplit.ui.auth;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.billsplit.R;

public class LoginFragment extends Fragment {

    private LoginViewModel viewModel;

    private EditText emailInput;
    private EditText passwordInput;
    private TextView showHideToggle;
    private Button loginButton;
    private TextView signUpLink;
    private boolean passwordVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        showHideToggle = view.findViewById(R.id.showHideToggle);
        loginButton = view.findViewById(R.id.loginButton);
        signUpLink = view.findViewById(R.id.signUpLink);

        showHideToggle.setOnClickListener(v -> togglePasswordVisibility());

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            viewModel.login(email, password);
        });

        signUpLink.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_signUpFragment)
        );

        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loginButton.setEnabled(!isLoading);
            loginButton.setText(isLoading ? getString(R.string.logging_in) : getString(R.string.log_in));
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getLoginSuccess().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_loginFragment_to_groupsDashboardFragment);
            }
        });
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        int selection = passwordInput.getSelectionEnd();
        passwordInput.setInputType(passwordVisible
                ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        showHideToggle.setText(passwordVisible ? getString(R.string.hide) : getString(R.string.show));
        if (selection >= 0) {
            passwordInput.setSelection(selection);
        }
    }
}